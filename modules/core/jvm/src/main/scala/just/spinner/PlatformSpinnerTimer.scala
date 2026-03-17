package just.spinner

import scala.concurrent.duration.FiniteDuration

/** JVM implementation using java.util.Timer with a daemon thread.
  */
private[spinner] object PlatformSpinnerTimer {

  val create: SpinnerTimer = new SpinnerTimer {
    def scheduleAtFixedRate(interval: FiniteDuration)(task: => Unit): SpinnerTimer.CancelToken = {
      val timer     = new java.util.Timer(true)
      val timerTask = new java.util.TimerTask {
        def run(): Unit = task
      }
      timer.scheduleAtFixedRate(timerTask, 0L, interval.toMillis)
      SpinnerTimer.CancelToken(() => timer.cancel())
    }
  }

}
