package just.spinner

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import scala.concurrent.duration.FiniteDuration

/** Scala Native implementation using ScheduledThreadPoolExecutor.
  *
  * java.util.Timer is not available in Scala Native's javalib,
  * but ScheduledThreadPoolExecutor is (since Scala Native 0.5).
  * Thread.setDaemon is not available in Scala Native, so we use
  * the default thread factory.
  */
private[spinner] object PlatformSpinnerTimer {

  val create: SpinnerTimer = new SpinnerTimer {
    def scheduleAtFixedRate(interval: FiniteDuration)(task: => Unit): SpinnerTimer.CancelToken = {
      val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
      val runnable: Runnable                  = new Runnable { def run(): Unit = task }
      val handle = scheduler.scheduleAtFixedRate(runnable, 0L, interval.toMillis, TimeUnit.MILLISECONDS)
      SpinnerTimer.CancelToken(() => {
        val _ = handle.cancel(false)
        scheduler.shutdown()
      })
    }
  }

}
