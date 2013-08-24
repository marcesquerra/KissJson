package com.bryghts.kissjson

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.annotation.tailrec
import com.bryghts.kissnumber.IntegerNumber
import com.bryghts.kissnumber.RealNumber
import com.bryghts.kissnumber.Number


package codec
{


trait Coder {
	private[codec] def apply(in: Any, t: Type, env: CodecEnvironment): Option[JsonValue[_]]
}

trait PublicCoder[T] extends Coder {
	final def apply(in: T)(implicit tt: TypeTag[T], env: CodecEnvironment): Option[JsonValue[_]] = apply(in, tt.tpe, env)
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
			CaseClassCodec                                        ::
			Nil

	case class SimpleTypeCoder[T : TypeTag](f: T => JsonValue[_]) extends PublicCoder[T]
	{
		private[codec] def apply(v: Any, t: Type, env: CodecEnvironment): Option[JsonValue[_]] =
			if(t =:= ru.typeOf[T])
				Some(f(v.asInstanceOf[T]))
			else
				None
	}

	@tailrec
	private def doEncode(v: Any, t: Type, env: CodecEnvironment, encoders: CodecEnvironment): Option[JsonValue[_]] =
		if(encoders.isEmpty) None
		else {
			val h = encoders.head(v, t, env)

			if(h.isEmpty)
				doEncode(v, t, env, encoders.tail)
			else
				h
		}

	private def doEncode(v: Any, t: Type, env: CodecEnvironment): Option[JsonValue[_]] = doEncode(v, t, env, env)

	def caseClassCodec[T <: Product](in: T)(implicit tt: TypeTag[T], env: CodecEnvironment) = CaseClassCodec(in, tt.tpe, env)

	object OptionCodec extends Coder
	{
		private[codec] def apply(in: Any, t: Type, env: CodecEnvironment): Option[JsonValue[_]] =
		{
			if(t <:< ru.typeOf[Option[_]])
			{
				t match {
					case pt: TypeRef =>
						in match {
							case Some(v) =>
								doEncode(v, pt.args(0), env)
							case None =>
								Some(JsonNull)
						}
					case _ =>
						None
				}
			}
			else
				None
		}
	}

	object CaseClassCodec extends Coder
	{

		private[codec] def apply(in: Any, t: Type, env: CodecEnvironment): Option[JsonValue[_]] = {


			val m = ru.runtimeMirror(in.getClass.getClassLoader)

			if(!t.typeSymbol.asClass.isCaseClass)
				return None

			val ctor = t.declaration(ru.nme.CONSTRUCTOR).asMethod

			if(ctor.paramss.length != 1)
				return None

			val im = m.reflect(in)(ClassTag(m.runtimeClass(t)))

			Some(JsonObject(ctor.paramss(0).flatMap{p =>
				val n = p.name.decoded

				val m:FieldMirror = im.reflectField(t.declaration(ru.newTermName(n)).asTerm)
				val rt = m.symbol.asTerm.getter.asMethod.returnType
				val v = m.get

				doEncode(v, rt, env).map{(n -> _ :: Nil)}.getOrElse{Nil}

			}.toMap))
		}

	}

}

