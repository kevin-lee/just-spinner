package just.spinner

import cats.Monad
import cats.syntax.all.*
import effectie.core.FxCtor
import effectie.syntax.all.*

/** Handle to a spinner instance that can be started, stopped, and updated.
  *
  * All operations are parameterized by effect type `F[*]`.
  * For synchronous (side-effecting) usage, use `F = cats.Id`.
  */
trait SpinnerHandle[F[*]] {

  def text: F[String]
  def updateText(newText: String): F[Unit]
  def updateColor(newColor: Color): F[Unit]
  def updateNoColor(): F[Unit]
  def updatePrefixText(newText: String): F[Unit]
  def updateSuffixText(newText: String): F[Unit]
  def updateSpinnerType(newSpinnerType: SpinnerType): F[Unit]
  def updateIndent(newIndent: Int): F[Unit]

  def isSpinning: F[Boolean]

  def start(): F[SpinnerHandle[F]]
  def stop(): F[SpinnerHandle[F]]
  def succeed(text: Option[String]): F[SpinnerHandle[F]]
  def fail(text: Option[String]): F[SpinnerHandle[F]]
  def warn(text: Option[String]): F[SpinnerHandle[F]]
  def info(text: Option[String]): F[SpinnerHandle[F]]
  def stopAndPersist(options: PersistOptions): F[SpinnerHandle[F]]

  /** Print a line cleanly while spinner is running (cooperative hooking).
    * Clears the spinner, writes the message, then re-renders the spinner.
    */
  def println(msg: String): F[Unit]

  def frame(): F[String]
  def clear(): F[SpinnerHandle[F]]
  def render(): F[SpinnerHandle[F]]

  def getCurrentConfig: F[SpinnerConfig]

}
object SpinnerHandle {

  def apply[F[*]: Monad: FxCtor](
    initialConfig: SpinnerConfig,
    output: TerminalOutput[F],
    timer: SpinnerTimer[F],
    mkRef: SpinnerRefMaker[F],
  ): F[SpinnerHandle[F]] =
    for {
      stateRef       <- mkRef(SpinnerState.initial)
      configRef      <- mkRef(initialConfig)
      cancelTokenRef <- mkRef(Option.empty[SpinnerTimer.CancelToken[F]])
      unicodeSupp    <- effectOf(UnicodeSupport.fromPlatformUnicodeSupport)
      interactive    <- IsInteractive.check[F](output).map(initialConfig.isEnabled.getOrElse(_))
    } yield new DefaultSpinnerHandle[F](stateRef, configRef, cancelTokenRef, output, timer, unicodeSupp, interactive)

