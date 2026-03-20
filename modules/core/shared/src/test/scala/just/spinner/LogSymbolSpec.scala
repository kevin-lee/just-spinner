package just.spinner

import hedgehog.*
import hedgehog.runner.*

object LogSymbolSpec extends Properties {

  override def tests: List[Test] = List(
    example("resolve returns unicode when supported", testResolveUnicode),
    example("resolve returns ascii when not supported", testResolveAscii),
    example("colored wraps with correct color", testColoredSymbols),
  )

  def testResolveUnicode: Result = {
    Result.all(
      List(
        (LogSymbol.resolve(LogSymbol.Info, UnicodeSupport.supported) ==== "\u2139"),
        (LogSymbol.resolve(LogSymbol.Success, UnicodeSupport.supported) ==== "\u2714"),
        (LogSymbol.resolve(LogSymbol.Warning, UnicodeSupport.supported) ==== "\u26a0"),
        (LogSymbol.resolve(LogSymbol.Error, UnicodeSupport.supported) ==== "\u2716"),
      )
    )
  }

  def testResolveAscii: Result = {
    Result.all(
      List(
        (LogSymbol.resolve(LogSymbol.Info, UnicodeSupport.unsupported) ==== "i"),
        (LogSymbol.resolve(LogSymbol.Success, UnicodeSupport.unsupported) ==== "\u221a"),
        (LogSymbol.resolve(LogSymbol.Warning, UnicodeSupport.unsupported) ==== "\u203c"),
        (LogSymbol.resolve(LogSymbol.Error, UnicodeSupport.unsupported) ==== "\u00d7"),
      )
    )
  }

  def testColoredSymbols: Result = {
    val successColored  = LogSymbol.colored(LogSymbol.Success, UnicodeSupport.supported)
    val expectedSuccess = AnsiCode.color("\u2714", Color.Green)
    val infoColored     = LogSymbol.colored(LogSymbol.Info, UnicodeSupport.supported)
    val expectedInfo    = AnsiCode.color("\u2139", Color.Blue)
    (successColored ==== expectedSuccess).and(infoColored ==== expectedInfo)
  }

}
