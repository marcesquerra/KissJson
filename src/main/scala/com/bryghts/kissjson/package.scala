package com.bryghts

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable.Builder
import scala.reflect.ClassTag
import scala.language.dynamics
import scala.language.implicitConversions

package object kissjson
{

	sealed abstract class JsonValue[+T] extends IndexedSeq[JsonValue[_]] with Dynamic
	{
		def selectDynamic(name: String): JsonValue[_] = JsonNull
		def applyDynamic(name: String)(i: Int): JsonValue[_] = selectDynamic(name).apply(i)

		def toString: String
		def toJson: String = toString

		def apply(idx: Int): JsonValue[_] = JsonNull
		def length: Int = 0
		def value: Any
		def isNull:Boolean = false

		override def equals(in: Any) = in.isInstanceOf[JsonValue[_]] && in.asInstanceOf[JsonValue[_]].value == value
	}


////////////////////////////////////////////////////////////////////////////////
// NULL
////////////////////////////////////////////////////////////////////////////////

	object JsonNull extends JsonValue[Nothing]
	{
		override def toString = "null"
		val value: Null = null
		def unapply(v: JsonValue[_]): Boolean = {
			println("....." + v)
//			if(v eq this) Some(this) else None
			v eq this
		}

		override def equals(in: Any) = in.isInstanceOf[AnyRef] && in.asInstanceOf[AnyRef].eq(this)
		override def isNull: Boolean = true
	}

////////////////////////////////////////////////////////////////////////////////
// Boolean
////////////////////////////////////////////////////////////////////////////////

	type JsonBoolean = JsonValue[Boolean]

	class JsonBooleanImpl private[kissjson](val value: Boolean) extends JsonBoolean
	{
		override def toString: String = value.toString
	}

	object JsonBoolean
	{
		def apply(value: Boolean):JsonBoolean = new JsonBooleanImpl(value)

		def unapply(in: Any): Option[Boolean] = in match {
			case v: JsonBooleanImpl => Some(v.value)
			case _ => None
		}
	}


////////////////////////////////////////////////////////////////////////////////
// STRING
////////////////////////////////////////////////////////////////////////////////

	type JsonString = JsonValue[String]

	class JsonStringImpl private[kissjson](val value: String) extends JsonString
	{
		override def toString: String = value
		override def toJson: String = '"' + value.replaceAll("\"","\\\"") + '"'
	}

	object JsonString
	{
		def apply(value: String):JsonString = new JsonStringImpl(value)

		def unapply(in: Any): Option[String] = in match {
			case v: JsonStringImpl => Some(v.value)
			case _ => None
		}
	}

////////////////////////////////////////////////////////////////////////////////
// Number
////////////////////////////////////////////////////////////////////////////////

	sealed abstract class NumberWrapper[T]{
		def value: T
	}

	case class IntegerWrapper (value: Long)   extends NumberWrapper[Long]
	case class RealWrapper    (value: Double) extends NumberWrapper[Double]

	type JsonNumber    = JsonValue[NumberWrapper[_]]
	type JsonInteger   = JsonValue[NumberWrapper[Long]]
	type JsonReal      = JsonValue[NumberWrapper[Double]]

	class JsonIntegerImpl private[kissjson](private[kissjson] val internal: IntegerWrapper) extends JsonInteger
	{
		def value = internal.value
		override def toString: String = value.toString
	}

	class JsonRealImpl private[kissjson](private[kissjson] val internal: RealWrapper) extends JsonReal
	{
		def value = internal.value
		override def toString: String = value.toString
	}

	object JsonNumber
	{
		def apply(value: Long):    JsonInteger = new JsonIntegerImpl(IntegerWrapper(value))
		def apply(value: Int):     JsonInteger = new JsonIntegerImpl(IntegerWrapper(value))
		def apply(value: Short):   JsonInteger = new JsonIntegerImpl(IntegerWrapper(value))
		def apply(value: Byte):    JsonInteger = new JsonIntegerImpl(IntegerWrapper(value))
		def apply(value: Double):  JsonReal    = new JsonRealImpl(RealWrapper(value))
		def apply(value: Float):   JsonReal    = new JsonRealImpl(RealWrapper(value))

		def unapply(in: Any): Option[NumberWrapper[_]] = in match {
			case v: JsonIntegerImpl => Some(v.internal)
			case v: JsonRealImpl    => Some(v.internal)
			case _ => None
		}
	}

	object JsonInteger
	{
		def unapply(in: Any): Option[Long] = in match {
			case v: JsonIntegerImpl => Some(v.value)
			case _ => None
		}
	}

	object JsonReal
	{
		def unapply(in: Any): Option[Double] = in match {
			case v: JsonRealImpl => Some(v.value)
			case _ => None
		}
	}
////////////////////////////////////////////////////////////////////////////////
// Array
////////////////////////////////////////////////////////////////////////////////

	type JsonArray = JsonValue[Vector[JsonValue[_]]]

	class JsonArrayImpl private[kissjson](val value: Vector[JsonValue[_]]) extends JsonArray
	{
		override def toString: String = value.mkString("[", ", ", "]")
		override def toJson: String = value.map{_.toJson}.mkString("[", ", ", "]")

		override def apply(idx: Int): JsonValue[_] = if(idx < 0) JsonNull else if(idx >= length) JsonNull else value(idx)
		override def length: Int = value.length
	}

	object JsonArray
	{
		def apply(value: Traversable[JsonValue[_]]):JsonArray = new JsonArrayImpl(value.toVector)
		def apply(in: JsonValue[_]*) = new JsonArrayImpl(in.toVector)

		def unapply(in: Any): Option[Vector[JsonValue[_]]] = in match {
			case v: JsonArrayImpl => Some(v.value)
			case _ => None
		}
	}

	class JsonArrayBuilder(source: Builder[JsonValue[_], Vector[JsonValue[_]]]) extends Builder[JsonValue[_], JsonArray]
	{self =>
		def +=(elem: JsonValue[_]) = new JsonArrayBuilder(source += elem).asInstanceOf[self.type]

		def clear(): Unit = source.clear
		def result(): JsonArray = JsonArray(source.result())
	}

	implicit object CanBuildJsonArray extends CanBuildFrom[IndexedSeq[JsonValue[_]],JsonValue[_],JsonArray]
	{
		private val source: CanBuildFrom[Vector[JsonValue[_]], JsonValue[_], Vector[JsonValue[_]]] = implicitly

		def apply(): Builder[JsonValue[_], JsonArray] = new JsonArrayBuilder(source())
		def apply(from: IndexedSeq[JsonValue[_]]): Builder[JsonValue[_], JsonArray] = from match {
			case a: JsonArrayImpl => new JsonArrayBuilder(source(a.value))
			case _ => new JsonArrayBuilder(source(Vector()))
		}
	}

	private trait JsonBuilder[T, JT <: JsonValue[T]]{
		def apply(in: T): JT
	}

	private def CreateBuilder[T, JT <: JsonValue[_]](b: T => JT): CanBuildFrom[IndexedSeq[JsonValue[_]], T, JsonArray] = {

			class JtJsonArrayBuilder(source: Builder[JT, Vector[JT]]) extends Builder[T, JsonArray]
			{self =>
				def +=(elem: T) = new JtJsonArrayBuilder(source += b(elem)).asInstanceOf[self.type]
		
				def clear(): Unit = source.clear
				def result(): JsonArray = JsonArray(source.result())
			}

			new CanBuildFrom[IndexedSeq[JsonValue[_]],T,JsonArray]
			{
				private val source: CanBuildFrom[Vector[JsonValue[_]], JT, Vector[JT]] = implicitly
		
				def apply(): Builder[T, JsonArray] = new JtJsonArrayBuilder(source())
				def apply(from: IndexedSeq[JsonValue[_]]): Builder[T, JsonArray] = from match {
					case a: JsonArrayImpl => new JtJsonArrayBuilder(source(a.value))
					case _ => new JtJsonArrayBuilder(source(Vector()))
				}
			}
	}

	
	implicit val CanBuildJsonArrayFromStrings  = CreateBuilder[String,  JsonString]  (JsonString  (_))
	implicit val CanBuildJsonArrayFromBooleans = CreateBuilder[Boolean, JsonBoolean] (JsonBoolean (_))
	implicit val CanBuildJsonArrayFromLongs    = CreateBuilder[Long,    JsonInteger] (JsonNumber  (_))
	implicit val CanBuildJsonArrayFromIntegers = CreateBuilder[Int,     JsonInteger] (JsonNumber  (_))
	implicit val CanBuildJsonArrayFromShorts   = CreateBuilder[Short,   JsonInteger] (JsonNumber  (_))
	implicit val CanBuildJsonArrayFromByte     = CreateBuilder[Byte,    JsonInteger] (JsonNumber  (_))
	implicit val CanBuildJsonArrayFromDoubles  = CreateBuilder[Double,  JsonReal]    (JsonNumber  (_))
	implicit val CanBuildJsonArrayFromFloats   = CreateBuilder[Float,   JsonReal]    (JsonNumber  (_))



////////////////////////////////////////////////////////////////////////////////
// JsonObject
////////////////////////////////////////////////////////////////////////////////

	type JsonObject = JsonValue[Map[String, JsonValue[_]]]

	class JsonObjectImpl private[kissjson](internal: Map[String, JsonValue[_]]) extends JsonObject with Dynamic
	{
		val value = internal.filterNot{case (_, v) => v == JsonNull}

		override def toString: String = value.map{case (k, v) => s""""$k": "$v""""}.mkString("{", ", ", "}")
		override def toJson: String   = value.map{case (k, v) => s""""${k.replaceAll("\"", "\\\"")}": ${v.toJson}"""}.mkString("{", ", ", "}")

		override def selectDynamic(name: String): JsonValue[_] = value.get(name).getOrElse(JsonNull)
	}

	object JsonObject
	{
		def apply(value: (String, JsonValue[_])*):JsonObject = new JsonObjectImpl(value.toMap)
		def apply(value: Map[String, JsonValue[_]]):JsonObject = new JsonObjectImpl(value)

		def unapply(in: Any): Option[Map[String, JsonValue[_]]] = in match {
			case v: JsonObjectImpl => Some(v.value)
			case _ => None
		}
	}

////////////////////////////////////////////////////////////////////////////////
// Literals & Tools
////////////////////////////////////////////////////////////////////////////////

	implicit def implicitJson(in: String):  JsonString    = JsonString  (in)
	implicit def implicitJson(in: Boolean): JsonBoolean   = JsonBoolean (in)
	implicit def implicitJson(in: Null):    JsonNull.type = JsonNull
	implicit def implicitJson(in: Byte):    JsonNumber    = JsonNumber  (in)
	implicit def implicitJson(in: Short):   JsonNumber    = JsonNumber  (in)
	implicit def implicitJson(in: Int):     JsonNumber    = JsonNumber  (in)
	implicit def implicitJson(in: Long):    JsonNumber    = JsonNumber  (in)
	implicit def implicitJson(in: Float):   JsonNumber    = JsonNumber  (in)
	implicit def implicitJson(in: Double):  JsonNumber    = JsonNumber  (in)

	def J(in: JsonValue[_]*)                = JsonArray(in:_*)
	def J(in: (String, JsonValue[_])*)      = JsonObject(in:_*)

	implicit class StringJsonExtensions(val in: String) extends AnyVal
	{
		@inline
		def asJson: Option[JsonValue[_]] = parser.JsonParser(in)

		@inline
		def :=[U <% JsonValue[_]] (value: U): Tuple2[String, JsonValue[_]] = Tuple2(in, value)
	}

}

