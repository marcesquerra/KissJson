package com.bryghts.kissjson.renderer

import scala.util.Try

object Renderer
{

	def apply(in: Any): Try[String] = Try(renderAny(in))

	private def render(in: Byte):    String = in.toString
	private def render(in: Char):    String = in.toShort.toString
	private def render(in: Short):   String = in.toString
	private def render(in: Int):     String = in.toString
	private def render(in: Long):    String = in.toString
	private def render(in: Float):   String = in.toString
	private def render(in: Double):  String = in.toString

	private def render(in: Boolean): String = in.toString

	private def render(in: String):  String = s""" "${in.replace("\\", "\\\\").replace("\"", "\\\"")}" """.trim

	private def renderAny(in: Any): String = in match {
		case in: Byte            => render(in)
		case in: Char            => render(in)
		case in: Short           => render(in)
		case in: Int             => render(in)
		case in: Long            => render(in)
		case in: Float           => render(in)
		case in: Double          => render(in)
		case in: Boolean         => render(in)
		case in: String          => render    (in)
		case in: Map[Any, Any]   => renderMap (in)
		case in: Traversable[_]  => renderT   (in)
		case in: Array[Any]      => renderA   (in)
		case in =>
			throw new Exception(s"'${in.getClass}' Can not be rendered")
	}

	private def renderT   (in: Traversable[Any]): String   = in.map{renderAny(_)}.mkString("[", ", ", "]")
	private def renderA   (in: Array[Any]):       String   = in.map{renderAny(_)}.mkString("[", ", ", "]")
	private def renderMap (in: Map[Any, Any]):    String   = in.map{case (k: String, v) => s"${render(k)}: ${renderAny(v)}" case _ => throw new Exception("Can not be rendered")}.mkString("{", ", ", "}")

}
