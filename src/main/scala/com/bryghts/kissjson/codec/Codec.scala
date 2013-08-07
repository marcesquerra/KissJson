package com.bryghts.kissjson

import scala.reflect.ClassTag


package codec
{


trait Coder[T] {
	def apply(in: T): JsonValue[_]
}

trait Decoder[T] {
	def unapply(in: JsonValue[_]): Option[T]
}

trait Codec[T] extends Coder[T] with Decoder[T]


}

package object codec
{

	import scala.reflect.runtime.universe._

	class CaseClassCodec[T : TypeTag](implicit ct: ClassTag[T]) extends Coder[T]
	{

		def apply(in: T): JsonValue[_] = {

			import scala.reflect.runtime.{universe => ru}

			val m = ru.runtimeMirror(in.getClass.getClassLoader)

			val t = ru.typeOf[T]

			if(!t.typeSymbol.asClass.isCaseClass)
				throw new RuntimeException("Only valid for case classes")

			val ctor = t.declaration(ru.nme.CONSTRUCTOR).asMethod

			if(ctor.paramss.length != 1)
				throw new RuntimeException("Only constructors with one set of parameters suported")

			val im = m.reflect(in)

			JsonObject(ctor.paramss(0).map{p =>
				val n = p.name.decoded

				val v:FieldMirror = im.reflectField(t.declaration(ru.newTermName(n)).asTerm)
				val t = v.symbol.asTerm.getter.

//				val j = v match {}

				n -> JsonNull
			}.toMap)

		}

	}

}