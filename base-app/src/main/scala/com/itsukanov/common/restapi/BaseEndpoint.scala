package com.itsukanov.common.restapi

import io.circe.generic.auto._
import sttp.model.{Headers, StatusCode}
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.{EndpointInput, _}

trait BaseEndpoint {
  val basePath: EndpointInput[Unit] = "api" / "v1.0"

  val baseEndpoint = endpoint
    .in(headers.map(Headers.apply _)(_.headers.toList))
    .in(sttp.tapir.auth.bearer[BearerToken]())
    .errorOut(
      oneOf[ApiError](
        oneOfMapping(StatusCode.NotFound, jsonBody[NotFound].description("not found")),
        oneOfMapping(StatusCode.InternalServerError, jsonBody[InternalError].description("internal error")),
        oneOfMapping(
          StatusCode.Unauthorized,
          jsonBody[AuthorizationError].description("authorization error")
        )
      )
    )

}
