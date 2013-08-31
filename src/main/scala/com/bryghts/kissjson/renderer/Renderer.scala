package com.bryghts.kissjson
package renderer

import StringBuilder._

trait Renderer
{

	def renderNull    (                  out: StringBuilder): StringBuilder
	def renderInteger (in: JsonInteger,  out: StringBuilder): StringBuilder
	def renderReal    (in: JsonReal,     out: StringBuilder): StringBuilder
	def renderNumber  (in: JsonNumber,   out: StringBuilder): StringBuilder = in match {case i: MatchJsonInteger => renderInteger(i, out)
	                                                                                    case r: MatchJsonReal => renderReal(r, out)}
	def renderString  (in: JsonString,   out: StringBuilder): StringBuilder
	def renderBoolean (in: JsonBoolean,  out: StringBuilder): StringBuilder
	def renderArray   (in: JsonArray,    out: StringBuilder): StringBuilder
	def renderObject  (in: JsonObject,   out: StringBuilder): StringBuilder

	final def render  (in: JsonValue, out: StringBuilder): StringBuilder = in match
	{
		case i: MatchJsonInteger => renderInteger (i, out)
		case i: MatchJsonReal    => renderReal    (i, out)
		case i: MatchJsonString  => renderString  (i, out)
		case i: MatchJsonBoolean => renderBoolean (i, out)
		case i: MatchJsonArray   => renderArray   (i, out)
		case i: MatchJsonObject  => renderObject  (i, out)
		case i: MatchJsonNull    => renderNull    (   out)
	}

////////////////////////////////////////////////////////////////////////////////

	final def renderNull    (                 ): String = renderNull    (    newBuilder).toString
	final def renderInteger (in: JsonInteger  ): String = renderInteger (in, newBuilder).toString
	final def renderReal    (in: JsonReal     ): String = renderReal    (in, newBuilder).toString
	final def renderNumber  (in: JsonNumber   ): String = renderNumber  (in, newBuilder).toString
	final def renderString  (in: JsonString   ): String = renderString  (in, newBuilder).toString
	final def renderBoolean (in: JsonBoolean  ): String = renderBoolean (in, newBuilder).toString
	final def renderArray   (in: JsonArray    ): String = renderArray   (in, newBuilder).toString
	final def renderObject  (in: JsonObject   ): String = renderObject  (in, newBuilder).toString
	final def render        (in: JsonValue ): String = render        (in, newBuilder).toString

}

class CompactObjectRenderer extends Renderer
{
	import CompactObjectRenderer._

	def renderNull    (                 out: StringBuilder): StringBuilder = out ++= "null"
	def renderInteger (in: JsonInteger, out: StringBuilder): StringBuilder = out ++= in.asOption.map{_.toString}      .getOrElse("null")
	def renderReal    (in: JsonReal,    out: StringBuilder): StringBuilder = out ++= in.asOption.map{_.toString}      .getOrElse("null")
	def renderString  (in: JsonString,  out: StringBuilder): StringBuilder = out ++= in.asOption.map{_.asStringValue} .getOrElse("null")
	def renderBoolean (in: JsonBoolean, out: StringBuilder): StringBuilder = out ++= in.asOption.map{_.toString}      .getOrElse("null")

	def renderArray   (in: JsonArray,   out: StringBuilder): StringBuilder = {

		out ++= "["
		in.foldLeft(true){(first, i) => out ++= (if(first) "" else ", "); render(i, out); false}
		out ++= "]"

	}

	def renderObject  (in: JsonObject,  out: StringBuilder): StringBuilder = {

		out ++= "{"
		in.asMap.foldLeft(true){case (first, (k, i)) => out ++= (if(first) "" else ", "); out ++= k.asStringValue ++= ": "; render(i, out); false}
		out ++= "}"

	}

}

object CompactObjectRenderer
{
	private implicit class StringOps(val s: String) extends AnyVal
	{
		def asStringValue = "\"" + s.replaceAll("\"", "\\\"") + "\""
	}
}
