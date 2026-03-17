import sbtcrossproject.CrossProject

ThisBuild / scalaVersion := props.ScalaVersion
ThisBuild / version := props.ProjectVersion
ThisBuild / organization := props.Org
ThisBuild / organizationName := props.OrgName
ThisBuild / developers := List(
  Developer(
    props.GitHubUsername,
    "Kevin Lee",
    "kevin.code@kevinlee.io",
    url(s"https://github.com/${props.GitHubUsername}"),
  )
)
ThisBuild / homepage := url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}").some
ThisBuild / scmInfo :=
  ScmInfo(
    url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}"),
    s"https://github.com/${props.GitHubUsername}/${props.RepoName}.git",
  ).some

lazy val root = (project in file("."))
  .settings(
    name := props.ProjectName
  )
  .settings(noPublish)
  .aggregate(
    coreJvm,
    coreJs,
    coreNative,
  )

lazy val core = module("core", crossProject(JVMPlatform, JSPlatform, NativePlatform))
  .settings(
    crossScalaVersions := props.CrossScalaVersions,
  )

lazy val coreJvm    = core.jvm
lazy val coreJs     = core.js.settings(jsSettingsForFuture)
lazy val coreNative = core.native.settings(nativeSettings)

lazy val exampleNative = (project in file("modules/example-native"))
  .enablePlugins(ScalaNativePlugin)
  .settings(
    name := prefixedProjectName("example-native"),
    scalaVersion := props.ScalaVersion,
    Compile / console / scalacOptions :=
      (console / scalacOptions)
        .value
        .filterNot(option => option.contains("wartremover") || option.contains("import")),
    scalacOptions ++= List("-no-indent", "-explain"),
//    wartremoverErrors ++= Warts.allBut(Wart.Any, Wart.Nothing, Wart.ImplicitConversion, Wart.ImplicitParameter),
//    wartremoverErrors ++= Set.empty[Wart],
    tpolecatExcludeOptions += org.typelevel.scalacoptions.ScalacOptions.warnNonUnitStatement,
  )
  .settings(noPublish)
  .settings(nativeSettings)
  .dependsOn(coreNative)

lazy val exampleJvm = (project in file("modules/example-jvm"))
  .settings(
    name := prefixedProjectName("example-jvm"),
    scalaVersion := props.ScalaVersion,
    Compile / unmanagedSourceDirectories += (ThisBuild / baseDirectory).value / "modules" / "example-native" / "src" / "main" / "scala",
    Compile / console / scalacOptions :=
      (console / scalacOptions)
        .value
        .filterNot(option => option.contains("wartremover") || option.contains("import")),
    scalacOptions ++= List("-no-indent", "-explain"),
    tpolecatExcludeOptions += org.typelevel.scalacoptions.ScalacOptions.warnNonUnitStatement,
  )
  .settings(noPublish)
  .dependsOn(coreJvm)

