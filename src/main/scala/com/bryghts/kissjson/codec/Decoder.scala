package com.bryghts.kissjson
package codec

import scala.reflect.runtime.universe._
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import scala.reflect.ClassTag

trait Decoder[T]
{

	def decode(v: JsonValue, t: Type)(implicit env: DecoderEnvironment): Option[Try[T]]

	final def decode(v: JsonValue)(implicit t: TypeTag[T], env: DecoderEnvironment): Option[Try[T]] =
		decode(v, t.tpe)

}


case class SimpleDecoder[Source <: JsonValue : TypeTag, Target: TypeTag](validateJsonValue: JsonValue => Boolean, toSource: JsonValue => Source, sourceToTarget: Source => Target) extends Decoder[Target]
{

	def decode(v: JsonValue, t: Type)(implicit env: DecoderEnvironment): Option[Try[Target]] =
		if(t <:< typeOf[Target])
			if(validateJsonValue(v))
				Some(Success(sourceToTarget(toSource(v))))
			else
				Some(Failure(new Exception("Invalid type of value")))
		else
			None

}


object CaseClassDecoder extends Decoder[Product]
{

	def decode(v: JsonValue, t: Type)(implicit env: DecoderEnvironment): Option[Try[Product]] =
		v match {
			case jo: MatchJsonObject => doDecode(jo, t)
			case _ => Some(Failure(new Exception("Invalid type of value")))
		}

	private def doDecode(v: JsonObject, t: Type)(implicit env: DecoderEnvironment): Option[Try[Product]] =
	{

		???
//		val m = runtimeMirror(v.getClass.getClassLoader)
//
//		val ctor = t.declaration(nme.CONSTRUCTOR).asMethod
//
//		val im = m.reflect(v)(ClassTag(m.runtimeClass(t)))
//
//		val params = ctor.paramss.flatten.map{p =>
//			val n = p.name.decoded
//
//			val m:FieldMirror = im.reflectField(t.declaration(newTermName(n)).asTerm)
//			val rt = m.symbol.asTerm.getter.asMethod.returnType
//			val v = m.get
//
//			(n, rt, v)
//		}
//
//		def encodeField(h: (String, Type, Any)): Option[Try[(String, JsonValue)]] = {
//			val (n, t, v) = h
//
//			doEncode(v, t, env) map {_.map{(n -> _)}}
//		}
//
//		def encodeFail[T](h: (String, Type, Any)): Failure[T] = {
//			val (n, t, v) = h
//
//			fail(s"The field '$n' of type '$t' and value '$v' can not be converted to Json")
//		}
//
////		@tailrec
//		def encodeFields(params: List[(String, Type, Any)]): Try[List[(String, JsonValue)]] = {
//
//			if(params.isEmpty)
//				Success(Nil)
//			else {
//				encodeField(params.head) match {
//					case Some(Success(h)) => encodeFields(params.tail).map(h :: _)
//					case Some(Failure(t)) => Failure(t)
//					case None             => encodeFail(params.head)
//				}
//			}
//		}
//
//
//		encodeFields(params).map{fields => JsonObject(fields.toMap)}
	}


//	override private[codec] def canEncode(t: Type): Boolean =
//		t.typeSymbol.asClass.isCaseClass
}
