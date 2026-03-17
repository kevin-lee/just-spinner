package just.spinner

import hedgehog._
import hedgehog.runner._

import scala.concurrent.duration._

object SpinnerTypeSpec extends Properties {

  override def tests: List[Test] = List(
    example("dots spinner has non-empty frames", testDotsNonEmpty),
    example("line spinner has non-empty frames", testLineNonEmpty),
    example("all built-in spinners have positive intervals", testAllPositiveIntervals),
    example("all built-in spinners have non-empty frame lists", testAllNonEmptyFrames),
    example("custom SpinnerType can be created", testCustomSpinnerType),
  )

  private val allBuiltIn: List[SpinnerType] = List(
    SpinnerType.dots,
    SpinnerType.dots2,
    SpinnerType.dots3,
    SpinnerType.dots12,
    SpinnerType.line,
    SpinnerType.moon,
    SpinnerType.arrow,
    SpinnerType.bouncingBar,
    SpinnerType.arc,
    SpinnerType.toggle,
    SpinnerType.clock,
    SpinnerType.earth,
    SpinnerType.star,
    SpinnerType.aesthetic,
    SpinnerType.tableFlip,
  )

  def testDotsNonEmpty: Result =
    Result.assert(SpinnerType.dots.frames.nonEmpty)

  def testLineNonEmpty: Result =
    Result.assert(SpinnerType.line.frames.nonEmpty)

  def testAllPositiveIntervals: Result = {
    allBuiltIn.foldLeft(Result.success) { (acc, st) =>
      acc.and(Result.assert(st.interval > 0.millis))
    }
  }

  def testAllNonEmptyFrames: Result = {
    allBuiltIn.foldLeft(Result.success) { (acc, st) =>
      acc.and(Result.assert(st.frames.nonEmpty))
    }
  }

  def testCustomSpinnerType: Result = {
    val custom = SpinnerType(List("a", "b", "c"), 100.millis)
    (custom.frames.length ==== 3).and(custom.interval ==== 100.millis)
  }

}
