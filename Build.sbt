import SonatypeKeys._

sonatypeSettings

name := "KissJson"

organization := "com.bryghts.kissjson"

version      := "0.1.2"

scalaVersion := "2.10.3"

profileName  := "com.bryghts"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.3"

libraryDependencies += "org.specs2" %% "specs2" % "2.2.2" % "test"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.10.1" % "test"

libraryDependencies += "junit" % "junit" % "4.11" % "test"

libraryDependencies += "net.liftweb" % "lift-json_2.10" % "2.5.1" % "test"

libraryDependencies += "org.codehaus.jackson" % "jackson-core-asl" % "1.9.13" % "test"

libraryDependencies += "org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.13" % "test"

libraryDependencies += "net.minidev" % "json-smart" % "1.2" % "test"

libraryDependencies += "com.google.code.gson" % "gson" % "2.2.4" % "test"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.2.1" % "test"

libraryDependencies += "com.alibaba" % "fastjson" % "1.1.38" % "test"

libraryDependencies += "net.sf.json-lib" % "json-lib" % "2.4" % "test" classifier("jdk15")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

scalacOptions in Test ++= Seq("-Yrangepos")

publishMavenStyle := true

pomExtra := (
  <url>http://www.brights.com</url>
  <licenses>
    <license>
    <name>mit</name>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:marcesquerra/KissJson.git</url>
    <connection>scm:git:git@github.com:marcesquerra/KissJson.git</connection>
  </scm>
  <developers>
    <developer>
      <name>Marc Esquerrà i Bayo</name>
      <email>esquerra@bryghts.com</email>
    </developer>
  </developers>
)

