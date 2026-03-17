package just.spinner

/** Scala.js (Node.js) unicode support detection.
  */
@SuppressWarnings(Array("org.wartremover.warts.Equals"))
private[spinner] object PlatformUnicodeSupport {

  def isSupported: Boolean = {
    val platform = NodeProcess.platform
    if (platform == "win32") {
      NodeProcess.env.contains("WT_SESSION") ||
      NodeProcess.env.get("TERM_PROGRAM").contains("vscode") ||
      NodeProcess.env.contains("ConEmuTask") ||
      NodeProcess.env.get("TERMINAL_EMULATOR").contains("JetBrains-JediTerm")
    } else {
      NodeProcess.env.get("TERM").forall(_ != "linux")
    }
  }

}
