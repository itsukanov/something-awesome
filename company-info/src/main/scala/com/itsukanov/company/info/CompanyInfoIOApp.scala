package com.itsukanov.company.info

import cats.data.Kleisli
import cats.effect.{Blocker, ExitCode, IO, Resource}
import com.itsukanov.common.restapi.{BaseIOApp, Config, RestApiServer}
import com.itsukanov.company.info.db.{CompanyShortInfoDDL, CompanyShortInfoRepo, Postgres}
import com.itsukanov.company.info.restapi.{CompanyInfoEndpoint, CompanyInfoRoutes}
import doobie.implicits._
import io.janstenpickle.trace4cats.model.{SpanContext, TraceProcess}
import io.janstenpickle.trace4cats.{NoopSpan, Span}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object CompanyInfoIOApp extends BaseIOApp {

  val companiesRepoIO: IO[CompanyShortInfoRepo[Kleisli[IO, Span[IO], *]]] = for {
    xa <- Postgres.start[Kleisli[IO, Span[IO], *]]
    _ <- CompanyShortInfoDDL.create.transact(xa).run(NoopSpan(SpanContext.invalid))
    _ <- CompanyShortInfoDDL.insert.transact(xa).run(NoopSpan(SpanContext.invalid))
  } yield new CompanyShortInfoRepo[Kleisli[IO, Span[IO], *]](xa)

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      blocker <- Blocker[IO]
      implicit0(logger: Logger[IO]) <- Resource.eval(Slf4jLogger.create[IO])
      ep <- entryPoint[IO](blocker, TraceProcess("company-info-app"))
    } yield ep)
      .use(implicit ep =>
        for {
          companiesRepo <- companiesRepoIO
          _ <- RestApiServer.start(
            endpoints = CompanyInfoEndpoint.all,
            title = "Company info app",
            routes = new CompanyInfoRoutes[IO, Kleisli[IO, Span[IO], *]](companiesRepo),
            config = Config.companyInfo
          )
        } yield ()
      )
      .as(ExitCode.Success)

}
