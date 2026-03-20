package just.spinner

import effectie.core.FxCtor
import effectie.syntax.all.*

import java.util.concurrent.atomic.AtomicReference

/** Mutable reference abstraction parameterized by effect type `F[*]`.
  *
  * For `Id`, this is backed by `AtomicReference` with direct evaluation.
  * Other effect systems (e.g. cats-effect) can provide their own implementations.
  */
trait SpinnerRef[F[*], A] {
  def get: F[A]
  def set(a: A): F[Unit]
  def update(f: A => A): F[Unit]
  def updateAndGet(f: A => A): F[A]
  def getAndSet(a: A): F[A]
}
object SpinnerRef {

  /** Create a `SpinnerRef` backed by `AtomicReference`, suitable for any `F` with `FxCtor`.
    *
    * Each operation wraps the underlying `AtomicReference` call in `effectOf(...)`.
    */
  def atomicRef[F[*]: FxCtor, A](initial: A): F[SpinnerRef[F, A]] =
    effectOf {
      val underlying = new AtomicReference[A](initial)
      new AtomicRefBased[F, A](underlying)
    }

  final private class AtomicRefBased[F[*]: FxCtor, A](ref: AtomicReference[A]) extends SpinnerRef[F, A] {

    def get: F[A] = effectOf(ref.get())

    def set(a: A): F[Unit] = effectOf(ref.set(a))

    def update(f: A => A): F[Unit] = effectOf { val _ = casUpdateAndGet(f) }

    def updateAndGet(f: A => A): F[A] = effectOf(casUpdateAndGet(f))

    def getAndSet(a: A): F[A] = effectOf(casGetAndSet(a))

    @scala.annotation.tailrec
    private def casUpdateAndGet(f: A => A): A = {
      val current = ref.get()
      val updated = f(current)
      if (ref.compareAndSet(current, updated)) updated
      else casUpdateAndGet(f)
    }

    @scala.annotation.tailrec
    private def casGetAndSet(newValue: A): A = {
      val current = ref.get()
      if (ref.compareAndSet(current, newValue)) current
      else casGetAndSet(newValue)
    }

  }

}
