package just.spinner

import hedgehog.*
import hedgehog.runner.*

object ColorSpec extends Properties {

  override def tests: List[Test] = List(
    example("all colors have unique codes", testAllColorsUniqueCode),
    example("all colors have code in valid ANSI range", testAllColorsValidRange),
    example("Color.all contains all colors", testColorAllComplete),
  )

  def testAllColorsUniqueCode: Result = {
    val codes = Color.all.map(_.code)
    codes.distinct.length ==== codes.length
  }

  def testAllColorsValidRange: Result = {
    Color.all.foldLeft(Result.success) { (acc, color) =>
      acc.and(
        Result.assert(
          (color.code >= 30 && color.code <= 37) || (color.code >= 90 && color.code <= 97)
        )
      )
    }
  }

  def testColorAllComplete: Result = {
    Color.all.length ==== 9
  }

}
