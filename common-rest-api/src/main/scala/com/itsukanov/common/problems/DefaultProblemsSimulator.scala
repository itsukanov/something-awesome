package com.itsukanov.common.problems

import cats.MonadError
import cats.effect.Timer

import scala.concurrent.duration._

trait DefaultProblemsSimulator extends ProblemsSimulator {

  private val combinedSimulator = new CombinedSimulator(
    Seq(
      (40, HappyPath),
      (30, new TimedOut(5.seconds)),
      (30, FailedWithError)
    )
  )

  override def simulateProblems[T, F[_]](ft: F[T])(
       implicit ME: MonadError[F, Throwable],
       T: Timer[F]) =
    combinedSimulator.simulateProblems(ft)

}
