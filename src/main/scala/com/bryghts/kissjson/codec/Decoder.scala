package com.bryghts.kissjson
package codec

import scala.reflect.runtime.universe._
import scala.util.Try
import scala.util.Success
import scala.util.Failure

trait Decoder[T]
{

	def decode(v: JsonValue, t: Type)(implicit env: DeecoderEnvironment): Option[Try[T]]

	final def decode(v: JsonValue)(implicit t: TypeTag[T], env: DeecoderEnvironment): Option[Try[T]] =
		decode(v, t.tpe)

}


case class SimpleDecoder[Source <: JsonValue : TypeTag, Target: TypeTag](validateJsonValue: JsonValue => Boolean, toSource: JsonValue => Source, sourceToTarget: Source => Target) extends Decoder[Target]
{

	def decode(v: JsonValue, t: Type)(implicit env: DeecoderEnvironment): Option[Try[Target]] =
		if(t <:< typeOf[Target])
			if(validateJsonValue(v))
				Some(Success(sourceToTarget(toSource(v))))
			else
				Some(Failure(new Exception("Invalid type of value")))
		else
			None

}
