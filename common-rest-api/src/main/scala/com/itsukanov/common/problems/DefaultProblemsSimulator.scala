package com.itsukanov.common.problems

import cats.MonadError
import cats.effect.Timer

import scala.concurrent.duration._

trait DefaultProblemsSimulator extends ProblemsSimulator {

  private val combinedSimulator = new CombinedSimulator(Seq(
    (50, HappyPath),
    (30, new TimedOut(10.seconds)),
    (20, FailedWithError)
  ))

  override def simulateProblems[T, F[_]](ft: F[T])(implicit ME: MonadError[F, Throwable], T: Timer[F]) =
    combinedSimulator.simulateProblems(ft)
}
