import sbt._
import Keys._

object BuildSettings
{

	val buildSettings = Defaults.defaultSettings ++ Seq(
			organization := "com.bryghts.kissjson",
			version      := "0.1.0",
			scalaVersion := "2.10.3",
			scalacOptions ++= Seq(),
			libraryDependencies += "com.bryghts.kissnumber" % "kissnumber_2.10" % "0.0.3",
			libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.2",
			libraryDependencies += "org.specs2" %% "specs2" % "2.2.2" % "test",
			libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.10.1" % "test",
			libraryDependencies += "junit" % "junit" % "4.11" % "test",
			libraryDependencies += "net.liftweb" % "lift-json_2.10" % "2.5.1" % "test",
			libraryDependencies += "org.codehaus.jackson" % "jackson-core-asl" % "1.9.13" % "test",
			libraryDependencies += "org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.13" % "test",
			libraryDependencies += "net.minidev" % "json-smart" % "1.2" % "test",
			libraryDependencies += "com.google.code.gson" % "gson" % "2.2.4" % "test",
			libraryDependencies += "com.typesafe.play" %% "play-json" % "2.2.1" % "test",
			libraryDependencies += "com.alibaba" % "fastjson" % "1.1.38" % "test",
			libraryDependencies += "net.sf.json-lib" % "json-lib" % "2.4" % "test" classifier("jdk15"),
			resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
			scalacOptions in Test ++= Seq("-Yrangepos"),
			publishMavenStyle := true,
			publishTo <<= version { (v: String) =>
				val nexus = "https://oss.sonatype.org/"
				if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots") 
				else                             Some("releases"  at nexus + "service/local/staging/deploy/maven2")
			},
			publishArtifact in Test := false,
//			useGpg := true,
			pomExtra := (
				<url>http://www.brights.com</url>
				<licenses>
					<license>
					<name>mit</name>
					</license>
				</licenses>
				<scm>
					<url>git@github.com:marcesquerra/KissNumber.git</url>
					<connection>scm:git:git@github.com:marcesquerra/KissNumber.git</connection>
				</scm>
				<developers>
					<developer>
						<name>Marc Esquerrà i Bayo</name>
						<email>esquerra@bryghts.com</email>
					</developer>
				</developers>
			),
			pomIncludeRepository := { _ => false }
	)

}

object MyBuild extends Build
{
	val projectName = "KissJson"

	import BuildSettings._


	lazy val root: Project = Project(
		projectName,
		file("."),
		settings = buildSettings
	)

}
