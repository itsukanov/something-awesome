package com.itsukanov.entrypoint.restapi

import cats.Monad
import cats.effect.{BracketThrow, Concurrent, ContextShift, Sync, Timer}
import cats.implicits._
import com.itsukanov.common.restapi.{BaseRoutes, BearerToken, Endpoint2Rout, Paging}
import io.circe.generic.auto._
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.Provide
import io.janstenpickle.trace4cats.inject.{EntryPoint, Trace}
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client
import org.http4s.implicits._
import org.http4s.{HttpRoutes, Request, _}
import sttp.tapir.server.http4s.Http4sServerOptions

class EntryPointRoutes[
  F[_] : Concurrent : ContextShift : Timer,
  G[_] : BracketThrow : Trace : Sync
]
(ep: EntryPoint[F], client: Client[G])
(implicit serverOptions: Http4sServerOptions[F, F], P: Provide[F, G, Span[F]], authToken: BearerToken)
  extends BaseRoutes[F, G] with Endpoint2Rout {

  implicit val iep: EntryPoint[F] = ep

  private val getAll: HttpRoutes[F] = toRoutes1(EntryPointEndpoint.getAll) {
    case Paging(from, limit) =>
      val getAllRequest = Request[G](
        method = Method.GET,
        uri = uri"http://localhost:8081/api/v1.0/company"
          .withQueryParam("from", from)
          .withQueryParam("limit", limit),
        headers = Headers.of(
          Header("Authorization", s"Bearer ${authToken.token}")
        ))

      client.expect[List[CompanyShortInfo]](getAllRequest)
        .toEither
  }

  private val getSingle: HttpRoutes[F] = toRoutes1(EntryPointEndpoint.getSingle) {
    ticker =>
      implicitly[Monad[G]]
        .pure(CompanyFullInfo("Microsoft", "MSFT", Seq(1.1, 2.2)))
        .toEither
  }

  val routes = getAll <+> getSingle

}