  /** Default SpinnerHandle implementation using SpinnerRef for thread-safe mutable state.
    */
  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  final private class DefaultSpinnerHandle[F[*]: Monad: FxCtor](
    stateRef: SpinnerRef[F, SpinnerState],
    configRef: SpinnerRef[F, SpinnerConfig],
    cancelTokenRef: SpinnerRef[F, Option[SpinnerTimer.CancelToken[F]]],
    output: TerminalOutput[F],
    timer: SpinnerTimer[F],
    unicodeSupport: UnicodeSupport,
    interactive: Boolean,
  ) extends SpinnerHandle[F] {

    private val self: SpinnerHandle[F] = this

    def text: F[String] = configRef.get.map(_.text.getOrElse(""))

    def updateText(newText: String): F[Unit] =
      updateConfig(_.withText(newText))

    def updateColor(newColor: Color): F[Unit] =
      updateConfig(_.withColor(newColor))

    def updateNoColor(): F[Unit] =
      updateConfig(_.withNoColor)

    def updatePrefixText(newText: String): F[Unit] =
      updateConfig(_.withPrefixText(newText))

    def updateSuffixText(newText: String): F[Unit] =
      updateConfig(_.withSuffixText(newText))

    def updateSpinnerType(newSpinnerType: SpinnerType): F[Unit] =
      for {
        _ <- updateConfig(_.withSpinnerType(newSpinnerType))
        _ <- stateRef.update(_.copy(frameIndex = -1))
      } yield ()

    def updateIndent(newIndent: Int): F[Unit] =
      updateConfig(_.withIndent(newIndent))

    def isSpinning: F[Boolean] = stateRef.get.map(_.isRunning)

    def start(): F[SpinnerHandle[F]] =
      for {
        config <- configRef.get
        result <-
          if (config.isSilent) {
            Monad[F].pure(self)
          } else if (!interactive) {
            val symbol  = config.text.map(_ => "-").getOrElse("")
            val line    = buildOutputLine(symbol, config.text, config.prefixText, config.suffixText, config.indent)
            val trimmed = line.trim
            if (trimmed.nonEmpty)
              output.write(line + "\n").as(self)
            else
              Monad[F].pure(self)
          } else {
            for {
              spinning <- isSpinning
              result   <-
                if (spinning) {
                  Monad[F].pure(self)
                } else {
                  for {
                    _     <- if (config.hideCursor) output.write(AnsiCode.cursorHide) else Monad[F].unit
                    _     <- stateRef.update(_.copy(isRunning = true))
                    _     <- render()
                    token <- timer.scheduleAtFixedRate(config.spinnerType.interval) {
                               isSpinning.flatMap { s =>
                                 if (s) render().void else Monad[F].unit
                               }
                             }
                    _     <- cancelTokenRef.set(Some(token))
                  } yield self
                }
            } yield result
          }
      } yield result

    def stop(): F[SpinnerHandle[F]] =
      for {
        oldToken <- cancelTokenRef.getAndSet(None)
        _        <- oldToken.traverse_(_.cancel())
        _        <- stateRef.update(_.copy(isRunning = false, frameIndex = -1, lastFrameTime = 0L))
        _        <-
          if (interactive)
            for {
              _      <- clear()
              config <- configRef.get
              _      <- if (config.hideCursor) output.write(AnsiCode.cursorShow) else Monad[F].unit
            } yield ()
          else
            Monad[F].unit
      } yield self

    def succeed(text: Option[String]): F[SpinnerHandle[F]] = {
      val symbol = LogSymbol.colored(LogSymbol.Success, unicodeSupport)
      stopAndPersist(PersistOptions(Some(symbol), text, None, None))
    }

    def fail(text: Option[String]): F[SpinnerHandle[F]] = {
      val symbol = LogSymbol.colored(LogSymbol.Error, unicodeSupport)
      stopAndPersist(PersistOptions(Some(symbol), text, None, None))
    }

    def warn(text: Option[String]): F[SpinnerHandle[F]] = {
      val symbol = LogSymbol.colored(LogSymbol.Warning, unicodeSupport)
      stopAndPersist(PersistOptions(Some(symbol), text, None, None))
    }

    def info(text: Option[String]): F[SpinnerHandle[F]] = {
      val symbol = LogSymbol.colored(LogSymbol.Info, unicodeSupport)
      stopAndPersist(PersistOptions(Some(symbol), text, None, None))
    }

    def stopAndPersist(options: PersistOptions): F[SpinnerHandle[F]] =
      for {
        config <- configRef.get
        result <-
          if (config.isSilent) {
            Monad[F].pure(self)
          } else {
            val symbol    = options.symbol.getOrElse(" ")
            val finalText = options.text.orElse(config.text)
            val prefix    = options.prefixText.orElse(config.prefixText)
            val suffix    = options.suffixText.orElse(config.suffixText)
            val line      = buildOutputLine(symbol, finalText, prefix, suffix, config.indent)
            for {
              _ <- stop()
              _ <- output.write(line + "\n")
            } yield self
          }
      } yield result

    def println(msg: String): F[Unit] =
      for {
        spinning <- isSpinning
        _        <-
          if (spinning)
            for {
              _ <- clear()
              _ <- output.write(msg + "\n")
              _ <- render()
            } yield ()
          else
            output.write(msg + "\n")
      } yield ()

    def frame(): F[String] =
      for {
        config <- configRef.get
        state  <- advanceFrame(config)
      } yield {
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

    def clear(): F[SpinnerHandle[F]] =
      if (interactive)
        for {
          linesToClear <- stateRef.get.map(_.linesToClear)
          _            <- output.write(AnsiCode.cursorTo(0))
          _            <- (0 until linesToClear).toList.traverse_ { i =>
                            for {
                              _ <- if (i > 0) output.write(AnsiCode.moveUp(1)) else Monad[F].unit
                              _ <- output.write(AnsiCode.eraseLine)
                            } yield ()
                          }
          config       <- configRef.get
          _            <- if (config.indent > 0) output.write(AnsiCode.cursorTo(config.indent)) else Monad[F].unit
          _            <- stateRef.update(_.copy(linesToClear = 0))
        } yield self
      else
        Monad[F].pure(self)

    def render(): F[SpinnerHandle[F]] =
      if (!interactive) {
        Monad[F].pure(self)
      } else {
        val useSyncOutput = interactive
        for {
          _            <- if (useSyncOutput) output.write(AnsiCode.syncOutputEnable) else Monad[F].unit
          _            <- clear()
          frameContent <- frame()
          columns      <- output.columns.map(_.getOrElse(80))
          lineCount = computeLineCount(frameContent, columns)
          _ <- output.write(frameContent)
          _ <- stateRef.update(_.copy(linesToClear = lineCount))
          _ <- if (useSyncOutput) output.write(AnsiCode.syncOutputDisable) else Monad[F].unit
        } yield self
      }

    def getCurrentConfig: F[SpinnerConfig] = configRef.get

    private def advanceFrame(config: SpinnerConfig): F[SpinnerState] =
      effectOf(System.currentTimeMillis()).flatMap { now =>
        stateRef.updateAndGet { current =>
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

    private def updateConfig(f: SpinnerConfig => SpinnerConfig): F[Unit] =
      configRef.update(f)

  }

}
