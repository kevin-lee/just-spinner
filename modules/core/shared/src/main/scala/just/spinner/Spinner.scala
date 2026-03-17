package just.spinner

/** Entry point for creating spinner instances.
  *
  * @example
  * {{{
  * val handle = Spinner.withText("Loading...").start()
  * // ... do work ...
  * handle.succeed(Some("Done!"))
  * }}}
  */
object Spinner {

  def withText(text: String): SpinnerConfig =
    SpinnerConfig.default.withText(text)

  def withSpinnerType(spinnerType: SpinnerType): SpinnerConfig =
    SpinnerConfig.default.withSpinnerType(spinnerType)

  def create(config: SpinnerConfig): SpinnerHandle =
    createWith(config, TerminalOutput.stderr, SpinnerTimer.create)

  def createWith(config: SpinnerConfig, output: TerminalOutput, timer: SpinnerTimer): SpinnerHandle =
    SpinnerHandle(config, output, timer)

}
