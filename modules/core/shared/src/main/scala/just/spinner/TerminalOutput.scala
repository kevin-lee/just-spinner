package just.spinner

/** Abstraction for terminal output operations.
  *
  * Platform-specific implementations are provided for JVM, Scala.js, and Scala Native.
  */
trait TerminalOutput {
  def write(s: String): Unit
  def isTTY: Boolean
  def columns: Option[Int]
  def rows: Option[Int]
}
object TerminalOutput {

  /** Returns the platform-specific stderr terminal output. */
  def stderr: TerminalOutput = PlatformTerminalOutput.stderr
}
