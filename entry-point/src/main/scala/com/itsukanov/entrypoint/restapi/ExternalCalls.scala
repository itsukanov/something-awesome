package com.itsukanov.entrypoint.restapi

import cats.effect.Sync
import cats.implicits._
import com.itsukanov.common.CompanyShortInfo
import com.itsukanov.common.restapi.{BearerToken, ServerConfig}
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.{Client, UnexpectedStatus}
import org.http4s.{Header, Headers, Request, _}

class ExternalCalls[G[_]: Sync](
  http4sClient: Client[G],
  authToken: BearerToken,
  companyInfoConfig: ServerConfig,
  companyPricesConfig: ServerConfig
) {

  private val authHeaders = Headers.of(
    Header("Authorization", s"Bearer ${authToken.token}")
  )

  private def handle404[T](gt: G[T]): G[Option[T]] =
    gt.map(Option(_)).recover { case UnexpectedStatus(Status(404)) =>
      Option.empty[T]
    }

  private def uri(path: String, port: Int) = Uri(
    path = path,
    authority = Some(Uri.Authority(port = Some(port)))
  )

  def getAllCompanies(from: Int, limit: Int): G[List[CompanyShortInfo]] = {
    val request = Request[G](
      method = Method.GET,
      uri = uri("/api/v1.0/company", companyInfoConfig.port)
        .withQueryParam("from", from)
        .withQueryParam("limit", limit),
      headers = authHeaders
    )

    http4sClient.expect[List[CompanyShortInfo]](request)
  }

  def getCompanyShortInfo(ticker: String): G[Option[CompanyShortInfo]] = {
    handle404(
      http4sClient.expect[CompanyShortInfo](
        Request[G](
          method = Method.GET,
          uri = uri(s"/api/v1.0/company/$ticker", companyInfoConfig.port),
          headers = authHeaders
        )
      )
    )
  }

  def getCompanyPrices(ticker: String): G[Option[CompanyPrices]] = {
    handle404(
      http4sClient.expect[CompanyPrices](
        Request[G](
          method = Method.GET,
          uri = uri(s"/api/v1.0/prices/$ticker", companyPricesConfig.port),
          headers = authHeaders
        )
      )
    )
  }

}
