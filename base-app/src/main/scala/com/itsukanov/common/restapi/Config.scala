package com.itsukanov.common.restapi

case class ServerConfig(host: String, port: Int)

object Config {
  val defaultToken = BearerToken("123")
  val localHost    = "localhost"

  val entryPoint            = ServerConfig(localHost, 8080)
  val companyInfo           = ServerConfig(localHost, 8081)
  val companyPrices         = ServerConfig(localHost, 8082)
  val externalCompanyPrices = ServerConfig(localHost, 8082)

  val jaeger = ServerConfig(localHost, 6831)
}
