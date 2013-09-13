package com.bryghts.kissjson

import scala.reflect.runtime.universe.Type
import scala.reflect.runtime.universe.TypeTag
import scala.util.Try
import scala.annotation.tailrec
import scala.util.Failure
import com.bryghts.kissnumber.IntegerNumber
import com.bryghts.kissnumber.RealNumber
import com.bryghts.kissnumber.Number
import scala.reflect.ClassTag

package object codec
{

	implicit val coderEnvironment: CoderEnvironment =
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

	implicit val stringDecoder         = SimpleDecoder[JsonString,  String]        (_.isInstanceOf[MatchJsonString],  _.asInstanceOf[JsonString],  _.getOrElse(throw new Exception("")))
	implicit val byteDecoder           = SimpleDecoder[JsonNumber,  Byte]          (_.isInstanceOf[MatchJsonNumber],  _.asInstanceOf[JsonNumber],  _.getOrElse(throw new Exception("")).toByte)
	implicit val shortDecoder          = SimpleDecoder[JsonNumber,  Short]         (_.isInstanceOf[MatchJsonNumber],  _.asInstanceOf[JsonNumber],  _.getOrElse(throw new Exception("")).toShort)
	implicit val intDecoder            = SimpleDecoder[JsonNumber,  Int]           (_.isInstanceOf[MatchJsonNumber],  _.asInstanceOf[JsonNumber],  _.getOrElse(throw new Exception("")).toInt)
	implicit val longDecoder           = SimpleDecoder[JsonNumber,  Long]          (_.isInstanceOf[MatchJsonNumber],  _.asInstanceOf[JsonNumber],  _.getOrElse(throw new Exception("")).toLong)
	implicit val floatDecoder          = SimpleDecoder[JsonNumber,  Float]         (_.isInstanceOf[MatchJsonNumber],  _.asInstanceOf[JsonNumber],  _.getOrElse(throw new Exception("")).toFloat)
	implicit val doubleDecoder         = SimpleDecoder[JsonNumber,  Double]        (_.isInstanceOf[MatchJsonNumber],  _.asInstanceOf[JsonNumber],  _.getOrElse(throw new Exception("")).toDouble)
	implicit val integerNumberDecoder  = SimpleDecoder[JsonNumber,  IntegerNumber] (_.isInstanceOf[MatchJsonNumber],  _.asInstanceOf[JsonNumber],  _.getOrElse(throw new Exception("")).toInteger)
	implicit val realNumberDecoder     = SimpleDecoder[JsonNumber,  RealNumber]    (_.isInstanceOf[MatchJsonNumber],  _.asInstanceOf[JsonNumber],  _.getOrElse(throw new Exception("")).toReal)
	implicit val numberDecoder         = SimpleDecoder[JsonNumber,  Number]        (_.isInstanceOf[MatchJsonNumber],  _.asInstanceOf[JsonNumber],  _.getOrElse(throw new Exception("")))
	implicit val booleanDecoder        = SimpleDecoder[JsonBoolean, Boolean]       (_.isInstanceOf[MatchJsonBoolean], _.asInstanceOf[JsonBoolean], _.getOrElse(throw new Exception("")))

//	implicit def collectionDecoder[T : Decoder: TypeTag, C <: Traversable[T] : TypeTag]: Decoder[C] = TraversableDecoder[T, C]
	implicit def caseClassDecoder[T <: Product]:Decoder[T] = CaseClassDecoder.asInstanceOf[Decoder[T]]
	implicit def arrayDecoder[T : Decoder: TypeTag : ClassTag]: Decoder[Array[T]] = ArrayDecoder[T]
	implicit def optionDecoder[T : Decoder: TypeTag]: Decoder[Option[T]] = OptionDecoder[T]

	implicit val decoderEnvironment: DecoderEnvironment =
			stringDecoder             ::
			byteDecoder               ::
			shortDecoder              ::
			intDecoder                ::
			longDecoder               ::
			floatDecoder              ::
			doubleDecoder             ::
			booleanDecoder            ::
			integerNumberDecoder      ::
			realNumberDecoder         ::
			numberDecoder             ::
			GenericArrayDecoder       ::
			GenericTraversableDecoder ::
			GenericOptionDecoder      ::
			CaseClassDecoder          ::
			Nil

	type CoderEnvironment = List[Coder]
	type DecoderEnvironment = List[Decoder[_]]

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