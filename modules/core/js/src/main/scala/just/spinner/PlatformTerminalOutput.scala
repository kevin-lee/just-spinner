package just.spinner

import effectie.core.FxCtor
import effectie.syntax.all.*

/** Scala.js (Node.js) implementation of TerminalOutput.
  */
private[spinner] object PlatformTerminalOutput {

  def stderr[F[*]: FxCtor]: TerminalOutput[F] = new TerminalOutput[F] {
    def write(s: String): F[Unit] = effectOf {
      val _ = NodeProcess.stderr.write(s)
    }

    def isTTY: F[Boolean] =
      effectOf(NodeProcess.stderr.isTTY.getOrElse(false))

    def columns: F[Option[Int]] =
      effectOf(NodeProcess.stderr.columns.toOption)

    def rows: F[Option[Int]] =
      effectOf(NodeProcess.stderr.rows.toOption)
  }

}
