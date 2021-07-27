package com.itsukanov.company.prices.restapi

import cats.effect.{BracketThrow, Concurrent, ContextShift, Timer}
import cats.implicits._
import com.itsukanov.common.Companies
import com.itsukanov.common.problems.DefaultProblemsSimulator
import com.itsukanov.common.restapi.{ApiError, BaseRoutes, BearerToken, Endpoint2Rout}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.Provide
import io.janstenpickle.trace4cats.inject.{EntryPoint, Trace}
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger
import sttp.tapir.server.http4s.Http4sServerOptions

class CompanyPricesRoutes[
     F[_]: Concurrent: ContextShift: Timer: EntryPoint,
     G[_]: BracketThrow: Trace: Timer: Logger
](
  implicit serverOptions: Http4sServerOptions[F, F],
  P: Provide[F, G, Span[F]],
  authToken: BearerToken)
    extends BaseRoutes[F, G]
    with Endpoint2Rout
    with DefaultProblemsSimulator {

  private val notFound = ApiError.CompanyNotFound.asLeft[CompanyPrices]

  private val getByTicker: HttpRoutes[F] = toRoutes1(CompanyPricesEndpoint.getByTicker) { ticker =>
    simulateProblems {
      (Companies.allCompanies.find(_.ticker == ticker) match {
        case None           => notFound
        case Some(fullInfo) => CompanyPrices(fullInfo.prices).asRight[ApiError]
      }).pure[G]
    }
  }

  val routes = getByTicker

}
