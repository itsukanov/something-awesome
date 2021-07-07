package com.itsukanov.entrypoint

import cats.data.Kleisli
import cats.effect.{ContextShift, IO, Timer, _}
import com.itsukanov.common.restapi.{BearerToken, RestApiServer, ServerConfig}
import com.itsukanov.entrypoint.restapi.{EntryPointEndpoint, EntryPointRoutes}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.`export`.CompleterConfig
import io.janstenpickle.trace4cats.inject.EntryPoint
import io.janstenpickle.trace4cats.jaeger.JaegerSpanCompleter
import io.janstenpickle.trace4cats.kernel.SpanSampler
import io.janstenpickle.trace4cats.model.TraceProcess
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration._

object EntryPointApp extends IOApp {

  implicit val cs: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.global)
  implicit val t: Timer[IO] = IO.timer(scala.concurrent.ExecutionContext.global)

  def entryPoint[F[_] : Concurrent : ContextShift : Timer : Logger](blocker: Blocker,
                                                                    process: TraceProcess): Resource[F, EntryPoint[F]] = {
    JaegerSpanCompleter[F](blocker, process,
      "localhost", 6831, // todo move it to the config
      config = CompleterConfig(batchTimeout = 50.millis)).map { completer =>
      EntryPoint[F](SpanSampler.always[F], completer)
    }
  }

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      blocker <- Blocker[IO]
      implicit0(logger: Logger[IO]) <- Resource.eval(Slf4jLogger.create[IO])
      ep <- entryPoint[IO](blocker, TraceProcess("entry-point-service"))
    } yield ep)
      .use { implicit ep =>
        implicit val bearerToken: BearerToken = BearerToken("asd") // todo move it to the config

        RestApiServer(
          endpoints = EntryPointEndpoint.all,
          title = "Entry point service",
          routes = new EntryPointRoutes[IO, Kleisli[IO, Span[IO], *]],
          config = ServerConfig("localhost", 8080)
        )
      }
      .as(ExitCode.Success)

}
