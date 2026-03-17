package just.spinner

/** Handle to a spinner instance that can be started, stopped, and updated.
  */
trait SpinnerHandle {

  def text: String
  def updateText(newText: String): Unit
  def updateColor(newColor: Color): Unit
  def updateNoColor(): Unit
  def updatePrefixText(newText: String): Unit
  def updateSuffixText(newText: String): Unit
  def updateSpinnerType(newSpinnerType: SpinnerType): Unit
  def updateIndent(newIndent: Int): Unit

  def isSpinning: Boolean

  def start(): SpinnerHandle
  def stop(): SpinnerHandle
  def succeed(text: Option[String]): SpinnerHandle
  def fail(text: Option[String]): SpinnerHandle
  def warn(text: Option[String]): SpinnerHandle
  def info(text: Option[String]): SpinnerHandle
  def stopAndPersist(options: PersistOptions): SpinnerHandle

  /** Print a line cleanly while spinner is running (cooperative hooking).
    * Clears the spinner, writes the message, then re-renders the spinner.
    */
  def println(msg: String): Unit

  def frame(): String
  def clear(): SpinnerHandle
  def render(): SpinnerHandle

  def getCurrentConfig: SpinnerConfig

}
object SpinnerHandle {

  def apply(
    initialConfig: SpinnerConfig,
    output: TerminalOutput,
    timer: SpinnerTimer,
  ): SpinnerHandle = new DefaultSpinnerHandle(initialConfig, output, timer)

  import java.util.concurrent.atomic.AtomicReference

