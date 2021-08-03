package com.itsukanov.common.restapi

import cats.MonadThrow
import cats.effect.{BracketThrow, Concurrent, ContextShift, Timer}
import cats.implicits._
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.Provide
import io.janstenpickle.trace4cats.inject.{EntryPoint, Trace}
import io.janstenpickle.trace4cats.sttp.tapir.syntax._
import org.http4s.client.UnexpectedStatus
import org.http4s.{HttpRoutes, Status}
import org.typelevel.log4cats.Logger
import sttp.capabilities.WebSockets
import sttp.capabilities.fs2.Fs2Streams
import sttp.model.Headers
import sttp.tapir.Endpoint
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}

trait Endpoint2Rout {

  implicit class GOps[G[_]: MonadThrow, T](gt: G[T]) {
    def toEither = gt.map(_.asRight[ApiError])

    def handle404: G[Option[T]] = gt.map(Option(_)).recover { case UnexpectedStatus(Status(404)) =>
      Option.empty[T]
    }

  }

  def logErrorWithTrace[T, O, G[_]: Trace](action: G[Either[ApiError, O]])(
       implicit BT: BracketThrow[G],
       L: Logger[G]): G[Either[ApiError, O]] = {
    BT.recoverWith(action) { case thr: Throwable =>
      for {
        traceId <- Trace[G]
                     .traceId
                     .map(
                       _.getOrElse(throw new RuntimeException("traceId is not set"))
                     ) // todo when traceId can be missing?
        _ <- L.error(thr)(s"TraceId = $traceId, ${thr.getMessage}")
        apiError = InternalError.fromTraceId(traceId).asLeft[O]
      } yield apiError
    }
  }

  def toRoutes0[
       O,
       F[_]: Concurrent: ContextShift: Timer,
       G[_]: BracketThrow: Trace
  ](
       endpoint: Endpoint[(Headers, BearerToken), ApiError, O, Fs2Streams[F] with WebSockets]
  )(f: => G[Either[ApiError, O]])(
       implicit authToken: BearerToken,
       serverOptions: Http4sServerOptions[F, F],
       entryPoint: EntryPoint[F],
       P: Provide[F, G, Span[F]]): HttpRoutes[F] = {
    val serverEndpoint = endpoint
      .serverLogic { case (_, bearerToken) =>
        if (bearerToken == authToken) f
        else ApiError.InvalidBearerToken.asLeft[O].pure[G]
      }
      .inject(
        entryPoint,
        _._1
      )

    Http4sServerInterpreter.toRoutes(serverEndpoint)
  }

  def toRoutes1[
       I1, // todo how to solve a common case instead of "I1, I2, I3"?
       O,
       F[_]: Concurrent: ContextShift: Timer,
       G[_]: Trace: Logger
  ](
       endpoint: Endpoint[(Headers, BearerToken, I1), ApiError, O, Fs2Streams[F] with WebSockets]
  )(f: I1 => G[Either[ApiError, O]])(
       implicit authToken: BearerToken,
       serverOptions: Http4sServerOptions[F, F],
       entryPoint: EntryPoint[F],
       BT: BracketThrow[G],
       P: Provide[F, G, Span[F]]): HttpRoutes[F] = {
    val serverEndpoint = endpoint
      .serverLogic { case (_, bearerToken, i1) =>
        if (bearerToken == authToken) f(i1)
        else ApiError.InvalidBearerToken.asLeft[O].pure[G]
      }
      .inject(
        entryPoint,
        _._1
      )

    Http4sServerInterpreter.toRoutes(serverEndpoint)
  }

}
