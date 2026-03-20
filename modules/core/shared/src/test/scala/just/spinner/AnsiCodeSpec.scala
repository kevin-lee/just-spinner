package just.spinner

import hedgehog.*
import hedgehog.runner.*

object AnsiCodeSpec extends Properties {

  override def tests: List[Test] = List(
    example("cursorHide is correct ANSI sequence", testCursorHide),
    example("cursorShow is correct ANSI sequence", testCursorShow),
    property("cursorTo produces correct sequence", testCursorTo),
    property("moveUp produces correct sequence", testMoveUp),
    example("eraseLine is correct ANSI sequence", testEraseLine),
    property("color wraps text with correct codes", testColor),
    example("syncOutput sequences are correct", testSyncOutput),
  )

  def testCursorHide: Result = {
    AnsiCode.cursorHide ==== "\u001b[?25l"
  }

  def testCursorShow: Result = {
    AnsiCode.cursorShow ==== "\u001b[?25h"
  }

  def testCursorTo: Property =
    for {
      col <- Gen.int(Range.linear(0, 200)).log("col")
    } yield AnsiCode.cursorTo(col) ==== s"\u001b[${col}G"

  def testMoveUp: Property =
    for {
      n <- Gen.int(Range.linear(1, 50)).log("n")
    } yield AnsiCode.moveUp(n) ==== s"\u001b[${n}A"

  def testEraseLine: Result = {
    AnsiCode.eraseLine ==== "\u001b[2K"
  }

  def testColor: Property =
    for {
      s <- Gen.string(Gen.unicode, Range.linear(1, 20)).log("s")
    } yield {
      val result = AnsiCode.color(s, Color.Red)
      result ==== s"\u001b[31m$s\u001b[0m"
    }

  def testSyncOutput: Result = {
    (AnsiCode.syncOutputEnable ==== "\u001b[?2026h").and(AnsiCode.syncOutputDisable ==== "\u001b[?2026l")
  }

}
