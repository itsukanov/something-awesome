package com.itsukanov.common.restapi

import sttp.tapir.Validator._
import sttp.tapir._

case class Paging(from: Int, limit: Int)

trait PagingParams {
  private val defaultFrom = 0
  private val defaultLimit = 100

  private val pagingFrom = query[Option[Int]]("from")
    .description("Indicates where we should start returning data from. Must be >= 0")
    .example(Some(0))
    .validateOption(min(0))

  private val pagingLimit = query[Option[Int]]("limit")
    .description("An optional number of rows to be returned. Must be in [1, 100]")
    .example(Some(100))
    .validateOption(min(1).and(max(100)))

  val pagingIn: EndpointInput[Paging] =
    pagingFrom.and(pagingLimit).map(input => Paging(
      from = input._1.getOrElse(defaultFrom),
      limit = input._2.getOrElse(defaultLimit)
    )) {
      case Paging(from, to) => (Some(from), Some(to))
    }

}
