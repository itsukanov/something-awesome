package com.itsukanov.company.info

import cats.data.Kleisli
import cats.effect.{Blocker, ExitCode, IO, Resource}
import com.itsukanov.common.restapi.{BaseIOApp, Config, RestApiServer}
import com.itsukanov.company.info.db.Postgres
import com.itsukanov.company.info.restapi.{CompanyInfoEndpoint, CompanyInfoRoutes}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.model.TraceProcess
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object CompanyInfoIOApp extends BaseIOApp {

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      blocker <- Blocker[IO]
      implicit0(logger: Logger[IO]) <- Resource.eval(Slf4jLogger.create[IO])
      ep <- entryPoint[IO](blocker, TraceProcess("company-info-app"))
    } yield ep)
      .use(implicit ep =>
        for {
          _ <- Postgres.start
          _ <- RestApiServer.start(
            endpoints = CompanyInfoEndpoint.all,
            title = "Company info app",
            routes = new CompanyInfoRoutes[IO, Kleisli[IO, Span[IO], *]],
            config = Config.companyInfo
          )
        } yield ()
      )
      .as(ExitCode.Success)

}
