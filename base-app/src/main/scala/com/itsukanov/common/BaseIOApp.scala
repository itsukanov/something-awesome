package com.itsukanov.common

import cats.data.Kleisli
import cats.effect.{ContextShift, IO, Timer, _}
import com.itsukanov.common.restapi.{BearerToken, Config}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.`export`.CompleterConfig
import io.janstenpickle.trace4cats.base.context.ContextRoot
import io.janstenpickle.trace4cats.inject.EntryPoint
import io.janstenpickle.trace4cats.jaeger.JaegerSpanCompleter
import io.janstenpickle.trace4cats.kernel.SpanSampler
import io.janstenpickle.trace4cats.model.TraceProcess
import org.typelevel.log4cats.Logger

import scala.concurrent.duration._

trait BaseIOApp extends IOApp {

  implicit val cs: ContextShift[IO]     = IO.contextShift(scala.concurrent.ExecutionContext.global)
  implicit val t: Timer[IO]             = IO.timer(scala.concurrent.ExecutionContext.global)
  implicit val bearerToken: BearerToken = Config.defaultToken

  def entryPoint[F[_]: Concurrent: ContextShift: Timer: Logger](
       blocker: Blocker,
       process: TraceProcess): Resource[F, EntryPoint[F]] = {
    JaegerSpanCompleter[F](
      blocker,
      process,
      Config.jaeger.host,
      Config.jaeger.port,
      config = CompleterConfig(batchTimeout = 50.millis)
    ).map { completer =>
      EntryPoint[F](SpanSampler.always[F], completer)
    }
  }

  /** To use inside a traced context
    * todo it looks a bit strange - find out is it a good practice?
    */
  def tracedLogger(logger: Logger[IO]): Logger[Kleisli[IO, Span[IO], *]] =
    logger.mapK(ContextRoot.kleisliInstance[IO, Span[IO]].liftK)

}
