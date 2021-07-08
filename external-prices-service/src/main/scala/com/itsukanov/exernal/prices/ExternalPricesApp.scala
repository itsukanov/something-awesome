package com.itsukanov.exernal.prices

import cats.data.Kleisli
import cats.effect.IO
import com.itsukanov.common.restapi.{Config, RestApiIOApp, RestApiServer}
import com.itsukanov.exernal.prices.restapi.{ExternalPricesEndpoint, ExternalPricesRoutes}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.inject.EntryPoint
import io.janstenpickle.trace4cats.model.TraceProcess
import org.http4s.client.Client

object ExternalPricesApp extends RestApiIOApp {

  override def traceProcess = TraceProcess("external-prices-app")

  def serverStart(ep: EntryPoint[IO],
                  client: Client[Kleisli[IO, Span[IO], *]]): IO[Unit] = {
    implicit val iep: EntryPoint[IO] = ep

    RestApiServer.start(
      endpoints = ExternalPricesEndpoint.all,
      title = "External prices app",
      routes = new ExternalPricesRoutes[IO, Kleisli[IO, Span[IO], *]],
      config = Config.externalCompanyPrices
    )
  }

}
