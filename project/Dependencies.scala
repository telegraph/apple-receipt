import sbt._

object Dependencies {

  private lazy val JUnit     = "junit"         %  "junit"     % "4.12"
  private lazy val Scalatest = "org.scalatest" %% "scalatest" % "3.0.1"

  lazy val ProjectDependencies = Seq(
    "com.typesafe" % "config" % "1.3.1",
    "com.typesafe.scala-logging" % "scala-logging_2.11" % "3.7.2",
    "org.slf4j" % "slf4j-simple" % "1.7.25",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.5",
    "com.fasterxml.jackson.core" % "jackson-annotations" % "2.8.5",
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.5",
    "org.apache.commons" % "commons-lang3" % "3.7",
    "com.google.apis" % "google-api-services-androidpublisher" % "v2-rev20-1.21.0",
    "com.amazonaws" % "aws-lambda-java-core" % "1.1.0"

  )

  lazy val UnitTestDependencies = Seq(
    JUnit     % Test,
    Scalatest % Test
  )

  lazy val IntTestDependencies = Seq(
    JUnit     % IntegrationTest,
    Scalatest % IntegrationTest,

    // Cucumber Runner
    "com.waioeka.sbt"  %% "cucumber-runner"       % "0.0.5"  % IntegrationTest,

    "org.scala-sbt"    % "test-interface"         % "1.0"    % IntegrationTest,
    "info.cukes"       %  "cucumber-core"         % "1.2.5"  % IntegrationTest,
    "info.cukes"       %  "cucumber-jvm"          % "1.2.5"  % IntegrationTest,
    "info.cukes"       %  "cucumber-junit"        % "1.2.5"  % IntegrationTest,
    "info.cukes"       %% "cucumber-scala"        % "1.2.5"  % IntegrationTest,
    "net.serenity-bdd" %  "serenity-junit"        % "1.2.2"  % IntegrationTest,
    "net.serenity-bdd" %  "serenity-cucumber"     % "1.1.23" % IntegrationTest,
    "net.serenity-bdd" %  "serenity-rest-assured" % "1.2.2"  % IntegrationTest
  )
}