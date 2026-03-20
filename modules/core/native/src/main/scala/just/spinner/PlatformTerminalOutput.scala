package just.spinner

import effectie.core.FxCtor
import effectie.syntax.all.*

import scala.scalanative.unsafe.*
import scala.scalanative.posix.unistd

/** Scala Native implementation of TerminalOutput using POSIX APIs.
  */
private[spinner] object PlatformTerminalOutput {

  private val STDERR_FILENO: CInt = 2

  def stderr[F[*]: FxCtor]: TerminalOutput[F] = new TerminalOutput[F] {
    def write(s: String): F[Unit] = effectOf {
      System.err.print(s)
      System.err.flush()
    }

    @SuppressWarnings(Array("org.wartremover.warts.Equals"))
    def isTTY: F[Boolean] = effectOf(unistd.isatty(STDERR_FILENO) == 1)

    def columns: F[Option[Int]] =
      effectOf(sys.env.get("COLUMNS").flatMap(_.toIntOption.filter(_ > 0)))

    def rows: F[Option[Int]] =
      effectOf(sys.env.get("LINES").flatMap(_.toIntOption.filter(_ > 0)))

  }

}
