package just.spinner

import scala.concurrent.duration.FiniteDuration
import scala.scalajs.js.timers

/** Scala.js implementation using js.timers.setInterval.
  */
private[spinner] object PlatformSpinnerTimer {

  val create: SpinnerTimer = new SpinnerTimer {
    def scheduleAtFixedRate(interval: FiniteDuration)(task: => Unit): SpinnerTimer.CancelToken = {
      val handle = timers.setInterval(interval)(task)
      SpinnerTimer.CancelToken(() => timers.clearInterval(handle))
    }
  }

}
