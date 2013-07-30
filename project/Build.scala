import sbt._
import Keys._

object BuildSettings
{

	val buildSettings = Defaults.defaultSettings ++ Seq(
			organization := "com.bryghts.kissjson",
			version      := "0.0.1-SNAPSHOT",
			scalaVersion := "2.10.1",
			scalacOptions ++= Seq()//,
//			libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.4.7"
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