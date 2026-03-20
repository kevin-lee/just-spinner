package just.spinner

import hedgehog.*
import hedgehog.runner.*

object StringWidthSpec extends Properties {

  override def tests: List[Test] = List(
    property("ASCII string has width equal to length", testAsciiWidth),
    example("empty string has width 0", testEmptyWidth),
    example("CJK characters have width 2", testCjkWidth),
    example("ANSI escape codes are stripped from width calculation", testAnsiStripping),
    example("mixed ASCII and CJK", testMixedWidth),
  )

  def testAsciiWidth: Property =
    for {
      s <- Gen.string(Gen.alpha, Range.linear(0, 50)).log("s")
    } yield StringWidth.calculate(s) ==== s.length

  def testEmptyWidth: Result =
    StringWidth.calculate("") ==== 0

  def testCjkWidth: Result = {
    // Each CJK character should be width 2
    (StringWidth.calculate("\u4e16") ==== 2)
      .and( // 世
        StringWidth.calculate("\u4e16\u754c") ==== 4
      )
      .and( // 世界
        StringWidth.calculate("\u3053\u3093") ==== 4
      ) // こん (hiragana)
  }

  def testAnsiStripping: Result = {
    val colored = AnsiCode.color("hello", Color.Red)
    StringWidth.calculate(colored) ==== 5
  }

  def testMixedWidth: Result = {
    // "hi세계" = 2 ASCII + 2 CJK = 2 + 4 = 6
    StringWidth.calculate("hi\uc138\uacc4") ==== 6
  }

}
