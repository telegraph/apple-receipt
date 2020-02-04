import sbt._

object Dependencies {

  private lazy val JUnit     = "junit"         %  "junit"     % "4.12"
  private lazy val Scalatest = "org.scalatest" %% "scalatest" % "3.0.1"
  private lazy val ScalaMock = "org.scalamock" %% "scalamock" % "4.1.0"

  lazy val ProjectDependencies = Seq(
    "com.typesafe" % "config" % "1.3.1",
    "org.slf4j" % "slf4j-simple" % "1.7.25",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.5",
    "com.fasterxml.jackson.core" % "jackson-annotations" % "2.8.5",
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.5",
    "org.apache.commons" % "commons-lang3" % "3.7",
    "com.google.apis" % "google-api-services-androidpublisher" % "v2-rev20-1.21.0",
    "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
    "org.apache.httpcomponents" % "httpclient" % "4.5.1",
    "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % "2.6.3",
    "org.scala-lang.modules" % "scala-parser-combinators_2.11" % "1.1.0",
    "com.fasterxml.jackson.core" % "jackson-core" % "2.6.3",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % "2.6.3",
    "joda-time" % "joda-time" % "2.9.1",
    "org.glassfish.jersey.core" % "jersey-client" % "2.13",
    "org.scalaj" % "scalaj-http_2.11" % "2.3.0",
    "org.mockito" % "mockito-all" % "1.10.19",
    "org.json4s" %% "json4s-native" % "3.5.3",
    "net.logstash.logback" % "logstash-logback-encoder" % "4.10",
    "ch.qos.logback" % "logback-classic" % "1.2.3" ,
    "ch.qos.logback" % "logback-core" % "1.2.3",
    "org.jlib" % "jlib-awslambda-logback" % "1.0.0",
    "io.reactivex" % "rxscala_2.11" % "0.22.0",
    "uk.co.telegraph.integration.common" %  "structure-logging-scala" % "1.2.8"

  )

  lazy val UnitTestDependencies = Seq(
    JUnit     % Test,
    Scalatest % Test,
    ScalaMock % Test
  )

  lazy val IntTestDependencies = Seq(
    JUnit     % IntegrationTest,
    Scalatest % IntegrationTest,
    ScalaMock % Test,

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