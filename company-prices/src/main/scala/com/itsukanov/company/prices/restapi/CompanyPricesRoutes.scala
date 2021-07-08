package com.itsukanov.company.prices.restapi

import cats.Monad
import cats.effect.{BracketThrow, Concurrent, ContextShift, IO, Timer}
import com.itsukanov.common.restapi.{BaseRoutes, BearerToken, Endpoint2Rout}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.Provide
import io.janstenpickle.trace4cats.inject.{EntryPoint, Trace}
import org.http4s.HttpRoutes
import org.http4s.client.Client
import sttp.tapir.server.http4s.Http4sServerOptions

class CompanyPricesRoutes[
  F[_] : Concurrent : ContextShift : Timer,
  G[_] : BracketThrow : Trace
]
(ep: EntryPoint[F], client: Client[G])
(implicit serverOptions: Http4sServerOptions[F, F], P: Provide[F, G, Span[F]], authToken: BearerToken)
  extends BaseRoutes[F, G] with Endpoint2Rout {

  implicit val iep: EntryPoint[F] = ep

  private val getByTicker: HttpRoutes[F] = toRoutes1(CompanyPricesEndpoint.getByTicker) {
    ticker =>
      implicitly[Monad[G]] // todo it should retry
        .pure(CompanyPrices(Seq(1, 2, 3)))
        .toEither
  }

  val routes = getByTicker

}
