package com.bryghts.kissjson
package codec

import scala.reflect.runtime.universe._
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import scala.reflect.ClassTag

import scala.language.reflectiveCalls

trait Decoder[T]
{

	def decode(v: JsonValue, t: Type)(implicit env: DecoderEnvironment): Option[Try[T]]

	final def decode(v: JsonValue)(implicit t: TypeTag[T], env: DecoderEnvironment): Option[Try[T]] =
		decode(v, t.tpe)

}


case class SimpleDecoder[Source <: JsonValue : TypeTag, Target: TypeTag](validateJsonValue: JsonValue => Boolean, toSource: JsonValue => Source, sourceToTarget: Source => Target) extends Decoder[Target]
{

	def decode(v: JsonValue, t: Type)(implicit env: DecoderEnvironment): Option[Try[Target]] =
	{
		if(t <:< typeOf[Target])
			if(validateJsonValue(v))
				Some(Success(sourceToTarget(toSource(v))))
			else
				Some(Failure(new Exception("Invalid type of value")))
		else
			None
	}

}


object CaseClassDecoder extends Decoder[Product]
{

	private val m = runtimeMirror(getClass.getClassLoader)

	def decode(v: JsonValue, t: Type)(implicit env: DecoderEnvironment): Option[Try[Product]] =
		v match {
			case jo: MatchJsonObject => doDecode(jo, t)
			case _ => Some(Failure(new Exception("Invalid type of value")))
		}

	private def doDecode(v: JsonObject, t: Type)(implicit env: DecoderEnvironment): Option[Try[Product]] =
	{

		val ctor = t.declaration(nme.CONSTRUCTOR).asMethod
		val cm = m.reflectClass(t.typeSymbol.asClass)
		val ctorm = cm.reflectConstructor(ctor)

		val params = ctor.paramss.map{_.map{p =>
			val n = p.name.decoded
			val rt = p.typeSignatureIn(t)


			val f = v.asMap.getOrElse(n, JsonNull)

			(n, tryToDecode(f, rt, env, env))
		}}

		if(params.flatten.exists(f => f._2 == None || f._2.get.isFailure))
			return Some(Failure(new Exception("")))

		Some(Success(ctorm(params.flatten.map{_._2.get.get} :_*).asInstanceOf[Product]))

	}

}
