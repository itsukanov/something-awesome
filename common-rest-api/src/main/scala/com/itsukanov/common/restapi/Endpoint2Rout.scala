package com.itsukanov.common.restapi

import cats.Functor
import cats.effect.{BracketThrow, Concurrent, ContextShift, Timer}
import cats.implicits._
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.Provide
import io.janstenpickle.trace4cats.inject.{EntryPoint, Trace}
import io.janstenpickle.trace4cats.sttp.tapir.syntax._
import org.http4s.HttpRoutes
import sttp.capabilities.WebSockets
import sttp.capabilities.fs2.Fs2Streams
import sttp.model.Headers
import sttp.tapir.Endpoint
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}

trait Endpoint2Rout {

  implicit class GOps[G[_]: Functor, T](g: G[T]) {
    def toEither = g.map(_.asRight[ApiError])
  }

  def toRoutes1[
    I, O,
    F[_] : Concurrent : ContextShift : Timer,
    G[_] : BracketThrow : Trace
  ](
     endpoint: Endpoint[(Headers, BearerToken, I), ApiError, O, Fs2Streams[F] with WebSockets]
   )(f: I => G[Either[ApiError, O]])
   (implicit authToken: BearerToken,
    serverOptions: Http4sServerOptions[F, F],
    entryPoint: EntryPoint[F],
    P: Provide[F, G, Span[F]]
   ): HttpRoutes[F] = {
    val serverEndpoint = endpoint.serverLogic {
      case (_, bearerToken, i1) =>
        if (bearerToken == authToken) f(i1)
        else ApiError.InvalidBearerToken.asLeft[O].pure[G]
    }.inject(
      entryPoint,
      _._1
    )

    Http4sServerInterpreter.toRoutes(serverEndpoint)
  }

  def toRoutes3[
    I1, I2, I3, O, // todo how to solve a common case instead of "I1, I2, I3"?
    F[_] : Concurrent : ContextShift : Timer,
    G[_] : BracketThrow : Trace
  ](
     endpoint: Endpoint[(Headers, BearerToken, I1, I2, I3), ApiError, O, Any]
   )(f: (I1, I2, I3)  => G[Either[ApiError, O]])
   (implicit authToken: BearerToken,
    serverOptions: Http4sServerOptions[F, F],
    entryPoint: EntryPoint[F],
    P: Provide[F, G, Span[F]]
   ): HttpRoutes[F] = {
    val serverEndpoint = endpoint.serverLogic {
      case (_, bearerToken, i1, i2, i3) =>
        if (bearerToken == authToken) f(i1, i2, i3)
        else ApiError.InvalidBearerToken.asLeft[O].pure[G]
    }.inject(
      entryPoint,
      _._1
    )

    Http4sServerInterpreter.toRoutes(serverEndpoint)
  }

}
