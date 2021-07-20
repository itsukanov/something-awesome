package com.itsukanov.company.info.db

import cats.effect.Bracket
import doobie.Transactor
import doobie.implicits._

object CompanyShortInfoDDL {

  def initDB[F[_]](xa: Transactor[F])(implicit bracket: Bracket[F, Throwable]): F[Unit] = (for {
    _ <- create
    _ <- insert
  } yield ())
    .transact(xa)

  private val create =
    sql"""
    CREATE TABLE company_short_info (
      name VARCHAR NOT NULL,
      ticker character(4) unique NOT NULL
    )
  """.update.run

  private val insert =
    sql"""
      insert into company_short_info (name, ticker) values
      ('Microsoft Corporation2', 'MSFT')
       """.update.run

}
