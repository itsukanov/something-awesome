package com.itsukanov.company.prices

import cats.data.Kleisli
import cats.effect.IO
import com.itsukanov.common.restapi.{RestApiIOApp, RestApiServer, ServerConfig}
import com.itsukanov.company.prices.restapi.{CompanyPricesEndpoint, CompanyPricesRoutes}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.inject.EntryPoint
import io.janstenpickle.trace4cats.model.TraceProcess

object CompanyPricesApp extends RestApiIOApp {

  override def traceProcess = TraceProcess("company-prices-app")

  override def serverStart(implicit ep: EntryPoint[IO]) = RestApiServer.start(
    endpoints = CompanyPricesEndpoint.all,
    title = "Company prices app",
    routes = new CompanyPricesRoutes[IO, Kleisli[IO, Span[IO], *]],
    config = ServerConfig("localhost", 8082) // todo move it to the config
  )

}
