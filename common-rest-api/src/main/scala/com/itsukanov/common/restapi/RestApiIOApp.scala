package com.itsukanov.common.restapi

import cats.effect.{ContextShift, IO, Timer, _}
import io.janstenpickle.trace4cats.`export`.CompleterConfig
import io.janstenpickle.trace4cats.inject.EntryPoint
import io.janstenpickle.trace4cats.jaeger.JaegerSpanCompleter
import io.janstenpickle.trace4cats.kernel.SpanSampler
import io.janstenpickle.trace4cats.model.TraceProcess
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration._

trait RestApiIOApp extends IOApp {

  implicit val cs: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.global)
  implicit val t: Timer[IO] = IO.timer(scala.concurrent.ExecutionContext.global)
  implicit val bearerToken: BearerToken = BearerToken("asd") // todo move it to the config

  def entryPoint[F[_] : Concurrent : ContextShift : Timer : Logger](blocker: Blocker,
                                                                    process: TraceProcess): Resource[F, EntryPoint[F]] = {
    JaegerSpanCompleter[F](blocker, process,
      "localhost", 6831, // todo move it to the config
      config = CompleterConfig(batchTimeout = 50.millis)).map { completer =>
      EntryPoint[F](SpanSampler.always[F], completer)
    }
  }

  def traceProcess: TraceProcess

  def serverStart(implicit ep: EntryPoint[IO]): IO[Unit]

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      blocker <- Blocker[IO]
      implicit0(logger: Logger[IO]) <- Resource.eval(Slf4jLogger.create[IO])
      ep <- entryPoint[IO](blocker, traceProcess)
    } yield ep)
      .use { ep =>
        serverStart(ep)
      }
      .as(ExitCode.Success)

}