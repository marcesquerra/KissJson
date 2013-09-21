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
import scala.util.Try
import scala.util.Failure
import com.bryghts.safedynamic.SafeDynamic
import com.bryghts.safedynamic.ApplyAfterSafeDynamic
import com.bryghts.safedynamic.HasApply

package object kissjson
{

	type JsonValue      = JsonValueBase[_]
	type MatchJsonValue = JsonValueBase[_]

	sealed abstract class JsonValueBase[+T] extends IndexedSeq[JsonValue] with SafeDynamic[JsonValue] with ApplyAfterSafeDynamic[Int, JsonValue, JsonValue] with HasApply[Int, JsonValue]
	{
		def selectDynamic(name: String): JsonValue = JsonNull

		private[kissjson] def select(k: String): JsonValue = JsonNull

		def toString: String
		def render(implicit renderer: Renderer): String = renderer.render(this)

		def apply(idx: Int): JsonValue = JsonNull
		def length: Int = 0
		def getOrElse[B >: T](default: => B): B = v
		def asOption: Option[T] = Some(v)
		def asMap(): Map[String, JsonValue] = Map()

		private[kissjson] def v: T
		def isNull:Boolean = false

		override def equals(in: Any) = in match
		{
			case _: MatchJsonNull => this eq JsonNull
			case jv: MatchJsonValue => jv.v == v
			case _ => false
		}

		final def as[T](implicit tt: TypeTag[T], env: DecoderEnvironment): Try[T] = tryToDecode(this, tt.tpe, env) match {
			case Some(r) => r.asInstanceOf[Try[T]]
			case None => Failure(new Exception("There is no available Decoder"))
		}
	}


////////////////////////////////////////////////////////////////////////////////
// NULL
////////////////////////////////////////////////////////////////////////////////

	type MatchJsonNull = JsonNull.type
	type JsonNull      = JsonNull.type

	object JsonNull extends JsonValueBase[Nothing]
	{
		override def toString = "null"
		private[kissjson] def v = throw new Exception("")
		override def getOrElse[B >: Nothing](default: => B): B = default
		override def asOption: Option[Nothing] = None

		def unapply(v: JsonValue): Boolean = {
			v eq this
		}

		override def equals(in: Any) = in.isInstanceOf[AnyRef] && in.asInstanceOf[AnyRef].eq(this)
		override def isNull: Boolean = true
	}

////////////////////////////////////////////////////////////////////////////////
// Boolean
////////////////////////////////////////////////////////////////////////////////

	type JsonBoolean = JsonValueBase[Boolean]
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

	type JsonString = JsonValueBase[String]
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


	type JsonNumber       = JsonValueBase[Number]
	type MatchJsonNumber  = JsonNumberImpl[_]

	type JsonInteger      = JsonValueBase[IntegerNumber]
	type MatchJsonInteger = JsonIntegerImpl

	type JsonReal         = JsonValueBase[RealNumber]
	type MatchJsonReal    = JsonRealImpl

	sealed abstract class JsonNumberImpl[T <: Number] extends JsonValueBase[T]

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
		def apply(value: Number):         JsonNumber   = value match{case i: IntegerNumber => new JsonIntegerImpl(i) case r: RealNumber => new JsonRealImpl(r)}

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
		def apply(value: Byte):           JsonInteger  = new JsonIntegerImpl(value)
		def apply(value: Char):           JsonInteger  = new JsonIntegerImpl(value)
		def apply(value: Short):          JsonInteger  = new JsonIntegerImpl(value)
		def apply(value: Int):            JsonInteger  = new JsonIntegerImpl(value)
		def apply(value: Long):           JsonInteger  = new JsonIntegerImpl(value)

		def unapply(in: Any): Option[IntegerNumber] = in match {
			case v: MatchJsonInteger => Some(v.v)
			case _ => None
		}
	}

	object JsonReal
	{
		def apply(value: Float):          JsonReal     = new JsonRealImpl(value)
		def apply(value: Double):         JsonReal     = new JsonRealImpl(value)

		def unapply(in: Any): Option[RealNumber] = in match {
			case v: MatchJsonReal => Some(v.v)
			case _ => None
		}
	}

