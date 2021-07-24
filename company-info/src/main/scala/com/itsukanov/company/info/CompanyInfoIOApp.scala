package com.itsukanov.company.info

import cats.data.Kleisli
import cats.effect.{Blocker, ExitCode, IO, Resource}
import com.itsukanov.common.BaseIOApp
import com.itsukanov.common.restapi.{Config, RestApiServer}
import com.itsukanov.company.info.db.{CompanyShortInfoDDL, CompanyShortInfoRepo, Postgres}
import com.itsukanov.company.info.restapi.{CompanyInfoEndpoint, CompanyInfoRoutes}
import io.janstenpickle.trace4cats.inject.EntryPoint
import io.janstenpickle.trace4cats.model.{SpanContext, TraceProcess}
import io.janstenpickle.trace4cats.{NoopSpan, Span}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object CompanyInfoIOApp extends BaseIOApp {

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      blocker                       <- Blocker[IO]
      implicit0(logger: Logger[IO]) <- Resource.eval(Slf4jLogger.create[IO])
      ep                            <- entryPoint[IO](blocker, TraceProcess("company-info-app"))
      postgresContainer             <- Postgres.start[IO]
    } yield (ep, postgresContainer))
      .use { case (ep, postgres) =>
        implicit val iep: EntryPoint[IO] = ep

        val xa            = Postgres.getTransactor[Kleisli[IO, Span[IO], *]](postgres)
        val companiesRepo = new CompanyShortInfoRepo(xa)

        for {
          _ <- CompanyShortInfoDDL.initDB(xa).run(NoopSpan(SpanContext.invalid))
          _ <- RestApiServer.start(
                 endpoints = CompanyInfoEndpoint.all,
                 title = "Company info app",
                 routes = new CompanyInfoRoutes[IO, Kleisli[IO, Span[IO], *]](companiesRepo),
                 config = Config.companyInfo
               )
        } yield ()
      }
      .as(ExitCode.Success)

}
