package just.spinner

import hedgehog._
import hedgehog.runner._

object SpinnerConfigSpec extends Properties {

  override def tests: List[Test] = List(
    example("default config has expected values", testDefaultConfig),
    property("withText sets text", testWithText),
    example("withColor sets color", testWithColor),
    example("withNoColor removes color", testWithNoColor),
    property("withIndent sets indent", testWithIndent),
    example("withSpinnerType changes spinner type", testWithSpinnerType),
    property("withPrefixText sets prefix", testWithPrefixText),
    property("withSuffixText sets suffix", testWithSuffixText),
    example("withSilent sets silent", testWithSilent),
    example("withHideCursor sets hideCursor", testWithHideCursor),
    example("withEnabled sets isEnabled", testWithEnabled),
  )

  def testDefaultConfig: Result = {
    val d = SpinnerConfig.default

    Result.all(
      List(
        (d.text ==== None),
        (d.color ==== Some(Color.Cyan)),
        (d.indent ==== 0),
        (d.hideCursor ==== true),
        (d.isSilent ==== false),
        (d.isEnabled ==== None),
        (d.prefixText ==== None),
        (d.suffixText ==== None),
      )
    )

  }

  def testWithText: Property =
    for {
      t <- Gen.string(Gen.alpha, Range.linear(1, 20)).log("t")
    } yield SpinnerConfig.default.withText(t).text ==== Some(t)

  def testWithColor: Result = {
    SpinnerConfig.default.withColor(Color.Red).color ==== Some(Color.Red)
  }

  def testWithNoColor: Result = {
    SpinnerConfig.default.withNoColor.color ==== None
  }

  def testWithIndent: Property =
    for {
      n <- Gen.int(Range.linear(0, 20)).log("n")
    } yield SpinnerConfig.default.withIndent(n).indent ==== n

  def testWithSpinnerType: Result = {
    SpinnerConfig.default.withSpinnerType(SpinnerType.line).spinnerType ==== SpinnerType.line
  }

  def testWithPrefixText: Property =
    for {
      p <- Gen.string(Gen.alpha, Range.linear(1, 10)).log("p")
    } yield SpinnerConfig.default.withPrefixText(p).prefixText ==== Some(p)

  def testWithSuffixText: Property =
    for {
      s <- Gen.string(Gen.alpha, Range.linear(1, 10)).log("s")
    } yield SpinnerConfig.default.withSuffixText(s).suffixText ==== Some(s)

  def testWithSilent: Result = {
    SpinnerConfig.default.withSilent(true).isSilent ==== true
  }

  def testWithHideCursor: Result = {
    SpinnerConfig.default.withHideCursor(false).hideCursor ==== false
  }

  def testWithEnabled: Result = {
    SpinnerConfig.default.withEnabled(true).isEnabled ==== Some(true)
  }

}