  /** Default SpinnerHandle implementation using AtomicReference for thread-safe mutable state.
    */
  final private class DefaultSpinnerHandle(
    initialConfig: SpinnerConfig,
    output: TerminalOutput,
    timer: SpinnerTimer,
  ) extends SpinnerHandle {

    private val stateRef: AtomicReference[SpinnerState] = new AtomicReference(SpinnerState.initial)

    private val configRef: AtomicReference[SpinnerConfig] = new AtomicReference(initialConfig)

    private val cancelTokenRef: AtomicReference[Option[SpinnerTimer.CancelToken]] = new AtomicReference(None)

    private val unicodeSupported: Boolean = UnicodeSupport.isSupported

    private val interactive: Boolean = initialConfig.isEnabled.getOrElse(IsInteractive.check(output))

    def text: String = configRef.get().text.getOrElse("")

    def updateText(newText: String): Unit =
      updateConfig(_.withText(newText))

    def updateColor(newColor: Color): Unit =
      updateConfig(_.withColor(newColor))

    def updateNoColor(): Unit =
      updateConfig(_.withNoColor)

    def updatePrefixText(newText: String): Unit =
      updateConfig(_.withPrefixText(newText))

    def updateSuffixText(newText: String): Unit =
      updateConfig(_.withSuffixText(newText))

    def updateSpinnerType(newSpinnerType: SpinnerType): Unit = {
      updateConfig(_.withSpinnerType(newSpinnerType))
      updateState(_.copy(frameIndex = -1))
    }

    def updateIndent(newIndent: Int): Unit =
      updateConfig(_.withIndent(newIndent))

    def isSpinning: Boolean = stateRef.get().isRunning

    def start(): SpinnerHandle = {
      val config = configRef.get()
      if (config.isSilent) {
        this
      } else if (!interactive) {
        /* Non-interactive: just write static line */
        val symbol  = config.text.map(_ => "-").getOrElse("")
        val line    = buildOutputLine(symbol, config.text, config.prefixText, config.suffixText, config.indent)
        val trimmed = line.trim
        if (trimmed.nonEmpty) {
          output.write(line + "\n")
        }
        this
      } else if (isSpinning) {
        this
      } else {
        if (config.hideCursor) {
          output.write(AnsiCode.cursorHide)
        }
        updateState(_.copy(isRunning = true))
        val _     = render()
        val token = timer.scheduleAtFixedRate(config.spinnerType.interval) {
          if (isSpinning) {
            val _ = render()
          }
        }
        cancelTokenRef.set(Some(token))
        this
      }
    }

    def stop(): SpinnerHandle = {
      casGetAndSet(cancelTokenRef, None).foreach(_.cancel())
      updateState(_.copy(isRunning = false, frameIndex = -1, lastFrameTime = 0L))
      if (interactive) {
        val _      = clear()
        val config = configRef.get()
        if (config.hideCursor) {
          output.write(AnsiCode.cursorShow)
        }
      }
      this
    }

    def succeed(text: Option[String]): SpinnerHandle = {
      val symbol = LogSymbol.colored(LogSymbol.Success, unicodeSupported)
      stopAndPersist(PersistOptions(Some(symbol), text, None, None))
    }

    def fail(text: Option[String]): SpinnerHandle = {
      val symbol = LogSymbol.colored(LogSymbol.Error, unicodeSupported)
      stopAndPersist(PersistOptions(Some(symbol), text, None, None))
    }

    def warn(text: Option[String]): SpinnerHandle = {
      val symbol = LogSymbol.colored(LogSymbol.Warning, unicodeSupported)
      stopAndPersist(PersistOptions(Some(symbol), text, None, None))
    }

    def info(text: Option[String]): SpinnerHandle = {
      val symbol = LogSymbol.colored(LogSymbol.Info, unicodeSupported)
      stopAndPersist(PersistOptions(Some(symbol), text, None, None))
    }

    def stopAndPersist(options: PersistOptions): SpinnerHandle = {
      val config = configRef.get()
      if (config.isSilent) {
        this
      } else {
        val symbol    = options.symbol.getOrElse(" ")
        val finalText = options.text.orElse(config.text)
        val prefix    = options.prefixText.orElse(config.prefixText)
        val suffix    = options.suffixText.orElse(config.suffixText)
        val line      = buildOutputLine(symbol, finalText, prefix, suffix, config.indent)
        val _         = stop()
        output.write(line + "\n")
        this
      }
    }

    @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
    def println(msg: String): Unit =
      if (isSpinning) {
        (clear(): Unit)
        output.write(msg + "\n")
        (render(): Unit)
      } else {
        output.write(msg + "\n")
      }

    def frame(): String = {
      val config    = configRef.get()
      val state     = advanceFrame(config)
      val frames    = config.spinnerType.frames
      val idx       = state.frameIndex
      val frameChar = frames.lift(idx).getOrElse("")

      val coloredFrame = config.color.fold(frameChar)(c => AnsiCode.color(frameChar, c))

      val prefixStr = config.prefixText.filter(_.nonEmpty).map(_ + " ").getOrElse("")
      val textStr   = config.text.map(t => " " + t).getOrElse("")
      val suffixStr = config.suffixText.filter(_.nonEmpty).map(s => " " + s).getOrElse("")
      val indentStr = " " * config.indent

      indentStr + prefixStr + coloredFrame + textStr + suffixStr
    }

    def clear(): SpinnerHandle = {
      if (interactive) {
        val linesToClear = stateRef.get().linesToClear
        output.write(AnsiCode.cursorTo(0))
        (0 until linesToClear).foreach { i =>
          if (i > 0) {
            output.write(AnsiCode.moveUp(1))
          }
          output.write(AnsiCode.eraseLine)
        }
        val config       = configRef.get()
        if (config.indent > 0) {
          output.write(AnsiCode.cursorTo(config.indent))
        }
        updateState(_.copy(linesToClear = 0))
      }
      this
    }

    def render(): SpinnerHandle = {
      if (!interactive) {
        this
      } else {
        val useSyncOutput = interactive
        if (useSyncOutput) {
          output.write(AnsiCode.syncOutputEnable)
        }

        val _            = clear()
        val frameContent = frame()
        val columns      = output.columns.getOrElse(80)
        val lineCount    = computeLineCount(frameContent, columns)
        output.write(frameContent)
        updateState(_.copy(linesToClear = lineCount))

        if (useSyncOutput) {
          output.write(AnsiCode.syncOutputDisable)
        }
        this
      }
    }

    def getCurrentConfig: SpinnerConfig = configRef.get()

    private def advanceFrame(config: SpinnerConfig): SpinnerState = {
      val now = System.currentTimeMillis()
      casUpdateAndGet(stateRef) { current =>
        if (current.frameIndex < 0 || (now - current.lastFrameTime) >= config.spinnerType.interval.toMillis) {
          val nextIndex = (current.frameIndex + 1) % math.max(1, config.spinnerType.frames.length)
          current.copy(frameIndex = nextIndex, lastFrameTime = now)
        } else {
          current
        }
      }
    }

    private def computeLineCount(text: String, columns: Int): Int = {
      val stripped = StringWidth.stripAnsi(text)
      stripped.split('\n').foldLeft(0) { (count, line) =>
        count + math.max(1, math.ceil(StringWidth.calculate(line).toDouble / columns.toDouble).toInt)
      }
    }

    private def buildOutputLine(
      symbol: String,
      text: Option[String],
      prefixText: Option[String],
      suffixText: Option[String],
      indent: Int,
    ): String = {
      val prefixStr    = prefixText.filter(_.nonEmpty).map(_ + " ").getOrElse("")
      val separatorStr = if (symbol.nonEmpty) " " else ""
      val textStr      = text.map(t => separatorStr + t).getOrElse("")
      val suffixStr    = suffixText.filter(_.nonEmpty).map(s => " " + s).getOrElse("")
      val indentStr    = " " * indent

      indentStr + prefixStr + symbol + textStr + suffixStr
    }

    private def updateConfig(f: SpinnerConfig => SpinnerConfig): Unit = {
      val _ = casUpdateAndGet(configRef)(f)
    }

    private def updateState(f: SpinnerState => SpinnerState): Unit = {
      val _ = casUpdateAndGet(stateRef)(f)
    }

    @scala.annotation.tailrec
    private def casUpdateAndGet[A](ref: AtomicReference[A])(f: A => A): A = {
      val current = ref.get()
      val updated = f(current)
      if (ref.compareAndSet(current, updated)) updated
      else casUpdateAndGet(ref)(f)
    }

    @scala.annotation.tailrec
    private def casGetAndSet[A](ref: AtomicReference[A], newValue: A): A = {
      val current = ref.get()
      if (ref.compareAndSet(current, newValue)) current
      else casGetAndSet(ref, newValue)
    }

  }

}
