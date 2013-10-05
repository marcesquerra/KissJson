package com.bryghts.kissjson
package codec

import scala.util.Try
import scala.reflect.runtime.universe.Type
import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.universe.typeOf
import scala.reflect.runtime.universe.TypeRef
import scala.reflect.runtime.universe.FieldMirror
import scala.reflect.runtime.{universe => ru}
import scala.util.Success
import scala.reflect.ClassTag
import scala.util.Failure


trait Coder
{

	private[codec] final def apply(in: Any, t: Type, env: CoderEnvironment): Option[Try[JsonValue]] =
		if(canEncode(t))
			Some(encode(in, t, env))
		else
			None

	protected def encode(in: Any, t: Type, env: CoderEnvironment): Try[JsonValue]
	private[codec] def canEncode(t: Type): Boolean
}


trait PublicCoder[T] extends Coder
{
	final def apply(in: T)(implicit tt: TypeTag[T], env: CoderEnvironment): Option[Try[JsonValue]] = apply(in, tt.tpe, env)
}

case class SimpleTypeCoder[T : TypeTag](f: T => JsonValue) extends PublicCoder[T]
{
	override protected def encode(v: Any, t: Type, env: CoderEnvironment): Try[JsonValue] =
			Success(f(v.asInstanceOf[T]))

	override private[codec] def canEncode(t: Type): Boolean = t =:= typeOf[T]
}

object CaseClassCodec extends Coder
{

	override protected def encode(in: Any, t: Type, env: CoderEnvironment): Try[JsonValue] =
	{

		val m = ru.runtimeMirror(in.getClass.getClassLoader)

		val ctor = t.declaration(ru.nme.CONSTRUCTOR).asMethod

		val im = m.reflect(in)(ClassTag(m.runtimeClass(t)))
		val params = ctor.paramss.flatten.map{p =>
			val n = p.name.decoded

			val m:FieldMirror = im.reflectField(t.declaration(ru.newTermName(n)).asTerm)
			val rt = m.symbol.asTerm.getter.asMethod.returnType
			val v = m.get

			(n, rt, v)
		}

		def encodeField(h: (String, Type, Any)): Option[Try[(String, JsonValue)]] = {
			val (n, t, v) = h

			tryToEncode(v, t, env) map {_.map{(n -> _)}}
		}

		def encodeFail[T](h: (String, Type, Any)): Failure[T] = {
			val (n, t, v) = h

			fail(s"The field '$n' of type '$t' and value '$v' can not be converted to Json")
		}

//		@tailrec
		def encodeFields(params: List[(String, Type, Any)]): Try[List[(String, JsonValue)]] = {

			if(params.isEmpty)
				Success(Nil)
			else {
				encodeField(params.head) match {
					case Some(Success(h)) => encodeFields(params.tail).map(h :: _)
					case Some(Failure(t)) => Failure(t)
					case _                => encodeFail(params.head)
				}
			}
		}


		encodeFields(params).map{fields => JsonObject(fields.toMap)}
	}


	override private[codec] def canEncode(t: Type): Boolean =
		t.typeSymbol.asClass.isCaseClass
}

object ArrayCodec extends Coder
{
	protected def encode(in: Any, t: Type, env: CoderEnvironment): Try[JsonValue] =
	{
		val pt = t.asInstanceOf[TypeRef]
				in match {
					case v: Array[_] =>
						findEncoder(pt.args(0), env) match {
							case Some(c) =>
								val b: Try[Vector[JsonValue]] = Success(Vector())

								v.foldLeft(b) {
									case (Success(accum), v) =>

										def append(v: Vector[JsonValue], a: JsonValue): Vector[JsonValue] = v :+ a

										c(v, pt.args(0), env).map{_.map{append(accum, _)}}.getOrElse(fail(""))

									case (Failure(t), v) => Failure(t)
								}.map{JsonArray[JsonValue](_)(a => JsonNull)}

							case _ =>
								fail("_")
						}
					case _ =>
						fail(s"'$in' is not an Array")
				}
	}

	private[codec] def canEncode(t: Type): Boolean = t <:< ru.typeOf[Array[_]]
}

object TraversableCodec extends Coder
{
	protected def encode(in: Any, t: Type, env: CoderEnvironment): Try[JsonValue] =
	{
		val pt = t.asInstanceOf[TypeRef]
					in match {
						case v: Traversable[_] =>
							findEncoder(pt.args(0), env) match {
								case Some(c) =>
									val b: Try[Vector[JsonValue]] = Success(Vector())

									v.foldLeft(b) {
										case (Success(accum), v) =>

											def append(v: Vector[JsonValue], a: JsonValue): Vector[JsonValue] = v :+ a

											c(v, pt.args(0), env).map{_.map{append(accum, _)}}.getOrElse(fail(""))

										case (Failure(t), v) => Failure(t)
									}.map{JsonArray[JsonValue](_)(a => JsonNull)}

								case _ =>
									fail("_")
							}
						case _ =>
							fail(s"'$in' is not an Array")
					}
	}

	private[codec] def canEncode(t: Type): Boolean = t <:< ru.typeOf[Traversable[_]]
}

object OptionCodec extends Coder
{
	override protected def encode(in: Any, t: Type, env: CoderEnvironment): Try[JsonValue] =
	{
		val pt = t.asInstanceOf[TypeRef]
		in match {
			case Some(v) =>
				tryToEncode(v, pt.args(0), env) getOrElse {fail("The Encoder has returned no value after saying it would")}
			case _ =>
				Success(JsonNull)
		}
	}

	override private[codec] def canEncode(t: Type): Boolean = t <:< ru.typeOf[Option[_]]
}

