import Dependencies._
import sbt.Keys._

// give the user a nice default project!
lazy val buildNumber = sys.env.get("BUILD_NUMBER").map( bn => s"b$bn")

lazy val CommonSettings = Seq(
  name              := "apple-receipt",
  organization      := "uk.co.telegraph",
  version           := buildNumber.getOrElse("1.0.0"),
  scalaVersion      := "2.11.8",
  isSnapshot        := buildNumber.isEmpty,
  scalacOptions     += "-target:jvm-1.8",
  publishMavenStyle := false
)

lazy val CustomArtifacts = Seq(
  Artifact("name", "type", "extension")
)

lazy val root = (project in file(".")).
  configs ( IntegrationTest         ).
  settings( Defaults.itSettings: _* ).
  settings( CommonSettings     : _* ).
  settings(
    mainClass              in assembly := Some("uk.co.telegraph.applereceipt.Main"),
    target                 in assembly := file("target"),
    assemblyJarName        in assembly := s"${name.value}-${version.value}.jar",
    test                   in assembly := {},
    concurrentRestrictions             := Seq(
      Tags.limit(Tags.Test, 1)
    )
  ).
  settings(
    (stackCustomParams in DeployPreProd) += ("BuildVersion" -> version.value),
    (stackCustomParams in DeployProd   ) += ("BuildVersion" -> version.value),
    (stackTags in DeployPreProd) += ("Billing" -> "coreapi"),
    (stackTags in DeployProd) += ("Billing" -> "coreapi")
  )

(testFrameworks in IntegrationTest) += new TestFramework("com.waioeka.sbt.runner.CucumberFramework")

libraryDependencies ++=
  ProjectDependencies ++
  UnitTestDependencies ++
  IntTestDependencies

publishTo := {
    Some("commerce-artifacts-repo" at "s3://commerce-artifacts-repo/release")
}

// disable .jar publishing
publishArtifact in (Compile, packageBin) := false
publishArtifact in (Compile, packageDoc) := false
publishArtifact in (Compile, packageSrc) := false

addArtifact(artifact in (Compile, assembly), assembly)
assemblyMergeStrategy in assembly := {
  case "logback.xml"  => MergeStrategy.rename
  case "module-info.class" => MergeStrategy.first
  case PathList("org","apache","log4j", xs @ _*) => MergeStrategy.first
  case PathList("org","hamcrest", xs @ _*) => MergeStrategy.first
  case PathList("org","slf4j", "impl", "StaticLoggerBinder.class") => MergeStrategy.discard
  case PathList("org","slf4j", "impl", "StaticMDCBinder.class") => MergeStrategy.discard
  case PathList("org","slf4j", "impl", "StaticMarkerBinder.class") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}