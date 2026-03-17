package just.spinner

/** Configuration for a spinner instance, built via `withX` methods.
  */
final case class SpinnerConfig(
  text: Option[String],
  prefixText: Option[String],
  suffixText: Option[String],
  spinnerType: SpinnerType,
  color: Option[Color],
  indent: Int,
  hideCursor: Boolean,
  isSilent: Boolean,
  isEnabled: Option[Boolean],
) {}
object SpinnerConfig {
  val default: SpinnerConfig = SpinnerConfig(
    text = None,
    prefixText = None,
    suffixText = None,
    spinnerType = SpinnerType.dots,
    color = Some(Color.Cyan),
    indent = 0,
    hideCursor = true,
    isSilent = false,
    isEnabled = None,
  )

  implicit class SpinnerConfigOps(private val spinnerConfig: SpinnerConfig) extends AnyVal {
    def withText(text: String): SpinnerConfig           = spinnerConfig.copy(text = Some(text))
    def withPrefixText(prefix: String): SpinnerConfig   = spinnerConfig.copy(prefixText = Some(prefix))
    def withSuffixText(suffix: String): SpinnerConfig   = spinnerConfig.copy(suffixText = Some(suffix))
    def withSpinnerType(st: SpinnerType): SpinnerConfig = spinnerConfig.copy(spinnerType = st)
    def withColor(c: Color): SpinnerConfig              = spinnerConfig.copy(color = Some(c))
    def withNoColor: SpinnerConfig                      = spinnerConfig.copy(color = None)
    def withIndent(n: Int): SpinnerConfig               = spinnerConfig.copy(indent = n)
    def withHideCursor(hide: Boolean): SpinnerConfig    = spinnerConfig.copy(hideCursor = hide)
    def withSilent(silent: Boolean): SpinnerConfig      = spinnerConfig.copy(isSilent = silent)
    def withEnabled(enabled: Boolean): SpinnerConfig    = spinnerConfig.copy(isEnabled = Some(enabled))
  }

}
