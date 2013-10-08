import sbt._
import Keys._

object BuildSettings
{

	val buildSettings = Defaults.defaultSettings ++ Seq(
			organization := "com.bryghts.kissjson",
			version      := "0.0.1-M3",
			scalaVersion := "2.10.2",
			scalacOptions ++= Seq(),
			libraryDependencies += "com.bryghts.kissnumber" % "kissnumber_2.10" % "0.0.3",
			libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.2",
			libraryDependencies += "org.specs2" %% "specs2" % "2.2.2" % "test",
			libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.10.1" % "test",
			libraryDependencies += "junit" % "junit" % "4.11" % "test",
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