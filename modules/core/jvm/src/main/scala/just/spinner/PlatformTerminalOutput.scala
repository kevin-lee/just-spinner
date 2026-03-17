package just.spinner

/** JVM implementation of TerminalOutput using System.err.
  */
private[spinner] object PlatformTerminalOutput {

  val stderr: TerminalOutput = new TerminalOutput {
    def write(s: String): Unit = {
      System.err.print(s)
      System.err.flush()
    }

    def isTTY: Boolean = Option(System.console()).isDefined

    def columns: Option[Int] =
      sys.env.get("COLUMNS").flatMap(s => s.toIntOption.filter(_ > 0))

    def rows: Option[Int] =
      sys.env.get("LINES").flatMap(s => s.toIntOption.filter(_ > 0))
  }

}
