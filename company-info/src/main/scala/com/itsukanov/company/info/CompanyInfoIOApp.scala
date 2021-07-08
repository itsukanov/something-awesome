package com.itsukanov.company.info

import cats.data.Kleisli
import cats.effect.IO
import com.itsukanov.common.restapi.{RestApiIOApp, RestApiServer, ServerConfig}
import com.itsukanov.company.info.restapi.{CompanyInfoEndpoint, CompanyInfoRoutes}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.inject.EntryPoint
import io.janstenpickle.trace4cats.model.TraceProcess
import org.http4s.client.Client

object CompanyInfoIOApp extends RestApiIOApp {

  override def traceProcess = TraceProcess("company-info-app")

  def serverStart(ep: EntryPoint[IO],
                  client: Client[Kleisli[IO, Span[IO], *]]): IO[Unit] = {
    implicit val iep: EntryPoint[IO] = ep

    RestApiServer.start(
      endpoints = CompanyInfoEndpoint.all,
      title = "Company info app",
      routes = new CompanyInfoRoutes[IO, Kleisli[IO, Span[IO], *]],
      config = ServerConfig("localhost", 8081) // todo move it to the config
    )
  }
}
