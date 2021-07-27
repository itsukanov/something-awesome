package com.itsukanov.company.info.restapi

import com.itsukanov.common.CompanyShortInfo
import com.itsukanov.common.restapi._
import io.circe.generic.auto._
import sttp.model.Headers
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{Endpoint, _}

object CompanyInfoEndpoint extends BaseEndpoint with PagingParams {

  val getAll: Endpoint[(Headers, BearerToken, Paging), ApiError, List[CompanyShortInfo], Any] =
    baseEndpoint
      .get
      .in(basePath / "company")
      .in(pagingIn)
      .out(jsonBody[List[CompanyShortInfo]])

  val getSingle: Endpoint[(Headers, BearerToken, String), ApiError, CompanyShortInfo, Any] =
    baseEndpoint
      .get
      .in(basePath / "company" / path[String]("ticker"))
      .out(jsonBody[CompanyShortInfo])

  val all = List(getAll, getSingle)

}
