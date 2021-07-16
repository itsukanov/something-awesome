package com.itsukanov.company.info.db

import cats.effect.IO
import org.testcontainers.containers.PostgreSQLContainer

object Postgres {

  private val container = new PostgreSQLContainer("postgres:9.6.12")
  private val logger = org.log4s.getLogger


  def start = IO.delay { // todo should it be F[_]?
    container.start()
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      logger.debug("stopping postgres")
      container.stop()
      logger.debug("postgres stopped")
    }))
  }

}
