package com.bryghts.kissjson

import scala.util.Try

object KissJson extends App
{

	val parse:  String => Try[Any]    = parser.Parser.apply _
	val render: Any    => Try[String] = renderer.Renderer.apply _

}
