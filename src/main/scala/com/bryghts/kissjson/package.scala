package com.bryghts

import scala.language.dynamics
import scala.language.implicitConversions
import scala.collection.generic.CanBuildFrom
import scala.collection.mutable.Builder
import scala.reflect.ClassTag
import scala.util.parsing.json.JSON

package kissjson
{

	sealed trait JsonValue
	{

		def isNull = false

		def map(pf: PartialFunction[JsonValue, JsonValue]) = if(pf.isDefinedAt(this)) pf(this)

		def asArray:   Option[JsonArray]   = None
		def asObject:  Option[JsonObject]  = None
		def asBoolean: Option[JsonBoolean] = None
		def asNumber:  Option[JsonNumber]  = None
		def asString:  Option[JsonString]  = None

	}

	case object JsonNull extends JsonValue
	{
		override def isNull   = true
		override def toString = "null"
	}

	case class JsonBoolean(in: Boolean) extends JsonValue
	{
		override def toString  = in.toString
		override def asBoolean = Some(this)
	}

	case class JsonString(in: String) extends JsonValue
	{
		override def toString = '"' + in.replaceAll("\"", "\\\"") + '"'
		override def asString = Some(this)
	}

	sealed trait JsonNumber extends JsonValue
	{
		def toLong: Long
		def toDouble: Double
		override def asNumber = Some(this)
	}

	object JsonNumber
	{
		def apply(in: Byte):   JsonNumber  = JsonInteger(in)
		def apply(in: Short):  JsonNumber  = JsonInteger(in)
		def apply(in: Int):    JsonNumber  = JsonInteger(in)
		def apply(in: Long):   JsonNumber  = JsonInteger(in)
		def apply(in: Float):  JsonNumber  = JsonDouble(in)
		def apply(in: Double): JsonNumber  = JsonDouble(in)
	}

	case class JsonInteger(val in: Long) extends JsonNumber
	                                        with JsonValue
	{
		override def toLong   = in
		override def toDouble = in
	
		override def toString = in.toString
	}

	case class JsonDouble(val in: Double) extends JsonNumber
	                                         with JsonValue
	{
		override def toLong   = in.toInt
		override def toDouble = in
	
		override def toString = in.toString
	}

	class JsonArray(private val in: Vector[JsonValue]) extends JsonValue
	//                                         with IndexedSeq[JsonValue]
	{
		def apply(idx: Int): JsonValue = in(idx)
		def length: Int = in.length
	
		override def toString = in.mkString("[", ", ", "]")
	
		def map(f: JsonValue => JsonValue): JsonArray = new JsonArray(in map f)
		def flatMap(f: JsonValue => JsonArray): JsonArray = new JsonArray(in.flatMap{x => f(x).in})
		def filter(p: JsonValue => Boolean): JsonArray = new JsonArray(in filter p)
	
		def toVector[B](f: JsonValue => B): Vector[B] = in map f
		def toArray[B](f: JsonValue => B)(implicit ct: ClassTag[B]): Array[B] = toVector(f).toArray
		def toList[B](f: JsonValue => B): List[B] = (in map f).toList
		def foldLeft[B](z: B)(f: (B, JsonValue) => B): B = in.foldLeft(z)(f)
		def foldRight[B](z: B)(f: (JsonValue, B) => B): B = in.foldRight(z)(f)
	
		override def asArray:Option[JsonArray] = Some(this)
	}

	object JsonArray {
		def apply(in: JsonValue*) = new JsonArray(in.toVector)
		def apply(in: Traversable[JsonValue]) = new JsonArray(in.toVector)
		def unapply(in: JsonValue): Option[JsonArray] = in match {
		  case a: JsonArray => Some(a)
		  case _ => None
		}
	}

	class JsonObject(in: Map[String, JsonValue]) extends JsonValue
	                                                with Dynamic
	{
		def selectDynamic(name: String): JsonValue = in.get(name).getOrElse(JsonNull)
		def applyDynamic(name: String)(value: JsonValue): JsonObject = JsonObject(in + (name -> value))

		override def toString = in.map{p => '"' + p._1.replaceAll("\"", "\\\"") + "\": " + p._2}.mkString("{", ", ", "}")
		override def asObject = Some(this)
	}

	object JsonObject
	{
		def apply(in: Map[String, JsonValue]) = new JsonObject(in)
	
		def unapply(in: JsonValue): Option[JsonObject] = in match {
		  case a: JsonObject => Some(a)
		  case _ => None
		}
	}

}

package object kissjson
{
	implicit def implicitJson(in: String)   = JsonString (in)
	implicit def implicitJson(in: Byte)     = JsonInteger(in)
	implicit def implicitJson(in: Short)    = JsonInteger(in)
	implicit def implicitJson(in: Int)      = JsonInteger(in)
	implicit def implicitJson(in: Long)     = JsonInteger(in)
	implicit def implicitJson(in: Float)    = JsonDouble (in)
	implicit def implicitJson(in: Double)   = JsonDouble (in)

	def J(in: JsonValue*)                   = JsonArray(in:_*)
	def J(in: (String, JsonValue)*)         = JsonObject(in.toMap)

	implicit class StringJsonExtensions(val in: String) extends AnyVal
	{
		@inline
		def asJson: Option[JsonValue] = parser.JsonParser(in)

		@inline
		def := (value: JsonValue): Tuple2[String, JsonValue] = Tuple2(in, value)
	}

}

