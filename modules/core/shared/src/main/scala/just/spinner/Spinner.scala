package just.spinner

import cats.Monad
import effectie.core.FxCtor

/** Entry point for creating spinner instances.
  *
  * @example
  * {{{
  * import cats.Id
  * import effectie.instances.id.fx._
  *
  * val handle: SpinnerHandle[Id] =
  *   Spinner.create[Id](SpinnerConfig.default.withText("Loading..."), TerminalOutput.stderr[Id], SpinnerTimer.create, SpinnerRefMaker.atomicRef[Id])
  * handle.start()
  * // ... do work ...
  * handle.succeed(Some("Done!"))
  * }}}
  */
object Spinner {

  def withText(text: String): SpinnerConfig =
    SpinnerConfig.default.withText(text)

  def withSpinnerType(spinnerType: SpinnerType): SpinnerConfig =
    SpinnerConfig.default.withSpinnerType(spinnerType)

  def create[F[*]: Monad: FxCtor](
    config: SpinnerConfig,
    output: TerminalOutput[F],
    timer: SpinnerTimer[F],
    mkRef: SpinnerRefMaker[F],
  ): F[SpinnerHandle[F]] =
    SpinnerHandle[F](config, output, timer, mkRef)

  def createDefaultSideEffect(config: SpinnerConfig): SpinnerNoFx = {
    import effectie.instances.id.fx.idFx
    create[cats.Id](config, TerminalOutput.stderr[cats.Id], SpinnerTimer.create, SpinnerRefMaker.atomicRef[cats.Id])
  }
}
