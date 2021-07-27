package com.itsukanov.company.prices

import cats.data.Kleisli
import cats.effect.{Blocker, ExitCode, IO, Resource}
import com.itsukanov.common.BaseIOApp
import com.itsukanov.common.restapi.{Config, RestApiServer}
import com.itsukanov.company.prices.restapi.{CompanyPricesEndpoint, CompanyPricesRoutes}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.inject.EntryPoint
import io.janstenpickle.trace4cats.model.TraceProcess
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object CompanyPricesApp extends BaseIOApp {

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      blocker                       <- Blocker[IO]
      implicit0(logger: Logger[IO]) <- Resource.eval(Slf4jLogger.create[IO])
      ep                            <- entryPoint[IO](blocker, TraceProcess("company-prices-app"))
    } yield (ep, logger))
      .use { case (ep, logger) =>
        implicit val iep: EntryPoint[IO]                       = ep
        implicit val iLogger: Logger[Kleisli[IO, Span[IO], *]] = tracedLogger(logger)

        RestApiServer.start(
          endpoints = CompanyPricesEndpoint.all,
          title = "Company prices app",
          routes = new CompanyPricesRoutes[IO, Kleisli[IO, Span[IO], *]],
          config = Config.companyPrices
        )
      }
      .as(ExitCode.Success)

}
