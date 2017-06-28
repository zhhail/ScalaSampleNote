import Keys._

//logLevel := Level.Debug

//scalacOptions += -Ylog-classpath

val scala_v = "2.11.8"
val projectName = "scala-sample-note"

name := projectName

version := "1.0.0"

organization in ThisBuild := "com.zte.bigdata.zhanghl"

scalaVersion in ThisBuild := scala_v

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

libraryDependencies ++= Seq(
        "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
        "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
        "org.scala-lang.modules" %% "scala-swing" % "1.0.2")

autoCompilerPlugins := true

addCompilerPlugin("org.scala-lang.plugins" % "scala-continuations-plugin_2.11.8" % "1.0.2")

libraryDependencies += "org.scala-lang.plugins" %% "scala-continuations-library" % "1.0.2"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.6"

libraryDependencies += "io.spray" %% "spray-client" % "1.3.2"

libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.25"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.5.2"


scalacOptions += "-P:continuations:enable"


assemblyJarName := s"${projectName}_${scala_v.take(4)}.jar"

test in assembly := {}


