package just.spinner

/** Detects whether the current terminal supports Unicode characters.
  *
  * Delegates to platform-specific detection.
  */
object UnicodeSupport {
  def isSupported: Boolean = PlatformUnicodeSupport.isSupported
}
