package com.itsukanov.company.info.db

import cats.effect.{Async, Blocker, ContextShift, Resource}
import doobie.Transactor
import doobie.util.ExecutionContexts
import org.testcontainers.containers.PostgreSQLContainer

object Postgres {

  private val logger = org.log4s.getLogger

  def start[F[_]: ContextShift](
       implicit F: Async[F]): Resource[F, PostgreSQLContainer[_]] =
    Resource.make(F.delay {
      val container = new PostgreSQLContainer("postgres:9.6.12")
      logger.info("starting postgres")
      container.start()
      container
    })(container =>
      F.delay {
        logger.info("stopping postgres")
        container.stop()
        logger.info("postgres stopped")
      }
    )

  def getTransactor[F[_]: Async: ContextShift](container: PostgreSQLContainer[_]) =
    Transactor.fromDriverManager[F](
      container.getDriverClassName,
      container.getJdbcUrl,
      container.getUsername,
      container.getPassword,
      Blocker.liftExecutionContext(ExecutionContexts.synchronous)
    )

}
