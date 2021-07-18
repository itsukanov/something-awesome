package com.itsukanov.company.info.db

import cats.effect.Bracket
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.janstenpickle.trace4cats.inject.Trace

class CompanyShortInfoRepo[F[_]: Trace](xa: Transactor[F])(implicit bracket: Bracket[F, Throwable]) {

  def getCompanies(from: Int, limit: Int): F[List[CompanyShortInfo]] = {
    Trace[F].span("db: getCompanies") {
      sql"select * from company_short_info"
        .query[CompanyShortInfo]
        .to[List]
        .transact(xa)
    }
  }

}
