package com.itsukanov.entrypoint.restapi

import cats.Monad
import cats.effect.{BracketThrow, Concurrent, ContextShift, Timer}
import cats.syntax.semigroupk._
import com.itsukanov.common.restapi.{BaseRoutes, BearerToken, Endpoint2Rout, Paging}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.Provide
import io.janstenpickle.trace4cats.inject.{EntryPoint, Trace}
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.Http4sServerOptions

class EntryPointRoutes[
  F[_] : Concurrent : ContextShift : Timer : EntryPoint,
  G[_] : BracketThrow : Trace
]
(implicit serverOptions: Http4sServerOptions[F, F], P: Provide[F, G, Span[F]], authToken: BearerToken)
  extends BaseRoutes[F, G] with Endpoint2Rout {

  private val getAll: HttpRoutes[F] = toRoutes1(EntryPointEndpoint.getAll) {
    case Paging(from, limit) =>
      implicitly[Monad[G]]
        .pure(List(CompanyShortInfo("Microsoft", "MSFT"), CompanyShortInfo("Amazon", "AMZN")))
        .toEither
  }

  private val getSingle: HttpRoutes[F] = toRoutes1(EntryPointEndpoint.getSingle) {
    ticker =>
      implicitly[Monad[G]]
        .pure(CompanyFullInfo("Microsoft", "MSFT", Seq(1.1, 2.2)))
        .toEither
  }

  private val clearCache: HttpRoutes[F] = toRoutes0(EntryPointEndpoint.clearCache) {
    implicitly[Monad[G]]
      .pure(())
      .toEither
  }

  val routes = getAll <+> getSingle <+> clearCache

}
