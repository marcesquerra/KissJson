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

	private[codec] def subType(t: Type) =
			t match
			{
				case pt: TypeRef => pt.args(0)
				case _ => throw new Exception("")
			}

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

case class OptionDecoder[T](implicit internalDecoder: Decoder[T], it: TypeTag[T]) extends Decoder[Option[T]]
{
	def decode(v: JsonValue, t: Type)(implicit env: DecoderEnvironment): Option[Try[Option[T]]] =
		decodeInternal(v)

	def decodeInternal(v: JsonValue)(implicit env: DecoderEnvironment): Option[Try[Option[T]]] =
		if(v == JsonNull) Some(Success(None))
		else
			internalDecoder.decode(v, typeOf[T])(env) match {
				case None              => None
				case Some(Failure(t))  => Some(Failure(t))
				case Some(Success(r))  => Some(Success(Some(r)))
			}

}

object GenericOptionDecoder extends Decoder[Option[_]]
{


	def decode(v: JsonValue, t: Type)(implicit env: DecoderEnvironment): Option[Try[Option[Any]]] =
		if(t <:< typeOf[Option[_]])
		{
			if(v == JsonNull) Some(Success(None))
			else
			{
				val d = tryToDecode(v, subType(t), env, env)
		
				println(d)
		
				d match {
					case None => None
					case Some(Failure(t)) => Some(Failure(t))
					case Some(Success(r)) => Some(Success(Some(r)))
				}
			}
		}
		else None
}

case class ArrayDecoder[T](implicit internalDecoder: Decoder[T], it: TypeTag[T], ct: ClassTag[T]) extends Decoder[Array[T]]
{
	def decode(v: JsonValue, t: Type)(implicit env: DecoderEnvironment): Option[Try[Array[T]]] =
		decodeInternal(v)

	def decodeInternal(v: JsonValue)(implicit env: DecoderEnvironment): Option[Try[Array[T]]] =
		Some(v.toList.map{internalDecoder.decode(_, typeOf[T])(env)}.foldLeft(Success(Array[T]()): Try[Array[T]]){(t, b) =>
			t match {
				case Failure(t) => Failure(t)
				case Success(a) =>
					b match {
						case None => Failure(new Exception("No Decoder found"))
						case Some(Failure(t)) => Failure(t)
						case Some(Success(r)) => Success(a ++ Array(r))
					}
			}
		})

}

object GenericArrayDecoder extends Decoder[Array[_]]
{

	val ad = ArrayDecoder[Int]

	def decode(v: JsonValue, t: Type)(implicit env: DecoderEnvironment): Option[Try[Array[_]]] =
		if(t <:< typeOf[Array[_]])
			Some(v.toList.map{tryToDecode(_, subType(t), env, env)}.foldLeft(Success(Array[Any]()): Try[Array[_]]){(t, b) =>
				t match {
					case Failure(t) => Failure(t)
					case Success(a) =>
						b match {
							case None => Failure(new Exception("No Decoder found"))
							case Some(Failure(t)) => Failure(t)
							case Some(Success(r)) => Success(a ++ Array(r))
						}
				}
			})
		else
			None

//	def decode(v: JsonValue, t: Type)(implicit env: DecoderEnvironment): Option[Try[Option[Any]]] =
//		if(v == JsonNull) Some(Success(None))
//		else
//			tryToDecode(v, subType(t), env, env) match {
//				case None => None
//				case Some(Failure(t)) => Some(Failure(t))
//				case Some(Success(r)) => Some(Success(Some(r)))
//			}
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

		if(!t.typeSymbol.asClass.isCaseClass)    return None

		val ctor = t.declaration(nme.CONSTRUCTOR).asMethod
		val cm = m.reflectClass(t.typeSymbol.asClass)
		val ctorm = cm.reflectConstructor(ctor)

		val params = ctor.paramss.map{_.map{p =>

			val rt = p.typeSignatureIn(t)


			val f = v.asMap.getOrElse(p.name.decoded, JsonNull)

			tryToDecode(f, rt, env, env)
		}}

		if(params.flatten.exists(f => f == None || f.get.isFailure))
			return Some(Failure(new Exception("")))

		Some(Success(ctorm(params.flatten.map{_.get.get} :_*).asInstanceOf[Product]))

	}

}
