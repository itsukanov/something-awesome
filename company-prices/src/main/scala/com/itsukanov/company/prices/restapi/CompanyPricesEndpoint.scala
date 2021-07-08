package com.itsukanov.company.prices.restapi

import com.itsukanov.common.restapi._
import io.circe.generic.auto._
import sttp.model.Headers
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{Endpoint, _}

case class CompanyPrices(prices: Seq[Double])

object CompanyPricesEndpoint extends BaseEndpoint with PagingParams {

  val getByTicker: Endpoint[(Headers, BearerToken, String), ApiError, CompanyPrices, Any] =
    baseEndpoint
      .get
      .in(basePath / "prices")
      .in(query[String]("ticker").description("Company ticker").example("MSFT"))
      .out(jsonBody[CompanyPrices])

  val all = List(getByTicker)

}
