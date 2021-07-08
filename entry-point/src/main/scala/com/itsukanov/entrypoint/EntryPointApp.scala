package com.itsukanov.entrypoint

import cats.data.Kleisli
import cats.effect.IO
import com.itsukanov.common.restapi.{RestApiIOApp, RestApiServer, ServerConfig}
import com.itsukanov.entrypoint.restapi.{EntryPointEndpoint, EntryPointRoutes}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.inject.EntryPoint
import io.janstenpickle.trace4cats.model.TraceProcess

object EntryPointApp extends RestApiIOApp {

  override def traceProcess = TraceProcess("entry-point-service")

  override def serverStart(implicit ep: EntryPoint[IO]) = RestApiServer.start(
    endpoints = EntryPointEndpoint.all,
    title = "Entry point service",
    routes = new EntryPointRoutes[IO, Kleisli[IO, Span[IO], *]],
    config = ServerConfig("localhost", 8080) // todo move it to the config
  )

}
