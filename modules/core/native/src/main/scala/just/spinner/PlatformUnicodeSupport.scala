package just.spinner

/** Scala Native unicode support detection using environment heuristics.
  *
  * Note: java.util.Locale is not available in Scala Native,
  * so we use a simple char-by-char lowercase conversion.
  */
@SuppressWarnings(Array("org.wartremover.warts.Equals"))
private[spinner] object PlatformUnicodeSupport {

  private def asciiLowerCase(s: String): String =
    s.map { c =>
      if (c >= 'A' && c <= 'Z') (c + 32).toChar else c
    }

  def isSupported: Boolean = {
    val os = asciiLowerCase(sys.props.getOrElse("os.name", ""))
    if (os.contains("win")) {
      sys.env.contains("WT_SESSION") ||
      sys.env.get("TERM_PROGRAM").contains("vscode") ||
      sys.env.contains("ConEmuTask") ||
      sys.env.get("TERMINAL_EMULATOR").contains("JetBrains-JediTerm")
    } else {
      sys.env.get("TERM").forall(_ != "linux")
    }
  }

}
