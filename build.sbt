lazy val commonSettings = Seq(
  organization := "ru.ifmo",
  libraryDependencies += junitInterface,
  autoScalaLibrary := false,
  crossPaths := false,
  fork := true
)

lazy val junitInterface = "com.novocode" % "junit-interface" % "0.11" % "test"

lazy val root = project
  .in(file("."))
  .settings(commonSettings :_*)
  .settings(name    := "orthant-search",
            version := "0.0.0")
  .dependsOn(implementations, benchmarking)
  .aggregate(implementations, benchmarking)

lazy val implementations = project
  .in(file("implementations"))
  .settings(commonSettings :_*)
  .settings(name    := "orthant-search-implementations",
            version := "0.0.0")

lazy val benchmarking = project
  .in(file("benchmarking"))
  .settings(commonSettings :_*)
  .settings(name    := "orthant-search-benchmarking",
            version := "0.0.0")
  .dependsOn(implementations)
  .enablePlugins(JmhPlugin)
