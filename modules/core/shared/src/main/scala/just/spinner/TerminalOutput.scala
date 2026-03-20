package just.spinner

import effectie.core.FxCtor

/** Abstraction for terminal output operations, parameterized by effect type `F[*]`.
  *
  * Platform-specific implementations are provided for JVM, Scala.js, and Scala Native.
  */
trait TerminalOutput[F[*]] {
  def write(s: String): F[Unit]
  def isTTY: F[Boolean]
  def columns: F[Option[Int]]
  def rows: F[Option[Int]]
}
object TerminalOutput {

  /** Returns the platform-specific stderr terminal output for effect type `F`. */
  def stderr[F[*]: FxCtor]: TerminalOutput[F] = PlatformTerminalOutput.stderr[F]
}
