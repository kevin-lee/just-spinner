package just.spinner

/** Detects whether the current terminal supports Unicode characters.
  *
  * Delegates to platform-specific detection.
  */
sealed trait UnicodeSupport
object UnicodeSupport {
  case object Supported extends UnicodeSupport
  case object Unsupported extends UnicodeSupport

  def supported: UnicodeSupport   = Supported
  def unsupported: UnicodeSupport = Unsupported

  def fromPlatformUnicodeSupport: UnicodeSupport =
    if (PlatformUnicodeSupport.isSupported)
      supported
    else
      unsupported

}
