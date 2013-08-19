package com.bryghts.kissjson
package renderer

trait Renderer
{
	def renderNull    (                 out: StringBuilder)
	def renderInteger (in: JsonInteger, out: StringBuilder)
	def renderReal    (in: JsonReal,    out: StringBuilder)
//	def renderNumber  (in: JsonNumber,  out: StringBuilder) = in match {case JsonInteger(i) => renderInteger(i, out)}
}