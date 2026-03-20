package just.spinner

import cats.Monad
import cats.syntax.all.*
import effectie.core.FxCtor
import effectie.syntax.all.*

/** Checks whether the terminal is interactive (not piped, not CI, not dumb terminal).
  */
object IsInteractive {

  def check[F[*]: Monad: FxCtor](output: TerminalOutput[F]): F[Boolean] =
    output.isTTY.flatMap { tty =>
      effectOf {
        tty &&
        sys.env.get("TERM").forall(_ =!= "dumb") &&
        !sys.env.contains("CI")
      }
    }

}
