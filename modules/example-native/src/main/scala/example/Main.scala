package example

import cats.Id
import effectie.instances.id.fx._
import just.spinner._

@SuppressWarnings(Array("org.wartremover.warts.ThreadSleep"))
@main def run(): Unit = {

  Thread.sleep(3_000L)
  // Scenario 1: Basic spinner with succeed
  println("\n=== Scenario 1: Basic spinner with succeed ===")
  val handle1 = Spinner
    .create[Id](
      SpinnerConfig
        .default
        .withText("Processing data...")
        .withColor(Color.cyan)
        .withIndent(2),
      TerminalOutput.stderr[Id],
      SpinnerTimer.create,
      SpinnerRefMaker.atomicRef[Id],
    )
  handle1.start()
  Thread.sleep(3_000L)
  handle1.succeed(Some("Data processed successfully"))

  Thread.sleep(2_100L)

  // Scenario 2: Spinner with prefix/suffix, moon type, fail outcome
  println("\n=== Scenario 2: Moon spinner with prefix/suffix, fail ===")
  val handle2 = Spinner
    .create[Id](
      SpinnerConfig
        .default
        .withText("Uploading files...")
        .withPrefixText("[upload]")
        .withSuffixText("(2 of 5)")
        .withSpinnerType(SpinnerType.moon)
        .withColor(Color.yellow)
        .withIndent(2),
      TerminalOutput.stderr[Id],
      SpinnerTimer.create,
      SpinnerRefMaker.atomicRef[Id],
    )
  handle2.start()
  Thread.sleep(3_000L)
  handle2.fail(Some("Upload failed: connection timeout"))

  Thread.sleep(2_100L)

  // Scenario 3: Spinner with mid-spin updates, warn outcome
  println("\n=== Scenario 3: Updating text/color mid-spin, warn ===")
  val handle3 = Spinner
    .create[Id](
      SpinnerConfig
        .default
        .withText("Step 1 of 3...")
        .withSpinnerType(SpinnerType.dots2)
        .withColor(Color.blue)
        .withIndent(2),
      TerminalOutput.stderr[Id],
      SpinnerTimer.create,
      SpinnerRefMaker.atomicRef[Id],
    )
  handle3.start()
  Thread.sleep(2_000L)
  handle3.updateText("Step 2 of 3...")
  handle3.updateColor(Color.Magenta)
  Thread.sleep(2_000L)
  handle3.updateText("Step 3 of 3...")
  handle3.updateColor(Color.Green)
  Thread.sleep(2_000L)
  handle3.warn(Some("Completed with warnings"))

  Thread.sleep(2_100L)

  // Scenario 4: Arrow spinner with info outcome
  println("\n=== Scenario 4: Arrow spinner with info ===")
  val handle4 = Spinner
    .create[Id](
      SpinnerConfig
        .default
        .withText("Scanning environment...")
        .withSpinnerType(SpinnerType.arrow)
        .withColor(Color.gray)
        .withIndent(2),
      TerminalOutput.stderr[Id],
      SpinnerTimer.create,
      SpinnerRefMaker.atomicRef[Id],
    )
  handle4.start()
  Thread.sleep(3_000L)
  handle4.info(Some("Environment: macOS, Scala Native 0.5"))

  Thread.sleep(2_100L)

  // Scenario 5: dots3 spinner with succeed
  println("\n=== Scenario 5: dots3 spinner with succeed ===")
  val handle5 = Spinner
    .create[Id](
      SpinnerConfig
        .default
        .withText("Compiling sources...")
        .withSpinnerType(SpinnerType.dots3)
        .withColor(Color.green)
        .withIndent(2),
      TerminalOutput.stderr[Id],
      SpinnerTimer.create,
      SpinnerRefMaker.atomicRef[Id],
    )
  handle5.start()
  Thread.sleep(3_000L)
  handle5.succeed(Some("Compilation finished"))

  Thread.sleep(2_100L)

  // Scenario 6: dots12 spinner with fail
  println("\n=== Scenario 6: dots12 spinner with fail ===")
  val handle6 = Spinner
    .create[Id](
      SpinnerConfig
        .default
        .withText("Linking native binary...")
        .withSpinnerType(SpinnerType.dots12)
        .withColor(Color.red)
        .withIndent(2),
      TerminalOutput.stderr[Id],
      SpinnerTimer.create,
      SpinnerRefMaker.atomicRef[Id],
    )
  handle6.start()
  Thread.sleep(3_000L)
  handle6.fail(Some("Linking failed: missing symbol"))

  Thread.sleep(2_100L)

  // Scenario 7: line spinner with warn
  println("\n=== Scenario 7: line spinner with warn ===")
  val handle7 = Spinner
    .create[Id](
      SpinnerConfig
        .default
        .withText("Running tests...")
        .withSpinnerType(SpinnerType.line)
        .withColor(Color.yellow)
        .withIndent(2),
      TerminalOutput.stderr[Id],
      SpinnerTimer.create,
      SpinnerRefMaker.atomicRef[Id],
    )
  handle7.start()
  Thread.sleep(3_000L)
  handle7.warn(Some("Tests passed with deprecation warnings"))

  Thread.sleep(2_100L)

  // Scenario 8: bouncingBar spinner with info
  println("\n=== Scenario 8: bouncingBar spinner with info ===")
  val handle8 = Spinner
    .create[Id](
      SpinnerConfig
        .default
        .withText("Downloading dependencies...")
        .withSpinnerType(SpinnerType.bouncingBar)
        .withColor(Color.cyan)
        .withIndent(2),
      TerminalOutput.stderr[Id],
      SpinnerTimer.create,
      SpinnerRefMaker.atomicRef[Id],
    )
  handle8.start()
  Thread.sleep(3_000L)
  handle8.info(Some("Downloaded 42 dependencies"))

  Thread.sleep(2_100L)

  // Scenario 9: arc spinner with succeed
  println("\n=== Scenario 9: arc spinner with succeed ===")
  val handle9 = Spinner
    .create[Id](
      SpinnerConfig
        .default
        .withText("Formatting code...")
        .withSpinnerType(SpinnerType.arc)
        .withColor(Color.magenta)
        .withIndent(2),
      TerminalOutput.stderr[Id],
      SpinnerTimer.create,
      SpinnerRefMaker.atomicRef[Id],
    )
  handle9.start()
  Thread.sleep(3_000L)
  handle9.succeed(Some("Code formatted"))

  Thread.sleep(2_100L)

  // Scenario 10: toggle spinner with fail
  println("\n=== Scenario 10: toggle spinner with fail ===")
  val handle10 = Spinner
    .create[Id](
      SpinnerConfig
        .default
        .withText("Connecting to server...")
        .withSpinnerType(SpinnerType.toggle)
        .withColor(Color.white)
        .withIndent(2),
      TerminalOutput.stderr[Id],
      SpinnerTimer.create,
      SpinnerRefMaker.atomicRef[Id],
    )
  handle10.start()
  Thread.sleep(3_000L)
  handle10.fail(Some("Connection refused"))

  Thread.sleep(2_100L)

  // Scenario 11: clock spinner with warn
  println("\n=== Scenario 11: clock spinner with warn ===")
  val handle11 = Spinner
    .create[Id](
      SpinnerConfig
        .default
        .withText("Waiting for CI...")
        .withSpinnerType(SpinnerType.clock)
        .withColor(Color.blue)
        .withIndent(2),
      TerminalOutput.stderr[Id],
      SpinnerTimer.create,
      SpinnerRefMaker.atomicRef[Id],
    )
  handle11.start()
  Thread.sleep(3_000L)
  handle11.warn(Some("CI passed but took longer than expected"))

  Thread.sleep(2_100L)

  // Scenario 12: earth spinner with info
  println("\n=== Scenario 12: earth spinner with info ===")
  val handle12 = Spinner
    .create[Id](
      SpinnerConfig
        .default
        .withText("Resolving DNS...")
        .withSpinnerType(SpinnerType.earth)
        .withColor(Color.green)
        .withIndent(2),
      TerminalOutput.stderr[Id],
      SpinnerTimer.create,
      SpinnerRefMaker.atomicRef[Id],
    )
  handle12.start()
  Thread.sleep(3_000L)
  handle12.info(Some("Resolved to 127.0.0.1"))

  Thread.sleep(2_100L)

  // Scenario 13: star spinner with succeed
  println("\n=== Scenario 13: star spinner with succeed ===")
  val handle13 = Spinner
    .create[Id](
      SpinnerConfig
        .default
        .withText("Generating report...")
        .withSpinnerType(SpinnerType.star)
        .withColor(Color.yellow)
        .withIndent(2),
      TerminalOutput.stderr[Id],
      SpinnerTimer.create,
      SpinnerRefMaker.atomicRef[Id],
    )
  handle13.start()
  Thread.sleep(4_000L)
  handle13.succeed(Some("Report generated"))

  Thread.sleep(2_100L)

  // Scenario 14: aesthetic spinner with fail
  println("\n=== Scenario 14: aesthetic spinner with fail ===")
  val handle14 = Spinner
    .create[Id](
      SpinnerConfig
        .default
        .withText("Deploying to production...")
        .withSpinnerType(SpinnerType.aesthetic)
        .withColor(Color.cyan)
        .withIndent(2),
      TerminalOutput.stderr[Id],
      SpinnerTimer.create,
      SpinnerRefMaker.atomicRef[Id],
    )
  handle14.start()
  Thread.sleep(3_000L)
  handle14.fail(Some("Deploy failed: health check timeout"))

  Thread.sleep(2_100L)

  // Scenario 15: tableFlip spinner with warn
  println("\n=== Scenario 15: tableFlip spinner with warn ===")
  val handle15 = Spinner
    .create[Id](
      SpinnerConfig
        .default
        .withText("Reviewing pull request...")
        .withSpinnerType(SpinnerType.tableFlip)
        .withColor(Color.red)
        .withIndent(2),
      TerminalOutput.stderr[Id],
      SpinnerTimer.create,
      SpinnerRefMaker.atomicRef[Id],
    )
  handle15.start()
  Thread.sleep(5_000L)
  handle15.warn(Some("PR has merge conflicts"))

  println("\nAll scenarios completed.")

  Thread.sleep(10_000L)
}
