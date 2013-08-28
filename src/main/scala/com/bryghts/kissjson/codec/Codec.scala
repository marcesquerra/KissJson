package com.bryghts.kissjson

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.annotation.tailrec
import com.bryghts.kissnumber.IntegerNumber
import com.bryghts.kissnumber.RealNumber
import com.bryghts.kissnumber.Number
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import scala.util.Failure


package codec
{


trait Coder {
	private[codec] final def apply(in: Any, t: Type, env: CodecEnvironment): Option[Try[JsonValue[_]]] =
		if(canEncode(t))
			Some(encode(in, t, env))
		else
			None

	protected def encode(in: Any, t: Type, env: CodecEnvironment): Try[JsonValue[_]]
	private[codec] def canEncode(t: Type): Boolean
}

trait PublicCoder[T] extends Coder {
	final def apply(in: T)(implicit tt: TypeTag[T], env: CodecEnvironment): Option[Try[JsonValue[_]]] = apply(in, tt.tpe, env)
}


}

package object codec
{

	import scala.reflect.runtime.{universe => ru}

	type CodecEnvironment = List[Coder]

	implicit val codecEnvironment = 
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

	case class SimpleTypeCoder[T : TypeTag](f: T => JsonValue[_]) extends PublicCoder[T]
	{
		override protected def encode(v: Any, t: Type, env: CodecEnvironment): Try[JsonValue[_]] =
				Success(f(v.asInstanceOf[T]))

		override private[codec] def canEncode(t: Type): Boolean = t =:= ru.typeOf[T]
	}

	private def doEncode(v: Any, t: Type, env: CodecEnvironment): Option[Try[JsonValue[_]]] =
		findEncoder(t, env).flatMap{c => c(v, t, env)}

	@tailrec
	private def findEncoder(t: Type, encoders: CodecEnvironment): Option[Coder] =
		if(encoders.isEmpty) None
		else {
			if(encoders.head.canEncode(t))
				Some(encoders.head)
			else
				findEncoder(t, encoders.tail)
		}

	def caseClassCodec[T <: Product](in: T)(implicit tt: TypeTag[T], env: CodecEnvironment): Try[JsonValue[_]] =
		CaseClassCodec(in, tt.tpe, env).getOrElse(fail("There is no Codec capable of converting this object"))

	object ArrayCodec extends Coder
	{
		protected def encode(in: Any, t: Type, env: CodecEnvironment): Try[JsonValue[_]] =
		{
				t match {
					case pt: TypeRef =>
						in match {
							case v: Array[_] =>
								findEncoder(pt.args(0), env) match {
									case Some(c) =>
										val b: Try[Vector[JsonValue[_]]] = Success(Vector())

										v.foldLeft(b) {
											case (Success(accum), v) =>

												def append(v: Vector[JsonValue[_]], a: JsonValue[_]): Vector[JsonValue[_]] = v :+ a

												c(v, pt.args(0), env).map{_.map{append(accum, _)}}.getOrElse(fail(""))

											case (Failure(t), v) => Failure(t)
										}.map{JsonArray(_)}

									case None =>
										fail("_")
								}
							case None =>
								fail(s"'$in' is not an Array")
						}
					case _ =>
						fail("")
				}
		}

		private[codec] def canEncode(t: Type): Boolean = t <:< ru.typeOf[Array[_]]
	}

	object TraversableCodec extends Coder
	{
		protected def encode(in: Any, t: Type, env: CodecEnvironment): Try[JsonValue[_]] =
		{
				t match {
					case pt: TypeRef =>
						in match {
							case v: Traversable[_] =>
								findEncoder(pt.args(0), env) match {
									case Some(c) =>
										val b: Try[Vector[JsonValue[_]]] = Success(Vector())

										v.foldLeft(b) {
											case (Success(accum), v) =>

												def append(v: Vector[JsonValue[_]], a: JsonValue[_]): Vector[JsonValue[_]] = v :+ a

												c(v, pt.args(0), env).map{_.map{append(accum, _)}}.getOrElse(fail(""))

											case (Failure(t), v) => Failure(t)
										}.map{JsonArray(_)}

									case None =>
										fail("_")
								}
							case None =>
								fail(s"'$in' is not an Array")
						}
					case _ =>
						fail("")
				}
		}

		private[codec] def canEncode(t: Type): Boolean = t <:< ru.typeOf[Traversable[_]]
	}

	object OptionCodec extends Coder
	{
		override protected def encode(in: Any, t: Type, env: CodecEnvironment): Try[JsonValue[_]] =
		{
			t match {
				case pt: TypeRef =>
					in match {
						case Some(v) =>
							doEncode(v, pt.args(0), env) getOrElse {fail("The Encoder has returned no value after saying it would")}
						case None =>
							Success(JsonNull)
					}
				case _ =>
					fail("Not enough type information to recover the Option content")
			}
		}

		override private[codec] def canEncode(t: Type): Boolean = t <:< ru.typeOf[Option[_]]
	}

	private def fail(msg: String) = Failure(new Exception(msg))

	object CaseClassCodec extends Coder
	{

		override protected def encode(in: Any, t: Type, env: CodecEnvironment): Try[JsonValue[_]] =
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

			def encodeField(h: (String, Type, Any)): Option[Try[(String, JsonValue[_])]] = {
				val (n, t, v) = h

				doEncode(v, t, env) map {_.map{(n -> _)}}
			}

			def encodeFail[T](h: (String, Type, Any)): Failure[T] = {
				val (n, t, v) = h

				fail(s"The field '$n' of type '$t' and value '$v' can not be converted to Json")
			}

//			@tailrec
			def encodeFields(params: List[(String, Type, Any)]): Try[List[(String, JsonValue[_])]] = {

				if(params.isEmpty)
					Success(Nil)
				else {
					encodeField(params.head) match {
						case Some(Success(h)) => encodeFields(params.tail).map(h :: _)
						case Some(Failure(t)) => Failure(t)
						case None             => encodeFail(params.head)
					}
				}
			}


			encodeFields(params).map{fields => JsonObject(fields.toMap)}
		}


		override private[codec] def canEncode(t: Type): Boolean =
			t.typeSymbol.asClass.isCaseClass
	}

}

