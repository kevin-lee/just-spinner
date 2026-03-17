package just.spinner

import hedgehog._
import hedgehog.runner._

object LogSymbolSpec extends Properties {

  override def tests: List[Test] = List(
    example("resolve returns unicode when supported", testResolveUnicode),
    example("resolve returns ascii when not supported", testResolveAscii),
    example("colored wraps with correct color", testColoredSymbols),
  )

  def testResolveUnicode: Result = {
    Result.all(
      List(
        (LogSymbol.resolve(LogSymbol.Info, unicodeSupported = true) ==== "\u2139"),
        (LogSymbol.resolve(LogSymbol.Success, unicodeSupported = true) ==== "\u2714"),
        (LogSymbol.resolve(LogSymbol.Warning, unicodeSupported = true) ==== "\u26a0"),
        (LogSymbol.resolve(LogSymbol.Error, unicodeSupported = true) ==== "\u2716"),
      )
    )
  }

  def testResolveAscii: Result = {
    Result.all(
      List(
        (LogSymbol.resolve(LogSymbol.Info, unicodeSupported = false) ==== "i"),
        (LogSymbol.resolve(LogSymbol.Success, unicodeSupported = false) ==== "\u221a"),
        (LogSymbol.resolve(LogSymbol.Warning, unicodeSupported = false) ==== "\u203c"),
        (LogSymbol.resolve(LogSymbol.Error, unicodeSupported = false) ==== "\u00d7"),
      )
    )
  }

  def testColoredSymbols: Result = {
    val successColored  = LogSymbol.colored(LogSymbol.Success, unicodeSupported = true)
    val expectedSuccess = AnsiCode.color("\u2714", Color.Green)
    val infoColored     = LogSymbol.colored(LogSymbol.Info, unicodeSupported = true)
    val expectedInfo    = AnsiCode.color("\u2139", Color.Blue)
    (successColored ==== expectedSuccess).and(infoColored ==== expectedInfo)
  }

}
