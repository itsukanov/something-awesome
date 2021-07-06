package com.itsukanov.entrypoint.restapi

import com.itsukanov.common.restapi._
import io.circe.generic.auto._
import sttp.model.Headers
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{Endpoint, _}

case class CompanyShortInfo(name: String, ticker: String)

object EntryPointEndpoint extends BaseEndpoint with PagingParams {

  val getAll: Endpoint[(Headers, BearerToken, Paging), ApiError, List[CompanyShortInfo], Any] =
    baseEndpoint
      .get
      .in(basePath / "company")
      .in(pagingIn)
      .out(jsonBody[List[CompanyShortInfo]])

//  val getSingle = // todo implement

  val all = List(getAll)

}
