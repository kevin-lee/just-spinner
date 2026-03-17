package just.spinner

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/** Scala.js facades for Node.js process and stream APIs. */
@js.native
@JSImport("process", JSImport.Default)
private[spinner] object NodeProcess extends js.Object {
  val stderr: NodeWritableStream = js.native
  val stdout: NodeWritableStream = js.native
  val platform: String           = js.native
  val env: js.Dictionary[String] = js.native
}

@js.native
private[spinner] trait NodeWritableStream extends js.Object {
  def write(s: String): Boolean  = js.native
  val isTTY: js.UndefOr[Boolean] = js.native
  val columns: js.UndefOr[Int]   = js.native
  val rows: js.UndefOr[Int]      = js.native
}
