package com.bryghts.kissjson

import scala.reflect.runtime.universe.Type
import scala.reflect.runtime.universe.TypeTag
import scala.util.Try
import scala.annotation.tailrec
import scala.util.Failure
import com.bryghts.kissnumber.IntegerNumber
import com.bryghts.kissnumber.RealNumber
import com.bryghts.kissnumber.Number

package object codec
{

	implicit val coderEnvironment = 
			SimpleTypeCoder[String]        (v => JsonString (v))  ::
			SimpleTypeCoder[Byte]          (v => JsonNumber (v))  ::
			SimpleTypeCoder[Short]         (v => JsonNumber (v))  ::
			SimpleTypeCoder[Int]           (v => JsonNumber (v))  ::
			SimpleTypeCoder[Long]          (v => JsonNumber (v))  ::
			SimpleTypeCoder[Float]         (v => JsonNumber (v))  ::
			SimpleTypeCoder[Double]        (v => JsonNumber (v))  ::
			SimpleTypeCoder[Boolean]       (v => JsonBoolean(v))  ::
			SimpleTypeCoder[IntegerNumber] (v => JsonNumber (v))  ::
			SimpleTypeCoder[RealNumber]    (v => JsonNumber (v))  ::
			SimpleTypeCoder[Number]        (v => JsonNumber (v))  ::
			OptionCodec                                           ::
			ArrayCodec                                            ::
			TraversableCodec                                      ::
			CaseClassCodec                                        ::
			Nil

	type CoderEnvironment = List[Coder]
	type DeecoderEnvironment = List[Decoder[_]]

	private[codec] def doEncode(v: Any, t: Type, env: CoderEnvironment): Option[Try[JsonValue]] =
		findEncoder(t, env).flatMap{c => c(v, t, env)}

	@tailrec
	private[codec] def findEncoder(t: Type, encoders: CoderEnvironment): Option[Coder] =
		if(encoders.isEmpty) None
		else {
			if(encoders.head.canEncode(t))
				Some(encoders.head)
			else
				findEncoder(t, encoders.tail)
		}

	private[codec] def fail(msg: String) = Failure(new Exception(msg))

	def caseClassCodec[T <: Product](in: T)(implicit tt: TypeTag[T], env: CoderEnvironment): Try[JsonValue] =
		CaseClassCodec(in, tt.tpe, env).getOrElse(fail("There is no Codec capable of converting this object"))

}