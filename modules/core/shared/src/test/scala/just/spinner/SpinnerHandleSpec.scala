package just.spinner

import cats.Id
import effectie.instances.id.fx.*
import hedgehog.*
import hedgehog.runner.*

import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.duration.FiniteDuration

@SuppressWarnings(Array("org.wartremover.warts.Equals", "org.wartremover.warts.NonUnitStatements"))
object SpinnerHandleSpec extends Properties {

  /** A mock TerminalOutput[Id] that captures all writes to a buffer. */
  final class MockTerminalOutput(tty: Boolean) extends TerminalOutput[Id] {
    private val buffer: AtomicReference[List[String]] = new AtomicReference(Nil)

    @scala.annotation.tailrec
    def write(s: String): Unit = {
      val current = buffer.get()
      if (!buffer.compareAndSet(current, s :: current)) write(s)
    }

    def isTTY: Boolean       = tty
    def columns: Option[Int] = Some(80)
    def rows: Option[Int]    = Some(24)

    def written: List[String] = buffer.get().reverse
    def writtenJoined: String = written.mkString
    def reset(): Unit         = { buffer.set(Nil); () }
  }

  /** A mock SpinnerTimer[Id] that executes the task synchronously once. */
  object MockSpinnerTimer extends SpinnerTimer[Id] {
    def scheduleAtFixedRate(interval: FiniteDuration)(task: => Unit): SpinnerTimer.CancelToken[Id] = {
      task // execute once synchronously
      SpinnerTimer.CancelToken[Id](() => ())
    }
  }

  override def tests: List[Test] = List(
    example("create spinner with config", testCreateSpinner),
    example("start on non-interactive writes static line", testStartNonInteractive),
    example("start on silent does nothing", testStartSilent),
    example("succeed writes success symbol", testSucceed),
    example("fail writes error symbol", testFail),
    example("updateText changes text", testUpdateText),
    example("println writes message", testPrintln),
    example("frame produces spinner frame", testFrame),
    example("stopAndPersist writes final line", testStopAndPersist),
    example("start with isEnabled=true forces interactive on non-TTY", testStartWithEnabledForcesInteractive),
  )

  def testCreateSpinner: Result = {
    val config = SpinnerConfig.default.withText("loading")
    val output = new MockTerminalOutput(false)
    val handle = Spinner.create[Id](config, output, MockSpinnerTimer, SpinnerRefMaker.atomicRef[Id])
    Result.assert(!handle.isSpinning)
  }

  def testStartNonInteractive: Result = {
    val output = new MockTerminalOutput(false)
    val config = SpinnerConfig.default.withText("loading")
    val handle = Spinner.create[Id](config, output, MockSpinnerTimer, SpinnerRefMaker.atomicRef[Id])
    val _      = handle.start()
    val joined = output.writtenJoined
    Result.assert(joined.contains("loading")).and(Result.assert(joined.contains("-")))
  }

  def testStartSilent: Result = {
    val output = new MockTerminalOutput(false)
    val config = SpinnerConfig.default.withText("loading").withSilent(true)
    val handle = Spinner.create[Id](config, output, MockSpinnerTimer, SpinnerRefMaker.atomicRef[Id])
    val _      = handle.start()
    output.written ==== Nil
  }

  def testSucceed: Result = {
    val output = new MockTerminalOutput(false)
    val config = SpinnerConfig.default.withText("loading")
    val handle = Spinner.create[Id](config, output, MockSpinnerTimer, SpinnerRefMaker.atomicRef[Id])
    val _      = handle.start()
    output.reset()
    (handle.succeed(Some("done")): Unit)
    val joined = output.writtenJoined
    Result.assert(joined.contains("done"))
  }

  def testFail: Result = {
    val output = new MockTerminalOutput(false)
    val config = SpinnerConfig.default.withText("loading")
    val handle = Spinner.create[Id](config, output, MockSpinnerTimer, SpinnerRefMaker.atomicRef[Id])
    val _      = handle.start()
    output.reset()
    (handle.fail(Some("error occurred")): Unit)
    val joined = output.writtenJoined
    Result.assert(joined.contains("error occurred"))
  }

  def testUpdateText: Result = {
    val output = new MockTerminalOutput(false)
    val config = SpinnerConfig.default.withText("initial")
    val handle = Spinner.create[Id](config, output, MockSpinnerTimer, SpinnerRefMaker.atomicRef[Id])
    handle.updateText("updated")
    handle.text ==== "updated"
  }

  def testPrintln: Result = {
    val output = new MockTerminalOutput(false)
    val config = SpinnerConfig.default.withText("loading")
    val handle = Spinner.create[Id](config, output, MockSpinnerTimer, SpinnerRefMaker.atomicRef[Id])
    handle.println("a message")
    val joined = output.writtenJoined
    Result.assert(joined.contains("a message"))
  }

  def testFrame: Result = {
    val output = new MockTerminalOutput(false)
    val config = SpinnerConfig.default.withText("loading").withNoColor
    val handle = Spinner.create[Id](config, output, MockSpinnerTimer, SpinnerRefMaker.atomicRef[Id])
    val f      = handle.frame()
    Result.assert(f.contains("loading"))
  }

  def testStopAndPersist: Result = {
    val output  = new MockTerminalOutput(false)
    val config  = SpinnerConfig.default.withText("loading")
    val handle  = Spinner.create[Id](config, output, MockSpinnerTimer, SpinnerRefMaker.atomicRef[Id])
    val _       = handle.start()
    output.reset()
    val options = PersistOptions.empty.withSymbol("*").withText("persisted")
    (handle.stopAndPersist(options): Unit)
    val joined  = output.writtenJoined
    Result.assert(joined.contains("*")).and(Result.assert(joined.contains("persisted")))
  }

  def testStartWithEnabledForcesInteractive: Result = {
    val output = new MockTerminalOutput(false) // non-TTY
    val config = SpinnerConfig.default.withText("loading").withEnabled(true)
    val handle = Spinner.create[Id](config, output, MockSpinnerTimer, SpinnerRefMaker.atomicRef[Id])
    val _      = handle.start()
    val joined = output.writtenJoined
    // Interactive mode writes ANSI cursor-hide and renders a spinner frame, not a static "-"
    Result
      .assert(handle.isSpinning)
      .and(Result.assert(joined.contains(AnsiCode.cursorHide)))
      .and(Result.assert(!joined.contains("- loading")))
  }

}
