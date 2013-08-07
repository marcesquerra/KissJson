package com.bryghts.kissjson

import scala.reflect.ClassTag

import scala.reflect.runtime.universe._


package codec
{


trait Coder[T] {
	def apply(in: T)(implicit tt: TypeTag[T]): JsonValue[_]
}

trait Decoder[T] {
	def unapply(in: JsonValue[_]): Option[T]
}

trait Codec[T] extends Coder[T] with Decoder[T]


}

package object codec
{

	class CaseClassCodec[T <: Product] extends Coder[T]
	{

		import scala.reflect.runtime.{universe => ru}

		private val STRING  = ru.typeOf[String]
		private val BYTE    = ru.typeOf[Byte]
		private val SHORT   = ru.typeOf[Short]
		private val INT     = ru.typeOf[Int]
		private val LONG    = ru.typeOf[Long]
		private val FLOAT   = ru.typeOf[Float]
		private val DOUBLE  = ru.typeOf[Double]
		private val BOOLEAN = ru.typeOf[Boolean]

		def apply(in: T)(implicit tt: TypeTag[T]): JsonValue[_] = apply(in, tt.tpe)

		def apply(in: Any, t: Type): JsonValue[_] = {


			val m = ru.runtimeMirror(in.getClass.getClassLoader)

			if(!t.typeSymbol.asClass.isCaseClass)
				throw new RuntimeException("Only valid for case classes")

			val ctor = t.declaration(ru.nme.CONSTRUCTOR).asMethod

			if(ctor.paramss.length != 1)
				throw new RuntimeException("Only constructors with one set of parameters suported")

			val im = m.reflect(in)(ClassTag(m.runtimeClass(t)))

			JsonObject(ctor.paramss(0).map{p =>
				val n = p.name.decoded

				val m:FieldMirror = im.reflectField(t.declaration(ru.newTermName(n)).asTerm)
				val rt = m.symbol.asTerm.getter.asMethod.returnType
				val v = m.get

				rt match {
					case STRING  =>
						n -> JsonString(v.toString)
					case BYTE    =>
						n -> JsonNumber(v.asInstanceOf[Byte])
					case SHORT   =>
						n -> JsonNumber(v.asInstanceOf[Short])
					case INT     =>
						n -> JsonNumber(v.asInstanceOf[Int])
					case LONG    =>
						n -> JsonNumber(v.asInstanceOf[Long])
					case FLOAT   =>
						n -> JsonNumber(v.asInstanceOf[Float])
					case DOUBLE  =>
						n -> JsonNumber(v.asInstanceOf[Double])
					case BOOLEAN =>
						n -> JsonBoolean(v.asInstanceOf[Boolean])
					case _ =>
						if(rt.typeSymbol.isClass && rt.typeSymbol.asClass.isCaseClass){
							n -> new CaseClassCodec()(v, rt)
						}
						else
							n -> JsonNull
				}

			}.toMap)

		}

	}

}

