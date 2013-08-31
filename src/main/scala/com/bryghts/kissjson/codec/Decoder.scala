package com.bryghts.kissjson
package codec

import scala.reflect.runtime.universe._

trait Decoder[T]
{

	def decode(v: JsonValue, t: Type)(implicit env: DeecoderEnvironment): Option[T]

	final def decode(v: JsonValue)(implicit t: TypeTag[T], env: DeecoderEnvironment): Option[T] =
		decode(v, t.tpe)

}

