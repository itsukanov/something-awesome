package com.itsukanov.common.restapi

sealed trait ApiError

case class AuthorizationError(details: String) extends ApiError
case class NotFound(what: String)              extends ApiError
case class InternalError(details: String)      extends ApiError

object InternalError {

  def fromTraceId(traceId: String): ApiError = InternalError(s"TraceId = $traceId")

}

object ApiError {
  val CompanyNotFound: ApiError    = NotFound("Company")
  val InvalidBearerToken: ApiError = AuthorizationError("invalid bearer token")
}
