package just.spinner

import scala.scalanative.unsafe._
import scala.scalanative.posix.unistd

/** Scala Native implementation of TerminalOutput using POSIX APIs.
  */
private[spinner] object PlatformTerminalOutput {

  private val STDERR_FILENO: CInt = 2

  val stderr: TerminalOutput = new TerminalOutput {
    def write(s: String): Unit = {
      System.err.print(s)
      System.err.flush()
    }

    @SuppressWarnings(Array("org.wartremover.warts.Equals"))
    def isTTY: Boolean = unistd.isatty(STDERR_FILENO) == 1

    def columns: Option[Int] =
      sys.env.get("COLUMNS").flatMap(_.toIntOption.filter(_ > 0))

    def rows: Option[Int] =
      sys.env.get("LINES").flatMap(_.toIntOption.filter(_ > 0))

  }

}
