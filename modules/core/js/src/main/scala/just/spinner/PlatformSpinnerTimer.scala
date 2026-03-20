package just.spinner

import cats.Id

import scala.concurrent.duration.FiniteDuration
import scala.scalajs.js.timers

/** Scala.js implementation using js.timers.setInterval.
  */
private[spinner] object PlatformSpinnerTimer {

  val create: SpinnerTimer[Id] = new SpinnerTimer[Id] {
    def scheduleAtFixedRate(interval: FiniteDuration)(task: => Unit): SpinnerTimer.CancelToken[Id] = {
      val handle = timers.setInterval(interval)(task)
      SpinnerTimer.CancelToken[Id](() => timers.clearInterval(handle))
    }
  }

}
