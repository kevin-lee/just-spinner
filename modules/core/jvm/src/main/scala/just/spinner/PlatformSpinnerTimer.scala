package just.spinner

import cats.Id

import scala.concurrent.duration.FiniteDuration

/** JVM implementation using java.util.Timer with a daemon thread.
  */
private[spinner] object PlatformSpinnerTimer {

  val create: SpinnerTimer[Id] = new SpinnerTimer[Id] {
    def scheduleAtFixedRate(interval: FiniteDuration)(task: => Unit): SpinnerTimer.CancelToken[Id] = {
      val timer     = new java.util.Timer(true)
      val timerTask = new java.util.TimerTask {
        def run(): Unit = task
      }
      timer.scheduleAtFixedRate(timerTask, 0L, interval.toMillis)
      SpinnerTimer.CancelToken[Id](() => timer.cancel())
    }
  }

}
