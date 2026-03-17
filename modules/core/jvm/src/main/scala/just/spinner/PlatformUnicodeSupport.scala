package just.spinner

import java.util.Locale

/** JVM unicode support detection using environment heuristics.
  */
@SuppressWarnings(Array("org.wartremover.warts.Equals"))
private[spinner] object PlatformUnicodeSupport {

  def isSupported: Boolean = {
    val os = sys.props.getOrElse("os.name", "").toLowerCase(Locale.ROOT)
    if (os.contains("win")) {
      // On Windows, check for known modern terminals
      sys.env.contains("WT_SESSION") || // Windows Terminal
      sys.env.get("TERM_PROGRAM").contains("vscode") || // VS Code
      sys.env.contains("ConEmuTask") || // ConEmu
      sys.env.get("TERMINAL_EMULATOR").contains("JetBrains-JediTerm") // IntelliJ
    } else {
      // Non-Windows: assume unicode unless TERM=linux (virtual console)
      sys.env.get("TERM").forall(_ != "linux")
    }
  }

}
