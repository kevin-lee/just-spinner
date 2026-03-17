package example

import just.spinner.*

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.timers

object Main {

  private def delay(ms: Long): Future[Unit] = {
    val p = Promise[Unit]()
    timers.setTimeout(ms.toDouble) {
      p.success(())
    }
    p.future
  }

  private def scenario1(): Future[Unit] = {
    println("\n=== Scenario 1: Basic spinner with succeed ===")
    val handle = Spinner
      .create(
        SpinnerConfig
          .default
          .withText("Processing data...")
          .withColor(Color.Cyan)
          .withIndent(2)
          .withEnabled(true)
      )
    handle.start()
    delay(3_000L).map { _ =>
      handle.succeed(Some("Data processed successfully"))
    }
  }

  private def scenario2(): Future[Unit] = {
    println("\n=== Scenario 2: Moon spinner with prefix/suffix, fail ===")
    val handle = Spinner
      .create(
        SpinnerConfig
          .default
          .withText("Uploading files...")
          .withPrefixText("[upload]")
          .withSuffixText("(2 of 5)")
          .withSpinnerType(SpinnerType.moon)
          .withColor(Color.Yellow)
          .withIndent(2)
          .withEnabled(true)
      )
    handle.start()
    delay(3_000L).map { _ =>
      handle.fail(Some("Upload failed: connection timeout"))
    }
  }

  private def scenario3(): Future[Unit] = {
    println("\n=== Scenario 3: Updating text/color mid-spin, warn ===")
    val handle = Spinner
      .create(
        SpinnerConfig
          .default
          .withText("Step 1 of 3...")
          .withSpinnerType(SpinnerType.dots2)
          .withColor(Color.Blue)
          .withIndent(2)
          .withEnabled(true)
      )
    handle.start()
    for {
      _ <- delay(2_000L)
      _ = handle.updateText("Step 2 of 3...")
      _ = handle.updateColor(Color.Magenta)
      _ <- delay(2_000L)
      _ = handle.updateText("Step 3 of 3...")
      _ = handle.updateColor(Color.Green)
      _ <- delay(2_000L)
    } yield handle.warn(Some("Completed with warnings"))
  }

  private def scenario4(): Future[Unit] = {
    println("\n=== Scenario 4: Arrow spinner with info ===")
    val handle = Spinner
      .create(
        SpinnerConfig
          .default
          .withText("Scanning environment...")
          .withSpinnerType(SpinnerType.arrow)
          .withColor(Color.Gray)
          .withIndent(2)
          .withEnabled(true)
      )
    handle.start()
    delay(3_000L).map { _ =>
      handle.info(Some("Environment: Node.js, Scala.js"))
    }
  }

  private def scenario5(): Future[Unit] = {
    println("\n=== Scenario 5: dots3 spinner with succeed ===")
    val handle = Spinner
      .create(
        SpinnerConfig
          .default
          .withText("Compiling sources...")
          .withSpinnerType(SpinnerType.dots3)
          .withColor(Color.Green)
          .withIndent(2)
          .withEnabled(true)
      )
    handle.start()
    delay(3_000L).map { _ =>
      handle.succeed(Some("Compilation finished"))
    }
  }

  private def scenario6(): Future[Unit] = {
    println("\n=== Scenario 6: dots12 spinner with fail ===")
    val handle = Spinner
      .create(
        SpinnerConfig
          .default
          .withText("Linking native binary...")
          .withSpinnerType(SpinnerType.dots12)
          .withColor(Color.Red)
          .withIndent(2)
          .withEnabled(true)
      )
    handle.start()
    delay(3_000L).map { _ =>
      handle.fail(Some("Linking failed: missing symbol"))
    }
  }

  private def scenario7(): Future[Unit] = {
    println("\n=== Scenario 7: line spinner with warn ===")
    val handle = Spinner
      .create(
        SpinnerConfig
          .default
          .withText("Running tests...")
          .withSpinnerType(SpinnerType.line)
          .withColor(Color.Yellow)
          .withIndent(2)
          .withEnabled(true)
      )
    handle.start()
    delay(3_000L).map { _ =>
      handle.warn(Some("Tests passed with deprecation warnings"))
    }
  }

  private def scenario8(): Future[Unit] = {
    println("\n=== Scenario 8: bouncingBar spinner with info ===")
    val handle = Spinner
      .create(
        SpinnerConfig
          .default
          .withText("Downloading dependencies...")
          .withSpinnerType(SpinnerType.bouncingBar)
          .withColor(Color.Cyan)
          .withIndent(2)
          .withEnabled(true)
      )
    handle.start()
    delay(3_000L).map { _ =>
      handle.info(Some("Downloaded 42 dependencies"))
    }
  }

