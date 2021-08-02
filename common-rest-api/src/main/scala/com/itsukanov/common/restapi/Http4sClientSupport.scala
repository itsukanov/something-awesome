package com.itsukanov.common.restapi

import cats.MonadThrow
import cats.implicits._
import org.http4s.Status
import org.http4s.client.UnexpectedStatus

trait Http4sClientSupport {

  implicit class Http4sClientSupportOps[T, G[_]: MonadThrow](gt: G[T]) {

    def handle404: G[Option[T]] = gt.map(Option(_)).recover { case UnexpectedStatus(Status(404)) =>
      Option.empty[T]
    }

  }

}
