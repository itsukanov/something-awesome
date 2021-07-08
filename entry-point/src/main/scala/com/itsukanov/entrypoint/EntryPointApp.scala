package com.itsukanov.entrypoint

import cats.data.Kleisli
import cats.effect.IO
import com.itsukanov.common.restapi.{RestApiIOApp, RestApiServer, ServerConfig}
import com.itsukanov.entrypoint.restapi.{EntryPointEndpoint, EntryPointRoutes}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.inject.EntryPoint
import io.janstenpickle.trace4cats.model.TraceProcess
import org.http4s.client.Client

object EntryPointApp extends RestApiIOApp {

  override def traceProcess = TraceProcess("entry-point-app")

  def serverStart(ep: EntryPoint[IO],
                  client: Client[Kleisli[IO, Span[IO], *]]): IO[Unit] = {
    RestApiServer.start(
      endpoints = EntryPointEndpoint.all,
      title = "Entry point app",
      routes = new EntryPointRoutes(ep, client),
      config = ServerConfig("localhost", 8080) // todo move it to the config
    )
  }

}
