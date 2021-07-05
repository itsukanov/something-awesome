import sbt._

object Dependencies {
  val tapirVersion = "0.18.0-M7"
  val http4sVersion = "0.21.22"
  val swaggerUiVersion = "3.47.1"
  val testcontainersVersion = "1.12.0"
  val trace4catsVersion = "0.10.1"

  val commonInfo = Seq(
    "org.apache.logging.log4j" % "log4j-core" % "2.14.1",
    "org.slf4j" % "slf4j-log4j12" % "2.0.0-alpha1",

    "io.janstenpickle" %% "trace4cats-core" % trace4catsVersion,
    "io.janstenpickle" %% "trace4cats-inject" % trace4catsVersion,
    "io.janstenpickle" %% "trace4cats-sttp-tapir" % trace4catsVersion,
    "io.janstenpickle" %% "trace4cats-http4s-client" % trace4catsVersion,
    "io.janstenpickle" %% "trace4cats-jaeger-thrift-exporter" % trace4catsVersion,

    "org.testcontainers" % "postgresql" % testcontainersVersion,
    "org.testcontainers" % "testcontainers" % testcontainersVersion,

    "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,

    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-client" % tapirVersion,
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "org.http4s" %% "http4s-circe" % http4sVersion,
    "org.webjars" % "swagger-ui" % swaggerUiVersion
  )

}
