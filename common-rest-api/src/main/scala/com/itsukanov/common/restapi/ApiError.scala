package com.itsukanov.common.restapi

sealed trait ApiError

case class AuthorizationError(details: String) extends ApiError
case class NotFound(what: String)              extends ApiError

object ApiError {
  val CompanyNotFound: ApiError    = NotFound("Company")
  val InvalidBearerToken: ApiError = AuthorizationError("invalid bearer token")
}
