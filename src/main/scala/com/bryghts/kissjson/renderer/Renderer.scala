package com.bryghts.kissjson
package renderer

import StringBuilder._

trait Renderer
{

	def renderNull    (                  out: StringBuilder): StringBuilder
	def renderNumber  (in: JsonNumber,   out: StringBuilder): StringBuilder
	def renderString  (in: JsonString,   out: StringBuilder): StringBuilder
	def renderBoolean (in: JsonBoolean,  out: StringBuilder): StringBuilder
	def renderArray   (in: JsonArray[_], out: StringBuilder): StringBuilder
	def renderObject  (in: JsonObject,   out: StringBuilder): StringBuilder

	final def render  (in: JsonValue, out: StringBuilder): StringBuilder = in match
	{
		case i: JsonNull    => renderNull    (   out)
		case i: JsonNumber  => renderNumber  (i, out)
		case i: JsonString  => renderString  (i, out)
		case i: JsonBoolean => renderBoolean (i, out)
		case i: JsonArray[_]=> renderArray   (i, out)
		case i: JsonObject  => renderObject  (i, out)
	}

////////////////////////////////////////////////////////////////////////////////

	final def renderNull    (                 ): String = renderNull    (    newBuilder).toString
	final def renderNumber  (in: JsonNumber   ): String = renderNumber  (in, newBuilder).toString
	final def renderString  (in: JsonString   ): String = renderString  (in, newBuilder).toString
	final def renderBoolean (in: JsonBoolean  ): String = renderBoolean (in, newBuilder).toString
	final def renderArray   (in: JsonArray[_] ): String = renderArray   (in, newBuilder).toString
	final def renderObject  (in: JsonObject   ): String = renderObject  (in, newBuilder).toString
	final def render        (in: JsonValue ): String = render        (in, newBuilder).toString

}

class CompactObjectRenderer extends Renderer
{
	import CompactObjectRenderer._

	def renderNull    (                 out: StringBuilder): StringBuilder = out ++= "null"
	def renderNumber  (in: JsonNumber,  out: StringBuilder): StringBuilder = out ++= in.asOption.map{_.toString}      .getOrElse("null")
	def renderString  (in: JsonString,  out: StringBuilder): StringBuilder = out ++= in.asOption.map{_.asStringValue} .getOrElse("null")
	def renderBoolean (in: JsonBoolean, out: StringBuilder): StringBuilder = out ++= in.asOption.map{_.toString}      .getOrElse("null")

	def renderArray   (in: JsonArray[_],   out: StringBuilder): StringBuilder = {

		out ++= "["
		in.foldLeft(true){(first, i) => out ++= (if(first) "" else ", "); render(i.asInstanceOf[JsonValue], out); false}
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
