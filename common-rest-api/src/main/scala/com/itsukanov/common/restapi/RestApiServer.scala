package com.itsukanov.common.restapi

import cats.effect.{BracketThrow, ConcurrentEffect, ContextShift, Timer}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.Provide
import io.janstenpickle.trace4cats.inject.{EntryPoint, Trace}
import org.http4s.HttpRoutes
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import sttp.tapir.Endpoint
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.circe.yaml.RichOpenAPI

import scala.concurrent.ExecutionContext.Implicits.global

case class ServerConfig(host: String, port: Int)

/*
  We need BaseRouts to catch F and G, and declare [IO, Kleisli[IO, Span[IO], *]] only once during RestApiServer.apply
 */
abstract class BaseRoutes[F[_], G[_]] {
  def routes: HttpRoutes[F]
}

object RestApiServer {

  def apply[
    F[_] : ConcurrentEffect : ContextShift : Timer : EntryPoint,
    G[_] : BracketThrow : Trace
  ](endpoints: Seq[Endpoint[_, _, _, _]],
    title: String,
    routes: BaseRoutes[F, G],
    config: ServerConfig
   )(implicit P: Provide[F, G, Span[F]], bearerToken: BearerToken): F[Unit] = {
    val docs = OpenAPIDocsInterpreter.toOpenAPI(endpoints, title, "1.0")
    val docsRouts = new SwaggerHttp4s(docs.toYaml).routes[F]

    val httpApp = Router(
      "/" -> routes.routes,
      "/" -> docsRouts
    ).orNotFound

    BlazeServerBuilder[F](global)
      .bindHttp(config.port, config.host)
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
  }
}
