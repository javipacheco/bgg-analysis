ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "bgg_analysis"
  )

libraryDependencies ++= List(
  "co.fs2"    %% "fs2-core"     % "3.2.5",
  "co.fs2"    %% "fs2-io"       % "3.2.5",
  "org.gnieh" %% "fs2-data-csv" % "1.3.1"
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds"
)
