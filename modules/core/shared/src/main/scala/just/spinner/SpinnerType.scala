package just.spinner

import scala.concurrent.duration.*

/** Defines a spinner animation with its frames and interval.
  */
final case class SpinnerType(frames: List[String], interval: FiniteDuration)
object SpinnerType {

  // format: off
  val dots: SpinnerType = SpinnerType(
    List("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"),
    80.millis,
  )

  val dots2: SpinnerType = SpinnerType(
    List("⣾", "⣽", "⣻", "⢿", "⡿", "⣟", "⣯", "⣷"),
    80.millis,
  )

  val dots3: SpinnerType = SpinnerType(
    List("⠋", "⠙", "⠚", "⠞", "⠖", "⠦", "⠴", "⠲", "⠳", "⠓"),
    80.millis,
  )

  val dots12: SpinnerType = SpinnerType(
    List(
      "⢀⠀", "⡀⠀", "⠄⠀", "⢂⠀", "⡂⠀", "⠅⠀", "⢃⠀", "⡃⠀",
      "⠍⠀", "⢋⠀", "⡋⠀", "⠍⠁", "⢋⠁", "⡋⠁", "⠍⠉", "⠋⠉",
      "⠋⠉", "⠉⠙", "⠉⠙", "⠉⠩", "⠈⢙", "⠈⡙", "⢈⠩", "⡀⢙",
      "⠄⡙", "⢂⠩", "⡂⢘", "⠅⡘", "⢃⠨", "⡃⢐", "⠍⡐", "⢋⠠",
      "⡋⢀", "⠍⡁", "⢋⠁", "⡋⠁", "⠍⠉", "⠋⠉", "⠋⠉", "⠉⠙",
      "⠉⠙", "⠉⠩", "⠈⢙", "⠈⡙", "⠈⠩", "⠀⢙", "⠀⡙", "⠀⠩",
      "⠀⢘", "⠀⡘", "⠀⠨", "⠀⢐", "⠀⡐", "⠀⠠", "⠀⢀", "⠀⡀",
    ),
    80.millis,
  )

  val line: SpinnerType = SpinnerType(
    List("-", "\\", "|", "/"),
    130.millis,
  )

  val moon: SpinnerType = SpinnerType(
    List("🌑", "🌒", "🌓", "🌔", "🌕", "🌖", "🌗", "🌘"),
    80.millis,
  )

  val arrow: SpinnerType = SpinnerType(
    List("←", "↖", "↑", "↗", "→", "↘", "↓", "↙"),
    100.millis,
  )

  val bouncingBar: SpinnerType = SpinnerType(
    List(
      "[    ]", "[=   ]", "[==  ]", "[=== ]", "[ ===]", "[  ==]",
      "[   =]", "[    ]", "[   =]", "[  ==]", "[ ===]", "[====]",
      "[=== ]", "[==  ]", "[=   ]",
    ),
    80.millis,
  )

  val arc: SpinnerType = SpinnerType(
    List("◜", "◠", "◝", "◞", "◡", "◟"),
    100.millis,
  )

  val toggle: SpinnerType = SpinnerType(
    List("⊶", "⊷"),
    250.millis,
  )

  val clock: SpinnerType = SpinnerType(
    List("🕛", "🕐", "🕑", "🕒", "🕓", "🕔", "🕕", "🕖", "🕗", "🕘", "🕙", "🕚"),
    100.millis,
  )

  val earth: SpinnerType = SpinnerType(
    List("🌍", "🌎", "🌏"),
    180.millis,
  )

  val star: SpinnerType = SpinnerType(
    List("✶", "✸", "✹", "✺", "✹", "✷"),
    200.millis,
  )

  val aesthetic: SpinnerType = SpinnerType(
    List(
      "▰▱▱▱▱▱▱", "▰▰▱▱▱▱▱", "▰▰▰▱▱▱▱", "▰▰▰▰▱▱▱",
      "▰▰▰▰▰▱▱", "▰▰▰▰▰▰▱", "▰▰▰▰▰▰▰", "▰▱▱▱▱▱▱",
    ),
    80.millis,
  )

  val tableFlip: SpinnerType = SpinnerType(
    List(
      """(˘･_･˘) ┳━┳""",
      """( ಠ_ಠ)ノ┳━┳""",
      """(╯°□°)╯彡┳━┳""",
      """(╯°□°)╯ ︵ ┻━┻""",
    ),
    300.millis,
  )
  // format: on

}
