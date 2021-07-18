package com.itsukanov.company.info.db

import cats.effect.{Async, Blocker, ContextShift, IO}
import doobie.Transactor
import doobie.util.ExecutionContexts
import org.testcontainers.containers.PostgreSQLContainer

object Postgres {

  private val container = new PostgreSQLContainer("postgres:9.6.12")
  private val logger = org.log4s.getLogger

  def start[F[_] : Async : ContextShift] = IO.delay { // todo refactor to Resource
    container.start()
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      logger.debug("stopping postgres")
      container.stop()
      logger.debug("postgres stopped")
    }))

    Transactor.fromDriverManager[F](
      container.getDriverClassName,
      container.getJdbcUrl,
      container.getUsername,
      container.getPassword,
      Blocker.liftExecutionContext(ExecutionContexts.synchronous)
    )
  }

}
