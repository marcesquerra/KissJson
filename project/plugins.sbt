addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.2.1")

// This resolver declaration is added by default in SBT 0.12.x
resolvers += Resolver.url(
	"sbt-plugin-releases",
	new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/")
)(Resolver.ivyStylePatterns)

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.3")
