package just.spinner

import effectie.core.FxCtor
import effectie.syntax.all.*

/** JVM implementation of TerminalOutput using System.err.
  */
private[spinner] object PlatformTerminalOutput {

  def stderr[F[*]: FxCtor]: TerminalOutput[F] = new TerminalOutput[F] {
    def write(s: String): F[Unit] = effectOf {
      System.err.print(s)
      System.err.flush()
    }

    def isTTY: F[Boolean] = effectOf(Option(System.console()).isDefined)

    def columns: F[Option[Int]] =
      effectOf(sys.env.get("COLUMNS").flatMap(s => s.toIntOption.filter(_ > 0)))

    def rows: F[Option[Int]] =
      effectOf(sys.env.get("LINES").flatMap(s => s.toIntOption.filter(_ > 0)))
  }

}
