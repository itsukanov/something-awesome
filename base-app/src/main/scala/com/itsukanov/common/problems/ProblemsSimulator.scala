package com.itsukanov.common.problems

import cats.MonadError
import cats.effect.Timer
import cats.implicits._

import scala.concurrent.duration.FiniteDuration
import scala.util.Random

trait ProblemsSimulator {

  def simulateProblems[T, F[_]](ft: F[T])(
       implicit ME: MonadError[F, Throwable],
       T: Timer[F]): F[T]

}

object FailedWithError extends ProblemsSimulator {

  override def simulateProblems[T, F[_]](ft: F[T])(
       implicit ME: MonadError[F, Throwable],
       T: Timer[F]) =
    ME.raiseError(new RuntimeException("Something went wrong"))

}

object HappyPath extends ProblemsSimulator {

  override def simulateProblems[T, F[_]](ft: F[T])(
       implicit ME: MonadError[F, Throwable],
       T: Timer[F]) = ft

}

class TimedOut(to: FiniteDuration) extends ProblemsSimulator {

  override def simulateProblems[T, F[_]](ft: F[T])(
       implicit ME: MonadError[F, Throwable],
       T: Timer[F]) =
    T.sleep(to) *> ft

}

class CombinedSimulator(weight2scenario: Seq[(Int, ProblemsSimulator)]) extends ProblemsSimulator {
  require(weight2scenario.map(_._1).sum == 100, "sum of all weights must be 100")

  private val scenarios = weight2scenario.flatMap { case (weight, scenario) =>
    Seq.fill(weight)(scenario)
  }

  override def simulateProblems[T, F[_]](ft: F[T])(
       implicit ME: MonadError[F, Throwable],
       T: Timer[F]) =
    scenarios(Random.nextInt(99)).simulateProblems(ft)

}
