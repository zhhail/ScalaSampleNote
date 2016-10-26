import Keys._
import sbtassembly.Plugin.AssemblyKeys
import AssemblyKeys._

name := "scala-sample-note"

version := "1.0"

organization in ThisBuild := "com.zte.bigdata"

scalaVersion in ThisBuild := "2.10.4"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.5" % "test"

assemblySettings

jarName in assembly := "scala-sample-note.jar" 

test in assembly := {}

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

