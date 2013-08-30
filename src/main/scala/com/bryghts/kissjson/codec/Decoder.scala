package com.bryghts.kissjson
package codec

import scala.reflect.runtime.universe._

trait Decoder[T]
{

	def decode(v: JsonValue[_], t: Type): Option[T]

	final def decode(v: JsonValue[_])(implicit t: TypeTag[T]): Option[T] =
		decode(v, t.tpe)

}
