package just.spinner

/** ANSI terminal colors for spinner display.
  */
sealed abstract class Color(val code: Int)
object Color {
  case object Black extends Color(30)
  case object Red extends Color(31)
  case object Green extends Color(32)
  case object Yellow extends Color(33)
  case object Blue extends Color(34)
  case object Magenta extends Color(35)
  case object Cyan extends Color(36)
  case object White extends Color(37)
  case object Gray extends Color(90)

  def black: Color   = Black
  def red: Color     = Red
  def green: Color   = Green
  def yellow: Color  = Yellow
  def blue: Color    = Blue
  def magenta: Color = Magenta
  def cyan: Color    = Cyan
  def white: Color   = White
  def gray: Color    = Gray

  def unapply(c: Color): Option[Int] = Some(c.code)

  val all: List[Color] = List(black, red, green, yellow, blue, magenta, cyan, white, gray)
}
