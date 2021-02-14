name := "transformer"

version := "0.1"

organization := "org.lowfi"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.beachape" %% "enumeratum" % "1.6.1",
  "com.opencsv" % "opencsv" % "5.3",
  "com.typesafe" % "config" % "1.4.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.scalatest" %% "scalatest" % "3.2.3" % Test
)

enablePlugins(JavaAppPackaging)