  private def scenario9(): Future[Unit] = {
    println("\n=== Scenario 9: arc spinner with succeed ===")
    val handle = Spinner
      .create(
        SpinnerConfig
          .default
          .withText("Formatting code...")
          .withSpinnerType(SpinnerType.arc)
          .withColor(Color.Magenta)
          .withIndent(2)
          .withEnabled(true)
      )
    handle.start()
    delay(3_000L).map { _ =>
      handle.succeed(Some("Code formatted"))
    }
  }

  private def scenario10(): Future[Unit] = {
    println("\n=== Scenario 10: toggle spinner with fail ===")
    val handle = Spinner
      .create(
        SpinnerConfig
          .default
          .withText("Connecting to server...")
          .withSpinnerType(SpinnerType.toggle)
          .withColor(Color.White)
          .withIndent(2)
          .withEnabled(true)
      )
    handle.start()
    delay(3_000L).map { _ =>
      handle.fail(Some("Connection refused"))
    }
  }

  private def scenario11(): Future[Unit] = {
    println("\n=== Scenario 11: clock spinner with warn ===")
    val handle = Spinner
      .create(
        SpinnerConfig
          .default
          .withText("Waiting for CI...")
          .withSpinnerType(SpinnerType.clock)
          .withColor(Color.Blue)
          .withIndent(2)
          .withEnabled(true)
      )
    handle.start()
    delay(3_000L).map { _ =>
      handle.warn(Some("CI passed but took longer than expected"))
    }
  }

  private def scenario12(): Future[Unit] = {
    println("\n=== Scenario 12: earth spinner with info ===")
    val handle = Spinner
      .create(
        SpinnerConfig
          .default
          .withText("Resolving DNS...")
          .withSpinnerType(SpinnerType.earth)
          .withColor(Color.Green)
          .withIndent(2)
          .withEnabled(true)
      )
    handle.start()
    delay(3_000L).map { _ =>
      handle.info(Some("Resolved to 127.0.0.1"))
    }
  }

  private def scenario13(): Future[Unit] = {
    println("\n=== Scenario 13: star spinner with succeed ===")
    val handle = Spinner
      .create(
        SpinnerConfig
          .default
          .withText("Generating report...")
          .withSpinnerType(SpinnerType.star)
          .withColor(Color.Yellow)
          .withIndent(2)
          .withEnabled(true)
      )
    handle.start()
    delay(4_000L).map { _ =>
      handle.succeed(Some("Report generated"))
    }
  }

  private def scenario14(): Future[Unit] = {
    println("\n=== Scenario 14: aesthetic spinner with fail ===")
    val handle = Spinner
      .create(
        SpinnerConfig
          .default
          .withText("Deploying to production...")
          .withSpinnerType(SpinnerType.aesthetic)
          .withColor(Color.Cyan)
          .withIndent(2)
          .withEnabled(true)
      )
    handle.start()
    delay(3_000L).map { _ =>
      handle.fail(Some("Deploy failed: health check timeout"))
    }
  }

  private def scenario15(): Future[Unit] = {
    println("\n=== Scenario 15: tableFlip spinner with warn ===")
    val handle = Spinner
      .create(
        SpinnerConfig
          .default
          .withText("Reviewing pull request...")
          .withSpinnerType(SpinnerType.tableFlip)
          .withColor(Color.Red)
          .withIndent(2)
          .withEnabled(true)
      )
    handle.start()
    delay(5_000L).map { _ =>
      handle.warn(Some("PR has merge conflicts"))
    }
  }

  def main(args: Array[String]): Unit = {
    val scenarios = for {
      _ <- delay(3_000L)
      _ <- scenario1()
      _ <- delay(2_100L)
      _ <- scenario2()
      _ <- delay(2_100L)
      _ <- scenario3()
      _ <- delay(2_100L)
      _ <- scenario4()
      _ <- delay(2_100L)
      _ <- scenario5()
      _ <- delay(2_100L)
      _ <- scenario6()
      _ <- delay(2_100L)
      _ <- scenario7()
      _ <- delay(2_100L)
      _ <- scenario8()
      _ <- delay(2_100L)
      _ <- scenario9()
      _ <- delay(2_100L)
      _ <- scenario10()
      _ <- delay(2_100L)
      _ <- scenario11()
      _ <- delay(2_100L)
      _ <- scenario12()
      _ <- delay(2_100L)
      _ <- scenario13()
      _ <- delay(2_100L)
      _ <- scenario14()
      _ <- delay(2_100L)
      _ <- scenario15()
      _ <- delay(10_000L)
    } yield ()

    scenarios.foreach { _ =>
      println("\nAll scenarios completed.")
    }
  }

}
