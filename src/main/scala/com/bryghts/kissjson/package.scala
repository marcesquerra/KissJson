package com.bryghts

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable.Builder
import scala.reflect.ClassTag
import scala.language.dynamics
import scala.language.implicitConversions
import com.bryghts.kissjson.codec._
import scala.reflect.runtime.universe.TypeTag
import com.bryghts.kissnumber._
import com.bryghts.kissjson.renderer._

package object kissjson
{

	type MatchJsonValue = JsonValue[_]

	trait DynamicAccessor extends Dynamic {
		def selectDynamic(f: String): DynamicAccessor
		def applyDynamic(f: String)(): JsonValue[_]
	}

	object NullAccessor extends DynamicAccessor
	{
		def selectDynamic(f: String): DynamicAccessor = NullAccessor
		def applyDynamic(f: String)(): JsonValue[_]   = JsonNull
	}

	class ObjectAccessor(data: JsonValue[_]) extends DynamicAccessor
	{
		def selectDynamic(f: String): DynamicAccessor = new ObjectAccessor(data.select(f))
		def applyDynamic(f: String)(): JsonValue[_]   = data.select(f)
	}

	sealed abstract class JsonValue[+T] extends IndexedSeq[JsonValue[_]] //with Dynamic
	{
//		def selectDynamic(name: String): JsonValue[_] = JsonNull
//		def applyDynamic(name: String)(i: Int): JsonValue[_] = selectDynamic(name).apply(i)

		private[kissjson] def select(k: String): JsonValue[_] = JsonNull

		def apply(): DynamicAccessor = NullAccessor

		def toString: String
		def render(implicit renderer: Renderer): String = renderer.render(this)

		def apply(idx: Int): JsonValue[_] = JsonNull
		def length: Int = 0
		def getOrElse[B >: T](default: => B): B = v
		def asOption: Option[T] = Some(v)
		def asMap(): Map[String, JsonValue[_]] = Map()

		private[kissjson] def v: T
		def isNull:Boolean = false

		override def equals(in: Any) = in.isInstanceOf[JsonValue[_]] && in.asInstanceOf[JsonValue[_]].v == v
	}


////////////////////////////////////////////////////////////////////////////////
// NULL
////////////////////////////////////////////////////////////////////////////////

	type MatchJsonNull = JsonNull.type

	object JsonNull extends JsonValue[Unit]
	{
		override def toString = "null"
		private[kissjson] val v = ()
		override def getOrElse[B >: Unit](default: => B): B = default
		override def asOption: Option[Unit] = None

		def unapply(v: JsonValue[_]): Boolean = {
			v eq this
		}

		override def equals(in: Any) = in.isInstanceOf[AnyRef] && in.asInstanceOf[AnyRef].eq(this)
		override def isNull: Boolean = true
	}

////////////////////////////////////////////////////////////////////////////////
// Boolean
////////////////////////////////////////////////////////////////////////////////

	type JsonBoolean = JsonValue[Boolean]
	type MatchJsonBoolean = JsonBooleanImpl

	final class JsonBooleanImpl private[kissjson](private[kissjson] val v: Boolean) extends JsonBoolean
	{
		override def toString: String = v.toString
	}

	object JsonBoolean
	{
		def apply(value: Boolean):JsonBoolean = new JsonBooleanImpl(value)

		def unapply(in: Any): Option[Boolean] = in match {
			case value: MatchJsonBoolean => Some(value.v)
			case _ => None
		}
	}


////////////////////////////////////////////////////////////////////////////////
// STRING
////////////////////////////////////////////////////////////////////////////////

	type JsonString = JsonValue[String]
	type MatchJsonString = JsonStringImpl

	final class JsonStringImpl private[kissjson](private[kissjson] val v: String) extends JsonString
	{
		override def toString: String = v
	}

	object JsonString
	{
		def apply(value: String):JsonString = new JsonStringImpl(value)

		def unapply(in: Any): Option[String] = in match {
			case v: MatchJsonString => Some(v.v)
			case _ => None
		}
	}

////////////////////////////////////////////////////////////////////////////////
// Number
////////////////////////////////////////////////////////////////////////////////


	type JsonNumber       = JsonValue[Number]
	type MatchJsonNumber  = JsonNumberImpl[_]

	type JsonInteger      = JsonValue[IntegerNumber]
	type MatchJsonInteger = JsonIntegerImpl

	type JsonReal         = JsonValue[RealNumber]
	type MatchJsonReal    = JsonRealImpl

	sealed abstract class JsonNumberImpl[T <: Number] extends JsonValue[T]

	final class JsonIntegerImpl private[kissjson](private[kissjson] val v: IntegerNumber) extends JsonNumberImpl[IntegerNumber]
	{
		override def toString: String = v.toString
	}

	final class JsonRealImpl private[kissjson](private[kissjson] val v: RealNumber) extends JsonNumberImpl[RealNumber]
	{
		override def toString: String = v.toString
	}

	object JsonNumber
	{
		def apply(value: IntegerNumber):  JsonInteger  = new JsonIntegerImpl(value)
		def apply(value: RealNumber):     JsonReal     = new JsonRealImpl(value)

		def apply(value: Byte):           JsonInteger  = new JsonIntegerImpl(value)
		def apply(value: Char):           JsonInteger  = new JsonIntegerImpl(value)
		def apply(value: Short):          JsonInteger  = new JsonIntegerImpl(value)
		def apply(value: Int):            JsonInteger  = new JsonIntegerImpl(value)
		def apply(value: Long):           JsonInteger  = new JsonIntegerImpl(value)

		def apply(value: Float):          JsonReal     = new JsonRealImpl(value)
		def apply(value: Double):         JsonReal     = new JsonRealImpl(value)

		def unapply(in: Any): Option[Number] = in match {
			case v: MatchJsonInteger => Some(v.v)
			case v: MatchJsonReal    => Some(v.v)
			case _ => None
		}
	}

	object JsonInteger
	{
		def unapply(in: Any): Option[IntegerNumber] = in match {
			case v: MatchJsonInteger => Some(v.v)
			case _ => None
		}
	}

	object JsonReal
	{
		def unapply(in: Any): Option[RealNumber] = in match {
			case v: MatchJsonReal => Some(v.v)
			case _ => None
		}
	}

////////////////////////////////////////////////////////////////////////////////
// Array
////////////////////////////////////////////////////////////////////////////////

	type JsonArray      = JsonValue[Vector[JsonValue[_]]]
	type MatchJsonArray = JsonArrayImpl

	final class JsonArrayImpl private[kissjson](private[kissjson] val v: Vector[JsonValue[_]]) extends JsonArray
	{
		override def toString: String = v.mkString("[", ", ", "]")

		override def apply(idx: Int): JsonValue[_] = if(idx < 0) JsonNull else if(idx >= length) JsonNull else v(idx)
		override def length: Int = v.length
	}

	object JsonArray
	{
		def apply(value: Traversable[JsonValue[_]]):JsonArray = new JsonArrayImpl(value.toVector)
		def apply(in: JsonValue[_]*) = new JsonArrayImpl(in.toVector)

		def unapply(in: Any): Option[Vector[JsonValue[_]]] = in match {
			case v: MatchJsonArray => Some(v.v)
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
			case a: MatchJsonArray => new JsonArrayBuilder(source(a.v))
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
					case a: MatchJsonArray => new JtJsonArrayBuilder(source(a.v))
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

	type JsonObject      = JsonValue[Map[String, JsonValue[_]]]
	type MatchJsonObject = JsonObjectImpl

	final class JsonObjectImpl private[kissjson](internal: Map[String, JsonValue[_]]) extends JsonObject with Dynamic
	{
		private[kissjson] val v = internal.filterNot{case (_, v) => v == JsonNull}

		override def toString: String = v.map{case (k, v) => s""""$k": "$v""""}.mkString("{", ", ", "}")

//		override def selectDynamic(name: String): JsonValue[_] = v.get(name).getOrElse(JsonNull)

		override private[kissjson] def select(k: String): JsonValue[_] = v.get(k).getOrElse(JsonNull)

		override def apply(): DynamicAccessor = new ObjectAccessor(this)

		override def asMap():Map[String, JsonValue[_]] = v
	}

	object JsonObject
	{
		def apply(value: (String, JsonValue[_])*):JsonObject = new JsonObjectImpl(value.toMap)
		def apply(value: Map[String, JsonValue[_]]):JsonObject = new JsonObjectImpl(value)

		def unapply(in: Any): Option[Map[String, JsonValue[_]]] = in match {
			case v: MatchJsonObject => Some(v.v)
			case _ => None
		}
	}



////////////////////////////////////////////////////////////////////////////////
// Codecs
////////////////////////////////////////////////////////////////////////////////

	implicit class CaseClassToJsonConversor[T <: Product](val in: T)(implicit t: TypeTag[T]) {
		def toJson:JsonValue[_] = caseClassCodec(in)(t, implicitly).getOrElse(JsonNull)
	}

////////////////////////////////////////////////////////////////////////////////
// Literals & Tools
////////////////////////////////////////////////////////////////////////////////

	implicit def implicitJson(in: String):  JsonString    = JsonString  (in)
	implicit def implicitJson(in: Boolean): JsonBoolean   = JsonBoolean (in)
	implicit def implicitJson(in: Null):    JsonNull.type = JsonNull
	implicit def implicitJson(in: Byte):    JsonNumber    = JsonNumber  (in : IntegerNumber)
	implicit def implicitJson(in: Char):    JsonNumber    = JsonNumber  (in : IntegerNumber)
	implicit def implicitJson(in: Short):   JsonNumber    = JsonNumber  (in : IntegerNumber)
	implicit def implicitJson(in: Int):     JsonNumber    = JsonNumber  (in : IntegerNumber)
	implicit def implicitJson(in: Long):    JsonNumber    = JsonNumber  (in : IntegerNumber)
	implicit def implicitJson(in: Float):   JsonNumber    = JsonNumber  (in : RealNumber)
	implicit def implicitJson(in: Double):  JsonNumber    = JsonNumber  (in : RealNumber)

	def J(in: JsonValue[_]*)                = JsonArray(in:_*)
	def J(in: (String, JsonValue[_])*)      = JsonObject(in:_*)

	implicit class StringJsonExtensions(val in: String) extends AnyVal
	{
		@inline
		def asJson: Option[JsonValue[_]] = parser.JsonParser(in)

		@inline
		def :=[U <% JsonValue[_]] (value: U): Tuple2[String, JsonValue[_]] = Tuple2(in, value)
	}

	implicit object compact extends CompactObjectRenderer

}

