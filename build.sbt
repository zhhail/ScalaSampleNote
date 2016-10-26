import Keys._
import sbtassembly.Plugin.AssemblyKeys
import AssemblyKeys._

name := "xxx-demo"

version := "1.0"

organization in ThisBuild := "com.zte.bigdata"

scalaVersion in ThisBuild := "2.10.4"

//libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.5" % "test"
//libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.0" % "test"

assemblySettings

jarName in assembly := "North-xmlreader.jar" 

test in assembly := {}

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

