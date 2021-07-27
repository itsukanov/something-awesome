package com.itsukanov.company.info.db

import cats.effect.Bracket
import com.itsukanov.common.Companies
import doobie.implicits._
import doobie.{Transactor, Update}

object CompanyShortInfoDDL {

  def initDB[F[_]](xa: Transactor[F])(
       implicit bracket: Bracket[F, Throwable]): F[Unit] = (for {
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

  private val insert = {
    val sql = "insert into company_short_info (name, ticker) values (?, ?)"
    Update[(String, String)](sql).updateMany(Companies.allCompanies.map(x => (x.name, x.ticker)))
  }

}