////////////////////////////////////////////////////////////////////////////////
// Array
////////////////////////////////////////////////////////////////////////////////

	type JsonArray      = JsonValueBase[Vector[JsonValue]]
	type MatchJsonArray = JsonArrayImpl

	final class JsonArrayImpl private[kissjson](private[kissjson] val v: Vector[JsonValue]) extends JsonArray
	{
		override def toString: String = v.mkString("[", ", ", "]")

		override def apply(idx: Int): JsonValue = if(idx < 0) JsonNull else if(idx >= length) JsonNull else v(idx)
		override def length: Int = v.length
	}

	object JsonArray
	{
		def apply(value: Traversable[JsonValue]):JsonArray = new JsonArrayImpl(value.toVector)
		def apply(in: JsonValue*) = new JsonArrayImpl(in.toVector)

		def unapply(in: Any): Option[Vector[JsonValue]] = in match {
			case v: MatchJsonArray => Some(v.v)
			case _ => None
		}
	}

	class JsonArrayBuilder(source: Builder[JsonValue, Vector[JsonValue]]) extends Builder[JsonValue, JsonArray]
	{self =>
		def +=(elem: JsonValue) = new JsonArrayBuilder(source += elem).asInstanceOf[self.type]

		def clear(): Unit = source.clear
		def result(): JsonArray = JsonArray(source.result())
	}

	implicit object CanBuildJsonArray extends CanBuildFrom[IndexedSeq[JsonValue],JsonValue,JsonArray]
	{
		private def s(implicit cbf: CanBuildFrom[Vector[JsonValue], JsonValue, Vector[JsonValue]]) = cbf
		private val source = s

		def apply(): Builder[JsonValue, JsonArray] = new JsonArrayBuilder(source())
		def apply(from: IndexedSeq[JsonValue]): Builder[JsonValue, JsonArray] = from match {
			case a: MatchJsonArray => new JsonArrayBuilder(source(a.v))
			case _ => new JsonArrayBuilder(source(Vector()))
		}
	}

	private trait JsonBuilder[T, JT <: JsonValueBase[T]]{
		def apply(in: T): JT
	}

	private def CreateBuilder[T, JT <: JsonValue](b: T => JT): CanBuildFrom[IndexedSeq[JsonValue], T, JsonArray] = {

			class JtJsonArrayBuilder(source: Builder[JT, Vector[JT]]) extends Builder[T, JsonArray]
			{self =>
				def +=(elem: T) = new JtJsonArrayBuilder(source += b(elem)).asInstanceOf[self.type]

				def clear(): Unit = source.clear
				def result(): JsonArray = JsonArray(source.result())
			}

			new CanBuildFrom[IndexedSeq[JsonValue],T,JsonArray]
			{
				private val source: CanBuildFrom[Vector[JsonValue], JT, Vector[JT]] = implicitly

				def apply(): Builder[T, JsonArray] = new JtJsonArrayBuilder(source())
				def apply(from: IndexedSeq[JsonValue]): Builder[T, JsonArray] = from match {
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

	type JsonObject      = JsonValueBase[Map[String, JsonValue]]
	type MatchJsonObject = JsonObjectImpl

	final class JsonObjectImpl private[kissjson](internal: Map[String, JsonValue]) extends JsonObject
	{
		private[kissjson] val v = internal.filterNot{case (_, v) => v == JsonNull}

		override def toString: String = v.map{case (k, v) => s""""$k": "$v""""}.mkString("{", ", ", "}")

		override def selectDynamic(name: String): JsonValue = v.get(name).getOrElse(JsonNull)

		override private[kissjson] def select(k: String): JsonValue = v.get(k).getOrElse(JsonNull)

		override def asMap():Map[String, JsonValue] = v
	}

	object JsonObject
	{
		def apply(value: (String, JsonValue)*):JsonObject = new JsonObjectImpl(value.toMap)
		def apply(value: Map[String, JsonValue]):JsonObject = new JsonObjectImpl(value)

		def unapply(in: Any): Option[Map[String, JsonValue]] = in match {
			case v: MatchJsonObject => Some(v.v)
			case _ => None
		}
	}



////////////////////////////////////////////////////////////////////////////////
// Codecs
////////////////////////////////////////////////////////////////////////////////

	implicit class ToJsonConversor[T](val in: T)(implicit t: TypeTag[T], env: CoderEnvironment) {
		def asJson:Try[JsonValue] = tryToEncode(in, t.tpe, env) getOrElse {Failure(new Exception(""))}
	}

////////////////////////////////////////////////////////////////////////////////
// Literals & Tools
////////////////////////////////////////////////////////////////////////////////

	implicit def implicitJson(in: String):  JsonString    = JsonString  (in)
	implicit def implicitJson(in: Boolean): JsonBoolean   = JsonBoolean (in)
	implicit def implicitJson(in: Null):    JsonNull.type = JsonNull
	implicit def implicitJson(in: Byte):    JsonInteger   = JsonNumber  (in : IntegerNumber)
	implicit def implicitJson(in: Char):    JsonInteger   = JsonNumber  (in : IntegerNumber)
	implicit def implicitJson(in: Short):   JsonInteger   = JsonNumber  (in : IntegerNumber)
	implicit def implicitJson(in: Int):     JsonInteger   = JsonNumber  (in : IntegerNumber)
	implicit def implicitJson(in: Long):    JsonInteger   = JsonNumber  (in : IntegerNumber)
	implicit def implicitJson(in: Float):   JsonReal      = JsonNumber  (in : RealNumber)
	implicit def implicitJson(in: Double):  JsonReal      = JsonNumber  (in : RealNumber)

	object J extends Dynamic
	{
		private def fixName(in: String): String = {
			val regex =  """\$u[\dABCDEFabcdef][\dABCDEFabcdef][\dABCDEFabcdef][\dABCDEFabcdef]""".r

			regex replaceAllIn (in, m => {

				Integer
					.parseInt(m.matched.substring(2), 16)
					.toChar
					.toString
			})
		}

		private def fixNames(in: (String, JsonValue)*) = in.map{p => (fixName(p._1), p._2)}

		def applyDynamicNamed(method: String)(in: (String, JsonValue)*) = JsonObject(fixNames(in :_*):_*)
		def applyDynamic(method: String)(in: JsonValue*) = JsonArray(in:_*)
	}


	implicit class StringJsonExtensions(val in: String) extends AnyVal
	{
		@inline
		def asJson: Try[JsonValue] = parser.JsonParser(in)

		@inline
		def :=[U <% JsonValue] (value: U): Tuple2[String, JsonValue] = Tuple2(in, value)

		@inline
		def := (value: Null): Tuple2[String, JsonValue] = Tuple2(in, JsonNull)
	}

	implicit class SymbolJsonExtensions(val in: Symbol) extends AnyVal
	{
		@inline
		def :=[U <% JsonValue] (value: U): Tuple2[String, JsonValue] = Tuple2(in.name, value)

		@inline
		def := (value: Null): Tuple2[String, JsonValue] = Tuple2(in.name, JsonNull)
	}

	implicit object compact extends CompactObjectRenderer

}

