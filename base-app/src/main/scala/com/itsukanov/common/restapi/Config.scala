package com.itsukanov.common.restapi

import scala.concurrent.duration._

case class ServerConfig(host: String, port: Int)

object Config {
  val defaultToken = BearerToken("123")
  val localHost    = "localhost"

  val entryPoint    = ServerConfig(localHost, 8080)
  val companyInfo   = ServerConfig(localHost, 8081)
  val companyPrices = ServerConfig(localHost, 8082)

  val requestTimeout = 2.seconds

  val jaeger = ServerConfig(localHost, 6831)
  val jaegerBatchTimeout = 50.millis
}
