package just.spinner

/** Status symbols for spinner completion states.
  */
sealed abstract class LogSymbol(val unicode: String, val ascii: String)
object LogSymbol {
  case object Info extends LogSymbol("\u2139", "i")
  case object Success extends LogSymbol("\u2714", "\u221a")
  case object Warning extends LogSymbol("\u26a0", "\u203c")
  case object Error extends LogSymbol("\u2716", "\u00d7")

  def info: LogSymbol    = Info
  def success: LogSymbol = Success
  def warning: LogSymbol = Warning
  def error: LogSymbol   = Error

  def unapply(symbol: LogSymbol): Option[(String, String)] = Some((symbol.unicode, symbol.ascii))

  def resolve(symbol: LogSymbol, unicodeSupport: UnicodeSupport): String =
    unicodeSupport match {
      case UnicodeSupport.Supported => symbol.unicode
      case UnicodeSupport.Unsupported => symbol.ascii
    }

  def colored(symbol: LogSymbol, unicodeSupport: UnicodeSupport): String = {
    val resolved = resolve(symbol, unicodeSupport)
    symbol match {
      case Info => AnsiCode.color(resolved, Color.blue)
      case Success => AnsiCode.color(resolved, Color.green)
      case Warning => AnsiCode.color(resolved, Color.yellow)
      case Error => AnsiCode.color(resolved, Color.red)
    }
  }
}
