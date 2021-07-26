package com.itsukanov.entrypoint

import cats.MonadError
import io.janstenpickle.trace4cats.inject.Trace
import retry._

trait Retries {

  def withRetry[T, M[_]: Sleep: Trace](spanName: String)(action: => M[T])(
       implicit ME: MonadError[M, Throwable]): M[T] = Trace[M].span(s"$spanName.withRetry") {
    retryingOnAllErrors(
      policy = RetryPolicies.limitRetries(2),
      onError = (_: Throwable, _: RetryDetails) => ME.pure(())
    )(action)
  }

}
