name := """user-management"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.mockito" % "mockito-core" % "1.9.5",
  specs2 % Test,
  "org.jmock" % "jmock-junit4" % "2.8.2"
)

