package just.spinner

/** Scala.js (Node.js) implementation of TerminalOutput.
  */
private[spinner] object PlatformTerminalOutput {

  val stderr: TerminalOutput = new TerminalOutput {
    def write(s: String): Unit = {
      val _ = NodeProcess.stderr.write(s)
    }

    def isTTY: Boolean =
      NodeProcess.stderr.isTTY.getOrElse(false)

    def columns: Option[Int] =
      NodeProcess.stderr.columns.toOption

    def rows: Option[Int] =
      NodeProcess.stderr.rows.toOption
  }

}
