package just.spinner

import effectie.core.FxCtor

/** Factory for creating SpinnerRef instances, parameterized by effect type `F[*]`.
  *
  * This is NOT a typeclass — it is passed as an explicit parameter.
  * For `Id`, use the `AtomicReference`-backed implementation from `SpinnerRefMaker.atomicRef`.
  * For cats-effect `IO`, provide an implementation backed by `cats.effect.Ref`.
  */
trait SpinnerRefMaker[F[*]] {
  def apply[A](initial: A): F[SpinnerRef[F, A]]
}
object SpinnerRefMaker {

  /** SpinnerRefMaker backed by `AtomicReference`, suitable for `Id` or any `F` with `FxCtor`.
    * Delegates to `SpinnerRef.atomicRef`.
    */
  def atomicRef[F[*]: FxCtor]: SpinnerRefMaker[F] = new SpinnerRefMaker[F] {
    def apply[A](initial: A): F[SpinnerRef[F, A]] = SpinnerRef.atomicRef[F, A](initial)
  }
}