lazy val exampleJs = (project in file("modules/example-js"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := prefixedProjectName("example-js"),
    scalaVersion := props.ScalaVersion,
    Compile / console / scalacOptions :=
      (console / scalacOptions)
        .value
        .filterNot(option => option.contains("wartremover") || option.contains("import")),
    scalacOptions ++= List("-no-indent", "-explain"),
    tpolecatExcludeOptions ++= Set(
      org.typelevel.scalacoptions.ScalacOptions.warnNonUnitStatement,
      org.typelevel.scalacoptions.ScalacOptions.warnValueDiscard,
    ),
    scalaJSUseMainModuleInitializer := true,
  )
  .settings(jsSettingsForFuture)
  .settings(noPublish)
  .dependsOn(coreJs)

lazy val props =
  new {

    private val gitHubRepo = findRepoOrgAndName

    val GitHubUsername = gitHubRepo.fold("kevin-lee")(_.orgToString)
    val RepoName       = gitHubRepo.fold("just-spinner")(_.nameToString)
    val ProjectName    = RepoName

    val ScalaVersion       = "3.3.7"
    val CrossScalaVersions = List("2.13.18", "3.3.7")
    val Org                = "io.kevinlee"
    val OrgName            = "Kevin's Code"

    val ProjectVersion = "0.1.0-SNAPSHOT"

    lazy val licenses = List(License.MIT)

    val ExtrasVersion = "0.51.0"

    val HedgehogVersion = "0.13.0"

    val IncludeTest: String = "compile->compile;test->test"
  }

lazy val libs =
  new {

    lazy val tests = new {
      lazy val hedgehog = Def.setting(
        List(
          "qa.hedgehog" %%% "hedgehog-core"   % props.HedgehogVersion % Test,
          "qa.hedgehog" %%% "hedgehog-runner" % props.HedgehogVersion % Test,
          "qa.hedgehog" %%% "hedgehog-sbt"    % props.HedgehogVersion % Test,
        )
      )

      lazy val extrasHedgehogCe3 = Def.setting("io.kevinlee" %%% "extras-hedgehog-ce3" % props.ExtrasVersion % Test)
    }

  }

def isScala3(scalaVersion: String): Boolean = scalaVersion.startsWith("3.")

// format: off
def prefixedProjectName(name: String) = s"${props.RepoName}${if (name.isEmpty) "" else s"-$name"}"
// format: on

////

def module(projectName: String, crossProject: CrossProject.Builder): CrossProject = {
  val names          = projectName.split("/")
  val theProjectName = names.last
  val location       = names.toList
  val prefixedName   = prefixedProjectName(theProjectName)
  commonModule(prefixedName, location, crossProject)
}

def testModule(projectName: String, crossProject: CrossProject.Builder): CrossProject = {

  val names          = projectName.split("/")
  val theProjectName = names.last

  val prefixedName = s"test-${prefixedProjectName(theProjectName)}"
  val location     = (names.init :+ prefixedName).toList

  commonModule(prefixedName, location, crossProject)
}

def commonModule(prefixedName: String, path: List[String], crossProject: CrossProject.Builder): CrossProject = {
  val modulePath = file(("modules" :: path).mkString("/"))
  List(
    modulePath / "shared" / "src" / "main" / "scala",
    modulePath / "shared" / "src" / "test" / "scala",
  ).foreach(IO.createDirectory)
  crossProject
    .in(modulePath)
    .settings(
      name := prefixedName,
      fork := true,
      semanticdbEnabled := true,
      scalafixConfig := (
        if (scalaVersion.value.startsWith("3"))
          ((ThisBuild / baseDirectory).value / ".scalafix-scala3.conf").some
        else
          ((ThisBuild / baseDirectory).value / ".scalafix-scala2.conf").some
      ),
      scalacOptions ++= (if (isScala3(scalaVersion.value)) List("-no-indent", "-explain") else List("-Xsource:3")),
      //      scalacOptions ~= (ops => ops.filter(_ != "UTF-8")),
      libraryDependencies ++= libs.tests.hedgehog.value,
      wartremoverErrors ++= Warts.allBut(Wart.Any, Wart.Nothing, Wart.ImplicitConversion, Wart.ImplicitParameter),
      Compile / console / scalacOptions :=
        (console / scalacOptions)
          .value
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
      Test / console / scalacOptions :=
        (console / scalacOptions)
          .value
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
      /* } WartRemover and scalacOptions */
      licenses := props.licenses,
      /* coverage { */
      coverageHighlighting := (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 10)) | Some((2, 11)) =>
          false
        case _ =>
          true
      }),
      /* } coverage */
    )
}

lazy val jsSettingsForFuture: SettingsDefinition = List(
  Test / fork := false,
  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
  scalacOptions ++= (if (scalaVersion.value.startsWith("3")) List.empty
                     else List("-Wconf:msg=dead code following this construct:s")),
  Test / scalacOptions ++= (if (scalaVersion.value.startsWith("3")) List.empty
                            else List("-P:scalajs:nowarnGlobalExecutionContext")),
  Test / compile / scalacOptions ++= (if (scalaVersion.value.startsWith("3")) List.empty
                                      else List("-P:scalajs:nowarnGlobalExecutionContext")),
  coverageEnabled := false,
)

lazy val nativeSettings: SettingsDefinition = List(
  Test / fork := false,
  coverageEnabled := false,
)
