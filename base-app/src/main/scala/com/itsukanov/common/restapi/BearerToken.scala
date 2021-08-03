package com.itsukanov.common.restapi

import sttp.tapir.Codec.PlainCodec
import sttp.tapir.{Codec, DecodeResult}

case class BearerToken(token: String) extends AnyVal

object BearerToken {

  implicit val codecBearerToken: PlainCodec[BearerToken] = Codec
    .string
    .mapDecode(x => DecodeResult.Value(BearerToken(x)))(_.token)

}
