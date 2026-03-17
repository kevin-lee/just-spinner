package just.spinner

/** Immutable state for a running spinner.
  */
final case class SpinnerState(
  frameIndex: Int,
  lastFrameTime: Long,
  linesToClear: Int,
  isRunning: Boolean,
)
object SpinnerState {
  val initial: SpinnerState = SpinnerState(
    frameIndex = -1,
    lastFrameTime = 0L,
    linesToClear = 0,
    isRunning = false,
  )
}
