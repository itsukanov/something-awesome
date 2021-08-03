package com.itsukanov.entrypoint.restapi

import cats.NonEmptyParallel
import cats.effect.{BracketThrow, Concurrent, ContextShift, Sync, Timer}
import cats.implicits._
import com.itsukanov.common.restapi._
import com.itsukanov.common.{CompanyFullInfo, CompanyShortInfo}
import com.itsukanov.entrypoint.Retries
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.Provide
import io.janstenpickle.trace4cats.inject.{EntryPoint, Trace}
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger
import retry.Sleep
import sttp.tapir.server.http4s.Http4sServerOptions

class EntryPointRoutes[
     F[_]: Concurrent: ContextShift: Timer,
     G[_]: BracketThrow: Trace: Sync: NonEmptyParallel: Sleep: Logger
](ep: EntryPoint[F], externalCalls: ExternalCalls[G])(
  implicit serverOptions: Http4sServerOptions[F, F],
  P: Provide[F, G, Span[F]],
  authToken: BearerToken)
    extends BaseRoutes[F, G]
    with Retries
    with Endpoint2Rout {

  implicit val iep: EntryPoint[F] = ep

  private val notFount = ApiError.CompanyNotFound.asLeft[CompanyFullInfo]

  private val getAll: HttpRoutes[F] = toRoutes1(EntryPointEndpoint.getAll) {
    case Paging(from, limit) =>
      logErrorWithTrace(externalCalls.getAllCompanies(from, limit).toEither)
  }

  private val getSingle: HttpRoutes[F] = toRoutes1(EntryPointEndpoint.getSingle) { ticker =>
    val pricesCall = withRetry("getting.CompanyPrices")(
      externalCalls.getCompanyPrices(ticker)
    )

    val commonInfoCall = withRetry("getting.CompanyShortInfo")(
      externalCalls.getCompanyShortInfo(ticker)
    )

    logErrorWithTrace((pricesCall, commonInfoCall).parMapN { case (pricesOpt, infoOpt) =>
      (for {
        CompanyPrices(prices)          <- pricesOpt
        CompanyShortInfo(name, ticker) <- infoOpt
      } yield CompanyFullInfo(name, ticker, prices)) match {
        case Some(fullInfo) => fullInfo.asRight[ApiError]
        case None           => notFount
      }
    })
  }

  val routes = getAll <+> getSingle

}
