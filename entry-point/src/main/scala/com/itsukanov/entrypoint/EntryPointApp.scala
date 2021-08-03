package com.itsukanov.entrypoint

import cats.data.Kleisli
import cats.effect.{Blocker, ExitCode, IO, Resource}
import com.itsukanov.common.BaseIOApp
import com.itsukanov.common.restapi.{Config, RestApiServer}
import com.itsukanov.entrypoint.restapi.{EntryPointEndpoint, EntryPointRoutes}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.http4s.client.syntax.TracedClient
import io.janstenpickle.trace4cats.model.TraceProcess
import org.http4s.client.blaze.BlazeClientBuilder
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.ExecutionContext

object EntryPointApp extends BaseIOApp {

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      blocker                       <- Blocker[IO]
      implicit0(logger: Logger[IO]) <- Resource.eval(Slf4jLogger.create[IO])
      ep                            <- entryPoint[IO](blocker, TraceProcess("entry-point-app"))
      client <- BlazeClientBuilder[IO](ExecutionContext.global)
                  .withRequestTimeout(Config.requestTimeout)
                  .resource
      tracedClient = client.liftTrace()
    } yield (ep, tracedClient, logger))
      .use { case (ep, tracedClient, logger) =>
        implicit val iLogger: Logger[Kleisli[IO, Span[IO], *]] = tracedLogger(logger)

        RestApiServer.start(
          endpoints = EntryPointEndpoint.all,
          title = "Entry point app",
          routes = new EntryPointRoutes(ep, tracedClient),
          config = Config.entryPoint
        )
      }
      .as(ExitCode.Success)

}
