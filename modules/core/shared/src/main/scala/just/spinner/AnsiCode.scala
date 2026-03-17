package just.spinner

/** ANSI escape sequence utilities for terminal control.
  */
object AnsiCode {
  val Esc: String = "\u001b"
  val Csi: String = Esc + "["

  def color(text: String, c: Color): String =
    Csi + c.code.toString + "m" + text + Csi + "0m"

  val cursorHide: String = Csi + "?25l"
  val cursorShow: String = Csi + "?25h"

  def cursorTo(col: Int): String = Csi + col.toString + "G"
  def moveUp(n: Int): String     = Csi + n.toString + "A"

  val eraseLine: String = Csi + "2K"

  val syncOutputEnable: String  = Csi + "?2026h"
  val syncOutputDisable: String = Csi + "?2026l"
}
