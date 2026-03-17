package just.spinner

import scala.concurrent.duration.FiniteDuration

/** Abstraction for a repeating timer used by the spinner animation loop.
  *
  * Platform-specific implementations are provided for JVM, Scala.js, and Scala Native.
  */
trait SpinnerTimer {
  def scheduleAtFixedRate(interval: FiniteDuration)(task: => Unit): SpinnerTimer.CancelToken
}
object SpinnerTimer {

  trait CancelToken {
    def cancel(): Unit
  }
  object CancelToken {
    def apply(cancel: () => Unit): CancelToken = new DefaultCancelToken(cancel)

    final private class DefaultCancelToken(actualCancel: () => Unit) extends CancelToken {
      override def cancel(): Unit = actualCancel()
    }
  }

  /** Returns the platform-specific timer. */
  def create: SpinnerTimer = PlatformSpinnerTimer.create

}
