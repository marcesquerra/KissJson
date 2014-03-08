package com.bryghts.kissjson

import scala.util.Try

object KissJson
{

	val parse:  String => Try[Any]    = parser.Parser.apply _
	val render: Any    => Try[String] = renderer.Renderer.apply _

	object Parsed {
		def unapply(in: String): Option[Any] = parser.Parser(in).toOption
	}

	object Rendered {
		def unapply(in: Any): Option[String] = renderer.Renderer(in).toOption
	}

}
