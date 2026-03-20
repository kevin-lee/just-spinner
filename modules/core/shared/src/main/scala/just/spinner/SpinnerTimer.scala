package just.spinner

import cats.Id

import scala.concurrent.duration.FiniteDuration

/** Abstraction for a repeating timer used by the spinner animation loop,
  * parameterized by effect type `F[*]`.
  *
  * Platform-specific implementations are provided for JVM, Scala.js, and Scala Native.
  * The platform implementations provide `SpinnerTimer[Id]` for synchronous usage.
  * Other effect systems (e.g. cats-effect with `Dispatcher`) provide their own implementations.
  */
trait SpinnerTimer[F[*]] {
  def scheduleAtFixedRate(interval: FiniteDuration)(task: => F[Unit]): F[SpinnerTimer.CancelToken[F]]
}
object SpinnerTimer {

  trait CancelToken[F[*]] {
    def cancel(): F[Unit]
  }
  object CancelToken {
    def apply[F[*]](cancelF: () => F[Unit]): CancelToken[F] = new DefaultCancelToken[F](cancelF)

    final private class DefaultCancelToken[F[*]](actualCancel: () => F[Unit]) extends CancelToken[F] {
      override def cancel(): F[Unit] = actualCancel()
    }
  }

  /** Returns the platform-specific timer for synchronous (`Id`) usage. */
  def create: SpinnerTimer[Id] = PlatformSpinnerTimer.create

}
