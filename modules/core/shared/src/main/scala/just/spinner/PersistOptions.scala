package just.spinner

/** Options for `stopAndPersist`, controlling the final output line.
  */
final case class PersistOptions(
  symbol: Option[String],
  text: Option[String],
  prefixText: Option[String],
  suffixText: Option[String],
)
object PersistOptions {
  val empty: PersistOptions = PersistOptions(
    symbol = None,
    text = None,
    prefixText = None,
    suffixText = None,
  )

  implicit final class PersistOptionsOps(private val persistOptions: PersistOptions) extends AnyVal {
    def withSymbol(s: String): PersistOptions     = persistOptions.copy(symbol = Some(s))
    def withText(t: String): PersistOptions       = persistOptions.copy(text = Some(t))
    def withPrefixText(p: String): PersistOptions = persistOptions.copy(prefixText = Some(p))
    def withSuffixText(s: String): PersistOptions = persistOptions.copy(suffixText = Some(s))
  }
}
