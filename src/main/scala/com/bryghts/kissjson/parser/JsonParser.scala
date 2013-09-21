package com.bryghts


import kissjson._
import scala.util.parsing.json.JSON
import scala.util.Try
import scala.util.Success
import scala.util.Failure

package kissjson.parser
{

object JsonParser
{

	def apply(in: String): Try[JsonValue] = {

		val source = in.trim()

		if(in == "true")
			Success(JsonBoolean(true))
		else if(in == "false")
			Success(JsonBoolean(false))
		else if (source.charAt(0) == '[' || source.charAt(0) == '{')
			parseObjectOrArray(source).map{Success(_)}.getOrElse(Failure(new Exception("Could not be parsed")))
		else
			parseObjectOrArray(s"[$source]").map{_(0)}.map{Success(_)}.getOrElse(Failure(new Exception("Could not be parsed")))
	}

	private def parseObjectOrArray(source: String): Option[JsonValue] = {

		JSON.globalNumberParser = in => try{JsonNumber(in.toLong)}catch{case t: NumberFormatException => JsonNumber(in.toDouble)}

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
