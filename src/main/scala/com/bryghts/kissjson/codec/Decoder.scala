package com.bryghts.kissjson
package codec

import scala.reflect.runtime.universe._

trait Decoder[T]
{

	def decode(v: JsonValue, t: Type): Option[T]

	final def decode(v: JsonValue)(implicit t: TypeTag[T]): Option[T] =
		decode(v, t.tpe)

}
