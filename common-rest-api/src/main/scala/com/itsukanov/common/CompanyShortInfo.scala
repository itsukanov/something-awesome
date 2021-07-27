package com.itsukanov.common

case class CompanyShortInfo(name: String, ticker: String)

object CompanyShortInfo {

  val allCompanies = List(
    CompanyShortInfo("Apple Inc.", "AAPL"),
    CompanyShortInfo("Microsoft Corporation", "MSFT"),
    CompanyShortInfo("Amazon.com Inc.", "AMZN"),
    CompanyShortInfo("Facebook Inc.", "FB"),
    CompanyShortInfo("Alphabet Inc.", "GOOG"),
    CompanyShortInfo("Tesla Inc", "TSLA"),
    CompanyShortInfo("NVIDIA Corporation", "NVDA"),
    CompanyShortInfo("JPMorgan Chase & Co.", "JPM"),
    CompanyShortInfo("Johnson & Johnson", "JNJ"),
    CompanyShortInfo("Visa Inc.", "V")
  )

}
