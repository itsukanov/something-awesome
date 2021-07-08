package com.itsukanov.company.prices

import cats.data.Kleisli
import cats.effect.IO
import com.itsukanov.common.restapi.{Config, RestApiIOApp, RestApiServer}
import com.itsukanov.company.prices.restapi.{CompanyPricesEndpoint, CompanyPricesRoutes}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.inject.EntryPoint
import io.janstenpickle.trace4cats.model.TraceProcess
import org.http4s.client.Client

object CompanyPricesApp extends RestApiIOApp {

  override def traceProcess = TraceProcess("company-prices-app")

  def serverStart(ep: EntryPoint[IO],
                  client: Client[Kleisli[IO, Span[IO], *]]): IO[Unit] = {
    RestApiServer.start(
      endpoints = CompanyPricesEndpoint.all,
      title = "Company prices app",
      routes = new CompanyPricesRoutes(ep, client),
      config = Config.companyPrices
    )
  }

}
