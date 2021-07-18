package com.itsukanov.company.info.restapi

import cats.Monad
import cats.effect.{BracketThrow, Concurrent, ContextShift, Timer}
import cats.syntax.semigroupk._
import com.itsukanov.common.restapi.{BaseRoutes, BearerToken, Endpoint2Rout, Paging}
import com.itsukanov.company.info.db.{CompanyShortInfo, CompanyShortInfoRepo}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.Provide
import io.janstenpickle.trace4cats.inject.{EntryPoint, Trace}
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.Http4sServerOptions

class CompanyInfoRoutes[
  F[_] : Concurrent : ContextShift : Timer : EntryPoint,
  G[_] : BracketThrow : Trace
]
(companiesRepo: CompanyShortInfoRepo[G])
(implicit serverOptions: Http4sServerOptions[F, F], P: Provide[F, G, Span[F]], authToken: BearerToken)
  extends BaseRoutes[F, G] with Endpoint2Rout {

  private val getAll: HttpRoutes[F] = toRoutes1(CompanyInfoEndpoint.getAll) {
    case Paging(from, limit) =>
      companiesRepo.getCompanies(from, limit)
        .toEither
  }

  private val getSingle: HttpRoutes[F] = toRoutes1(CompanyInfoEndpoint.getSingle) {
    ticker =>
      implicitly[Monad[G]]
        .pure(CompanyShortInfo("Microsoft", "MSFT"))
        .toEither
  }

  val routes = getAll <+> getSingle

}
