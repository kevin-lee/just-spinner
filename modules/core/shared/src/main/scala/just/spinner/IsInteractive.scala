package just.spinner

/** Checks whether the terminal is interactive (not piped, not CI, not dumb terminal).
  */
@SuppressWarnings(Array("org.wartremover.warts.Equals"))
object IsInteractive {

  def check(output: TerminalOutput): Boolean =
    output.isTTY &&
      sys.env.get("TERM").forall(_ != "dumb") &&
      !sys.env.contains("CI")

}
