package com.itsukanov.common

case class CompanyShortInfo(name: String, ticker: String)

case class CompanyFullInfo(name: String, ticker: String, prices: Seq[Double])

object Companies {

  val allCompanies = List(
    CompanyFullInfo("Apple Inc.", "AAPL", Seq(148.99, 148.56, 146.80)),
    CompanyFullInfo("Microsoft Corporation", "MSFT", Seq(289.05, 289.67, 286.14)),
    CompanyFullInfo("Amazon.com Inc.", "AMZN", Seq(3699.82, 3656.64, 3638.03)),
    CompanyFullInfo("Facebook Inc.", "FB", Seq(372.46, 369.79, 351.19)),
    CompanyFullInfo("Alphabet Inc.", "GOOG", Seq(2792.89, 2756.32, 2666.57)),
    CompanyFullInfo("Tesla Inc", "TSLA", Seq(657.62, 643.38, 649.26)),
    CompanyFullInfo("NVIDIA Corporation", "NVDA", Seq(192.94, 195.58, 195.94)),
    CompanyFullInfo("JPMorgan Chase & Co.", "JPM", Seq(151.65, 150.64, 150.93)),
    CompanyFullInfo("Johnson & Johnson", "JNJ", Seq(171.87, 171.79, 169.98)),
    CompanyFullInfo("Visa Inc.", "V", Seq(250.25, 249.02, 244.14))
  )

}
