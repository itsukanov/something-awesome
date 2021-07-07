package com.itsukanov.company.info.restapi

import cats.effect.{BracketThrow, ConcurrentEffect, ContextShift, Timer}
import com.itsukanov.common.restapi.{BearerToken, SwaggerHttp4s}
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.Provide
import io.janstenpickle.trace4cats.inject.{EntryPoint, Trace}
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.circe.yaml.RichOpenAPI

import scala.concurrent.ExecutionContext.Implicits.global

object CompanyInfoRestApi {
  def apply[
    F[_] : ConcurrentEffect : ContextShift : Timer: EntryPoint,
    G[_] : BracketThrow : Trace
  ]
  (implicit P: Provide[F, G, Span[F]]): F[Unit] = {
    val docs = OpenAPIDocsInterpreter.toOpenAPI(CompanyInfoEndpoint.all, "Company info service", "1.0")
    val docsRouts = new SwaggerHttp4s(docs.toYaml).routes[F]

    implicit val bearerToken: BearerToken = BearerToken("asd") // todo move to the config
    val routes = new CompanyInfoRouts[F, G].routes

    val httpApp = Router(
      "/" -> routes,
      "/" -> docsRouts
    ).orNotFound

    BlazeServerBuilder[F](global)
      .bindHttp(8081, "localhost") // todo move to the config
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
  }
}
