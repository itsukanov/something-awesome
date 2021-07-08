package com.itsukanov.exernal.prices.restapi

import cats.Monad
import cats.effect.{BracketThrow, Concurrent, ContextShift, Timer}
import com.itsukanov.common.restapi.{BaseRoutes, BearerToken, Endpoint2Rout}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.Provide
import io.janstenpickle.trace4cats.inject.{EntryPoint, Trace}
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.Http4sServerOptions

class ExternalPricesRoutes[
  F[_] : Concurrent : ContextShift : Timer : EntryPoint,
  G[_] : BracketThrow : Trace
]
(implicit serverOptions: Http4sServerOptions[F, F], P: Provide[F, G, Span[F]], authToken: BearerToken)
  extends BaseRoutes[F, G] with Endpoint2Rout {

  private val getByTicker: HttpRoutes[F] = toRoutes1(ExternalPricesEndpoint.getByTicker) {
    ticker =>
      implicitly[Monad[G]] // todo it should fail from time to time
        .pure(CompanyPrices(Seq(1, 2, 3)))
        .toEither
  }

  val routes = getByTicker

}
