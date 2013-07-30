package com.bryghts


import kissjson._
import scala.util.parsing.json.JSON

package kissjson.parser
{

object JsonParser
{

	def apply(in: String): Option[JsonValue] = {

		val source = in.trim()

		if(in == "true")
			Some(JsonBoolean(true))
		else if(in == "false")
			Some(JsonBoolean(false))
		else if(in(0) == '[' || in(0) == '{')
			parseObjectOrArray(source)
		else
			parseObjectOrArray(s"[$source]").map{_.asArray.getOrElse(JsonArray())(0)}
	}

	private def parseObjectOrArray(source: String): Option[JsonValue] = {

		JSON.globalNumberParser = in => try{JsonInteger(in.toLong)}catch{case t: NumberFormatException => JsonDouble(in.toDouble)}

		JSON.parseFull(source) map convert

	}

	private def convert(in: Any): JsonValue = in match {
			case a: List    [Any]  => JsonArray   (a map convert)
			case o: Map     [_, _] => JsonObject  (o map {case (k, v) => (k.asInstanceOf[String], convert(v))})
			case b: Boolean        => JsonBoolean (b)
			case s: String         => JsonString  (s)
			case n: JsonNumber     => n
			case _ => JsonNull
	}
}

}
