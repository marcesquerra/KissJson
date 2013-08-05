package com.bryghts


import kissjson._
import scala.util.parsing.json.JSON

package kissjson.parser
{

object JsonParser
{

	def apply(in: String): Option[JsonValue[_]] = {

		val source = in.trim()

		if(in == "true")
			Some(JsonBoolean(true))
		else if(in == "false")
			Some(JsonBoolean(false))
		else if (source.charAt(0) == '[' || source.charAt(0) == '{')
			parseObjectOrArray(source)
		else
			parseObjectOrArray(s"[$source]").map{_(0)}
	}

	private def parseObjectOrArray(source: String): Option[JsonValue[_]] = {

		JSON.globalNumberParser = in => try{JsonNumber(in.toLong)}catch{case t: NumberFormatException => JsonNumber(in.toDouble)}

		JSON.parseFull(source) map convert

	}

	private def convert(in: Any): JsonValue[_] = in match {
			case a: List    [Any]  => JsonArray   (a map convert)
			case o: Map     [_, _] => JsonObject  (o map {case (k, v) => (k.asInstanceOf[String], convert(v))})
			case b: Boolean        => JsonBoolean (b)
			case s: String         => JsonString  (s)
			case n: JsonNumber     => n
			case _ => JsonNull
	}
}

}
