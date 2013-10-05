package com.bryghts

import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.universe.typeOf
import scala.language.implicitConversions
import scala.language.dynamics
import com.bryghts.safedynamic.SafeDynamic
import com.bryghts.kissjson.renderer.Renderer
import com.bryghts.kissjson.codec._
import scala.util.Try
import scala.util.Failure
import com.bryghts.kissnumber._
import com.bryghts.kissjson.renderer.CompactObjectRenderer
import scala.reflect.ClassTag

package object kissjson
{

////////////////////////////////////////////////////////////////////////////////
// JsonValue
////////////////////////////////////////////////////////////////////////////////


type JsonValue = JsonValueBase[_]

sealed trait JsonValueBase[+T] extends SafeDynamic[JsonValue]
{
	private [kissjson] def v: T

	def getOrElse[B >: T](default: => B): B
	def isNull = false

	override def equals(in: Any): Boolean = in match {
		case jv: JsonValue if isNull => jv.isNull
		case jv: JsonValue => if(jv.isNull) false else jv.v == v
		case _ => false
	}

	def selectDynamic(name: String): JsonValue = JsonNull

	def toString: String
	def render(implicit renderer: Renderer): String = renderer.render(this)
	def asOption: Option[T] = Some(v)
	def asMap(): Map[String, JsonValue] = Map()

	final def as[T](implicit tt: TypeTag[T], env: DecoderEnvironment): Try[T] = tryToDecode(this, tt.tpe, env) match {
		case Some(r) => r.asInstanceOf[Try[T]]
		case None => Failure(new Exception("There is no available Decoder"))
	}
}


////////////////////////////////////////////////////////////////////////////////
// JsonNull
////////////////////////////////////////////////////////////////////////////////

trait JsonNullTrait[T] extends JsonValueBase[T]

type JsonNull = JsonNullTrait[_]

object JsonNull extends JsonNullTrait[Nothing]
{

	override def toString = "null"

	private[kissjson] def v: Nothing =
		throw new Exception("The method v should never be called in a Null object")

	override def getOrElse[B >: Nothing](default: => B): B = default
	override def isNull = true

	def unapply(in: JsonValue): Boolean = in.isNull

	override def equals(in: Any): Boolean = in match {
		case jv: JsonValue => jv.isNull
		case _ => false
	}

	override def asOption: Option[Nothing] = None
}

//implicit def convertJsonValueNull (in: JsonNull.type): JsonValue = JsonNull



////////////////////////////////////////////////////////////////////////////////
// JsonBoolean
////////////////////////////////////////////////////////////////////////////////

class JsonBoolean(private[kissjson] val v: Boolean) extends JsonValueBase[Boolean]
{
	def getOrElse[B >: Boolean](default: => B): B = v
	override def toString = v.toString
}

object JsonBoolean
{
	def apply(in: Boolean) = new JsonBoolean(in)
	def unapply(in: JsonValue): Option[Boolean] = in match {
		case n: JsonNull    => None
		case b: JsonBoolean => Some(b.v)
		case _              => None
	}
}

object NullJsonBoolean extends JsonBoolean(false) with JsonNullTrait[Boolean]
{
	override def getOrElse[B >: Boolean](default: => B): B = default
	override def isNull = true
	override def asOption: Option[Nothing] = None
	override def toString = "null"
}

implicit def convertBooleanNull (in: JsonNull.type): JsonBoolean = NullJsonBoolean
implicit def convertBooleanJson (in: Boolean): JsonBoolean = new JsonBoolean(in)

implicit val booleanJsonConversor: JsonConversor[Boolean, JsonBoolean] = new BasicJsonConversor[Boolean, JsonBoolean](convertBooleanJson, convertBooleanNull)


////////////////////////////////////////////////////////////////////////////////
// JsonString
////////////////////////////////////////////////////////////////////////////////

class JsonString(private[kissjson] val v: String) extends JsonValueBase[String]
{
	def getOrElse[B >: String](default: => B): B = v
	override def toString: String = v
}

object JsonString
{
	def apply(in: String) = new JsonString(in)
	def unapply(in: JsonValue): Option[String] = in match {
		case n: JsonNull    => None
		case b: JsonString  => Some(b.v)
		case _              => None
	}
}

object NullJsonString extends JsonString("") with JsonNullTrait[String]
{
	override def getOrElse[B >: String](default: => B): B = default
	override def isNull = true
	override def asOption: Option[Nothing] = None
	override def toString = "null"
}

implicit def convertStringNull  (in: JsonNull.type): JsonString  = NullJsonString
implicit def convertStringJson  (in: String): JsonString         = new JsonString(in)

implicit val stringJsonConversor: JsonConversor[String, JsonString] = new BasicJsonConversor[String, JsonString](convertStringJson, convertStringNull)

////////////////////////////////////////////////////////////////////////////////
// JsonNumber
////////////////////////////////////////////////////////////////////////////////

class JsonNumber(private[kissjson] val v: Number) extends JsonValueBase[Number]
{
	def getOrElse[B >: Number](default: => B): B = v
	override def toString: String = v.toString
}

object JsonNumber
{
	def apply(value: Number):         JsonNumber   = new JsonNumber(value)

	def apply(value: Byte):           JsonNumber  = new JsonNumber(value)
	def apply(value: Char):           JsonNumber  = new JsonNumber(value)
	def apply(value: Short):          JsonNumber  = new JsonNumber(value)
	def apply(value: Int):            JsonNumber  = new JsonNumber(value)
	def apply(value: Long):           JsonNumber  = new JsonNumber(value)

	def apply(value: Float):          JsonNumber     = new JsonNumber(value)
	def apply(value: Double):         JsonNumber     = new JsonNumber(value)

	def unapply(in: JsonValue): Option[Number] = in match {
		case n: JsonNull    => None
		case b: JsonNumber  => Some(b.v)
		case _              => None
	}
}

object NullJsonNumber extends JsonNumber(0) with JsonNullTrait[Number]
{
	override def getOrElse[B >: Number](default: => B): B = default
	override def isNull = true
	override def asOption: Option[Nothing] = None
	override def toString = "null"
}

implicit def convertNumberNull (in: JsonNull.type): JsonNumber = NullJsonNumber

implicit def convertNumberJson[T <% Number] (in: T): JsonNumber = JsonNumber(in)

implicit def NumberJsonConversor[T <% Number]: JsonConversor[T, JsonNumber] = new BasicJsonConversor[T, JsonNumber](convertNumberJson, convertNumberNull)
//implicit val ByteJsonConversor:   JsonConversor[Byte,   JsonNumber] = new BasicJsonConversor[Byte,   JsonNumber](convertByteJson,   convertNumberNull)
//implicit val CharJsonConversor:   JsonConversor[Char,   JsonNumber] = new BasicJsonConversor[Char,   JsonNumber](convertCharJson,   convertNumberNull)
//implicit val ShortJsonConversor:  JsonConversor[Short,  JsonNumber] = new BasicJsonConversor[Short,  JsonNumber](convertShortJson,  convertNumberNull)
//implicit val IntJsonConversor:    JsonConversor[Int,    JsonNumber] = new BasicJsonConversor[Int,    JsonNumber](convertIntJson,    convertNumberNull)
//implicit val LongJsonConversor:   JsonConversor[Long,   JsonNumber] = new BasicJsonConversor[Long,   JsonNumber](convertLongJson,   convertNumberNull)
//implicit val FloatJsonConversor:  JsonConversor[Float,  JsonNumber] = new BasicJsonConversor[Float,  JsonNumber](convertFloatJson,  convertNumberNull)
//implicit val DoubleJsonConversor: JsonConversor[Double, JsonNumber] = new BasicJsonConversor[Double, JsonNumber](convertDoubleJson, convertNumberNull)

implicit def convertIntegerNumberJson (in: IntegerNumber): JsonNumber = new JsonNumber(in)
implicit def convertByteJson          (in: Byte):          JsonNumber = new JsonNumber(in)
implicit def convertCharJson          (in: Char):          JsonNumber = new JsonNumber(in)
implicit def convertShortJson         (in: Short):         JsonNumber = new JsonNumber(in)
implicit def convertIntJson           (in: Int):           JsonNumber = new JsonNumber(in)
implicit def convertLongJson          (in: Long):          JsonNumber = new JsonNumber(in)

implicit def convertRealNumberJson    (in: RealNumber):    JsonNumber = new JsonNumber(in)
implicit def convertFloatJson         (in: Float):         JsonNumber = new JsonNumber(in)
implicit def convertDoubleJson        (in: Double):        JsonNumber = new JsonNumber(in)

////////////////////////////////////////////////////////////////////////////////
// JsonArray
////////////////////////////////////////////////////////////////////////////////

class JsonArray[+T <: JsonValue] (private[kissjson] val v: Vector[T])(implicit toNull: JsonNull.type => T)  extends JsonValueBase[Vector[T]] with IndexedSeq[T]
{
	def getOrElse[B >: Vector[T]](default: => B): B = v
	def apply(i: Int): T = if(i < 0) JsonNull else if(i >= length) JsonNull else v(i)
	def length: Int = v.length

	override def toString: String = v.mkString("[", ", ", "]")
}

class NullJsonArray[T <: JsonValue](implicit toNull: JsonNull.type => T) extends JsonArray[T](Vector())(toNull) with JsonNullTrait[Vector[T]]
{
	override def getOrElse[B >: Vector[T]](default: => B): B = default
	override def isNull = true
	override def asOption: Option[Nothing] = None
	override def toString = "null"
}

implicit def arrayJsonConversor[T <: JsonValue](implicit toNull: JsonNull.type => JsonArray[T]): JsonConversor[JsonArray[T], JsonArray[T]] =
	new BasicJsonConversor[JsonArray[T], JsonArray[T]](a => a, toNull)

object JsonArray
{
	def apply[T <: JsonValue](in: Traversable[T])(implicit toNull: JsonNull.type => T): JsonArray[T] = new JsonArray(in.toVector)(toNull)
}

implicit def convertArrayNull[T <: JsonValue] (in: JsonNull.type): JsonArray[T] = new NullJsonArray[T]()(t => ??? : T)

////////////////////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////////////////////

class JsonObject(internal: Map[String, JsonValue]) extends JsonValueBase[Map[String, JsonValue]]
{
	private[kissjson] val v = internal.filterNot{case (_, v) => v == JsonNull}

	def getOrElse[B >: Map[String, JsonValue]](default: => B): B = v
	override def selectDynamic(name: String): JsonValue = v.get(name).getOrElse(JsonNull)
	override def asMap(): Map[String, JsonValue] = v
}

object JsonObject
{
	def apply(in: Traversable[(String, JsonValue)]) = new JsonObject(in.toMap)
	def unapply(in: JsonValue): Option[Map[String, JsonValue]] = in match {
		case n: JsonNull    => None
		case b: JsonObject  => Some(b.v)
		case _              => None
	}
}

object NullJsonObject extends JsonObject(Map()) with JsonNullTrait[Map[String, JsonValue]]
{
	override def getOrElse[B >: Map[String, JsonValue]](default: => B): B = default
	override def isNull = true
	override def asOption: Option[Nothing] = None
	override def toString = "null"
}

implicit def convertObjectNull  (in: JsonNull.type): JsonObject  = NullJsonObject
implicit val objectJsonConversor: JsonConversor[JsonObject, JsonObject] = new BasicJsonConversor[JsonObject, JsonObject](a => a, convertObjectNull)

////////////////////////////////////////////////////////////////////////////////
// Codecs
////////////////////////////////////////////////////////////////////////////////

implicit class ToJsonConversor[T](val in: T)(implicit t: TypeTag[T], env: CoderEnvironment) {
	def asJson:Try[JsonValue] = tryToEncode(in, t.tpe, env) getOrElse {Failure(new Exception(""))}
}


////////////////////////////////////////////////////////////////////////////////
// Literals
////////////////////////////////////////////////////////////////////////////////

trait JsonConversor[T, That <: JsonValue] extends Function1[JsonNull.type, That]
{
	def apply(in: T): That
}

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

	def applyDynamicNamed(method: String)(in: (String, JsonValue)*) = JsonObject(fixNames(in :_*))

	def applyDynamic[That <: JsonValue, T](name: String)(in: T*)(implicit cnv: JsonConversor[T, That]): JsonArray[That] = new JsonArray(in.toVector.map(cnv(_)))(cnv)
}


class convertAnythingToJsonValue[T] extends JsonConversor[T, JsonValue]{
	def apply(in: T): JsonValue = in match {
		case n: Number     => JsonNumber(n)
		case n: Byte     => JsonNumber(n)
		case n: Char     => JsonNumber(n)
		case n: Short     => JsonNumber(n)
		case n: Int     => JsonNumber(n)
		case n: Long     => JsonNumber(n)
		case n: Float     => JsonNumber(n)
		case n: Double     => JsonNumber(n)
		case b: Boolean => JsonBoolean(b)
		case s: String  => JsonString(s)
		case v: JsonValue  => v
		case _ => JsonNull
	}

	def apply(in: JsonNull.type) = JsonNull
}

implicit object convertAnyToJsonValue    extends convertAnythingToJsonValue[Any]
implicit object convertAnyRefToJsonValue extends convertAnythingToJsonValue[AnyRef]
implicit object convertAnyValToJsonValue extends convertAnythingToJsonValue[AnyVal]

class BasicJsonConversor[T, That <: JsonValue](f: T => That, toNull: JsonNull.type => That) extends JsonConversor[T, That]
{
	def apply(in: T): That = f(in)
	def apply(in: JsonNull.type) = toNull(in)
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

