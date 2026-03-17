package just.spinner

/** Simplified visual width calculation for terminal strings.
  *
  * Strips ANSI escape sequences and accounts for fullwidth CJK characters.
  */
object StringWidth {

  private val esc: String = "\u001b"
  private val ansiPattern = s"$esc\\[[0-9;]*[a-zA-Z]|$esc\\][^$esc]*$esc\\\\|$esc\\[\\?[0-9]+[a-z]".r

  def calculate(s: String): Int = {
    val stripped                    = stripAnsi(s)
    @scala.annotation.tailrec
    def loop(i: Int, acc: Int): Int =
      if (i >= stripped.length) acc
      else {
        val cp = stripped.codePointAt(i)
        loop(i + Character.charCount(cp), acc + charWidth(cp))
      }
    loop(0, 0)
  }

  def stripAnsi(s: String): String =
    ansiPattern.replaceAllIn(s, "")

  private def charWidth(codePoint: Int): Int = if (isFullwidth(codePoint)) 2 else 1

  private def isFullwidth(cp: Int): Boolean =
    // CJK Unified Ideographs
    (cp >= 0x4e00 && cp <= 0x9fff) ||
      // CJK Unified Ideographs Extension A
      (cp >= 0x3400 && cp <= 0x4dbf) ||
      // CJK Compatibility Ideographs
      (cp >= 0xf900 && cp <= 0xfaff) ||
      // CJK Symbols and Punctuation
      (cp >= 0x3000 && cp <= 0x303f) ||
      // Halfwidth and Fullwidth Forms
      (cp >= 0xff00 && cp <= 0xffef) ||
      // Katakana
      (cp >= 0x30a0 && cp <= 0x30ff) ||
      // Hiragana
      (cp >= 0x3040 && cp <= 0x309f) ||
      // Hangul Syllables
      (cp >= 0xac00 && cp <= 0xd7af) ||
      // CJK Unified Ideographs Extension B
      (cp >= 0x20000 && cp <= 0x2a6df) ||
      // CJK Unified Ideographs Extension C
      (cp >= 0x2a700 && cp <= 0x2b73f)

}
