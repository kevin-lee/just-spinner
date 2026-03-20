package just.spinner

import hedgehog.*
import hedgehog.runner.*

object SpinnerStateSpec extends Properties {

  override def tests: List[Test] = List(
    example("initial state has expected values", testInitialState),
    property("copy preserves unchanged fields", testCopyPreservesFields),
  )

  def testInitialState: Result = {
    val s = SpinnerState.initial
    Result.all(
      List(
        (s.frameIndex ==== -1).log("frameIndex should be -1"),
        (s.lastFrameTime ==== 0L).log("lastFrameTime should be 0L"),
        (s.linesToClear ==== 0).log("linesToClear should be 0"),
        (s.isRunning ==== false).log("isRunning should be false"),
      )
    )
  }

  def testCopyPreservesFields: Property =
    for {
      idx <- Gen.int(Range.linear(0, 100)).log("idx")
    } yield {
      val s = SpinnerState.initial.copy(frameIndex = idx, isRunning = true)
      Result.all(
        List(
          (s.frameIndex ==== idx).log(s"frameIndex should be idx ($idx)"),
          (s.lastFrameTime ==== 0L).log("lastFrameTime should still be 0L"),
          (s.isRunning ==== true).log("isRunning should be true"),
        )
      )
    }

}
