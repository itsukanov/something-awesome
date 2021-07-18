package com.itsukanov.company.info.db

import doobie.implicits._

object CompanyShortInfoDDL {

  val create =
    sql"""
    CREATE TABLE company_short_info (
      name VARCHAR NOT NULL,
      ticker character(4) unique NOT NULL
    )
  """.update.run

  val insert =
    sql"""
      insert into company_short_info (name, ticker) values
      ('Microsoft Corporation2', 'MSFT')
       """.update.run

}
