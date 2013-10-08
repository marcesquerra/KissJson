package bryghts.tests

import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.scalacheck._
import org.junit.runner.RunWith
import com.bryghts.kissjson._
import org.specs2.ScalaCheck

@RunWith(classOf[JUnitRunner])
class KissjsonSpecs extends Specification with ScalaCheck
{



	"Reqarding equality" >>
	{

		"a JsonValue" should
		{

			"be equal to another JsonValue when both have the same value and are not null" in{
				val a: JsonValue = 3
				val b = JsonNumber(3)
				a mustEqual b
			}

			"be equal to another JsonValue when both are null" in{
				val a: JsonValue = JsonNull
				val b = JsonNull
				a mustEqual b
			}

			"not be equal to another JsonValue when it's null and the other is a" >> {
				"JsonBoolean" in{
					val a = JsonNull
					val b: JsonValue = true
					a mustNotEqual b
				}

				"JsonString" in{
					val a = JsonNull
					val b: JsonValue = "string"
					a mustNotEqual b
				}

				"JsonNumber" in{
					val a = JsonNull
					val b: JsonValue = 3
					a mustNotEqual b
				}

				"JsonObject" in{
					val a = JsonNull
					val b: JsonValue = J(name = "John")
					a mustNotEqual b
				}

				"JsonArray" in{
					val a = JsonNull
					val b: JsonValue = J(1, 2, 3)
					a mustNotEqual b
				}

				"complex JsonArray" in{
					val a = JsonNull
					val b: JsonValue = J(J(1, 2, 3), "string")
					a mustNotEqual b
				}
			}

			"not be equal to another JsonValue when it's a not null" >>{

				"JsonBoolean and the other is null" in{
					val a: JsonValue = true
					val b = JsonNull
					a mustNotEqual b
				}

				"JsonString and the other is null" in{
					val a: JsonValue = "string"
					val b = JsonNull
					a mustNotEqual b
				}

				"JsonNumber and the other is null" in{
					val a: JsonValue = 3
					val b = JsonNull
					a mustNotEqual b
				}

				"JsonObject and the other is null" in{
					val a: JsonValue = J(name = "John")
					val b = JsonNull
					a mustNotEqual b
				}

				"JsonArray and the other is null" in{
					val a: JsonValue = J(1, 2, 3)
					val b = JsonNull
					a mustNotEqual b
				}

				"complex JsonArray and the other is null" in{
					val a: JsonValue = J(J(1, 2, 3), "string")
					val b = JsonNull
					a mustNotEqual b
				}

			}
		}

		"equals method should work properly for arrays" in{
			val a = J(1, J(2, 3))
			val b = J(1, J(2, 3))
			val c = J(4, J(5, 6))

			a mustEqual a
			a mustNotEqual c
		}

		"equals method should work properly for objects" in{
			val a = J(name = "John",     surname = "Watson")
			val b = J(name = "John",     surname = "Watson")
			val c = J(name = "Sherlock", surname = "Holmes")

			a mustEqual a
			a mustNotEqual c
		}








		"a JsonValue should be equal to JsonNull when" >> {
			"is null" in {
				JsonNull mustEqual JsonNull
			}

			"it's a null JsonBoolean" in {
				val v: JsonBoolean = JsonNull
				v mustEqual JsonNull
				JsonNull mustEqual v
			}

			"it's a null JsonString" in {
				val v: JsonString = JsonNull
				v mustEqual JsonNull
				JsonNull mustEqual v
			}

			"it's a null JsonNumber" in {
				val v: JsonNumber = JsonNull
				v mustEqual JsonNull
				JsonNull mustEqual v
			}

			"it's a null JsonObject" in {
				val v: JsonObject = JsonNull
				v mustEqual JsonNull
				JsonNull mustEqual v
			}

			"it's a null JsonArray" in {
				val v: JsonArray[_] = JsonNull

				(v == JsonNull) must beTrue
				(JsonNull == v) must beTrue

				(v equals JsonNull) must beTrue
				(JsonNull equals v) must beTrue
			}

			"it's a null complex JsonArray" in {
				val v: JsonArray[JsonArray[JsonString]] = JsonNull
				v mustEqual JsonNull
			}
		}
	}



	"""Regarding "Option" like behaviour, a JsonValue""" should {

		"identify himself as null when" >> {
			"is null" in {
				JsonNull.isNull must beTrue
			}

			"it's a null JsonBoolean" in {
				val v: JsonBoolean = JsonNull
				v.isNull must beTrue
			}

			"it's a null JsonString" in {
				val v: JsonString = JsonNull
				v.isNull must beTrue
			}

			"it's a null JsonNumber" in {
				val v: JsonNumber = JsonNull
				v.isNull must beTrue
			}

			"it's a null JsonObject" in {
				val v: JsonObject = JsonNull
				v.isNull must beTrue
			}

			"it's a null JsonArray" in {
				val v: JsonArray[_] = JsonNull
				v.isNull must beTrue
			}

			"it's a null complex JsonArray" in {
				val v: JsonArray[JsonArray[JsonString]] = JsonNull
				v.isNull must beTrue
			}
		}


		"not identify himself as null when" >> {
			"is not null" in {
				val v: JsonValue = JsonBoolean(true)
				v.isNull must beFalse
			}

			"it's a not null JsonBoolean" in {
				val v: JsonBoolean = true
				v.isNull must beFalse
			}

			"it's a not null JsonString" in {
				val v: JsonString = "string"
				v.isNull must beFalse
			}

			"it's a not null JsonNumber" in {
				val v: JsonNumber = 3
				v.isNull must beFalse
			}

			"it's a not null JsonObject" in {
				val v: JsonObject = J(name = "Jhon")
				v.isNull must beFalse
			}

			"it's a not null JsonArray" in {
				val v: JsonArray[_] = J(true)
				v.isNull must beFalse
			}

			"it's a not null complex JsonArray" in {
				val v: JsonArray[JsonArray[JsonString]] = J(J("Jhon"))
				v.isNull must beFalse
			}
		}




		"return it's content with the method getOrElse when" >> {
			"is not null" in {
				val v: JsonValue = JsonBoolean(true)
				v.getOrElse(throw new Exception("Failed test")) mustEqual true
			}

			"it's a not null JsonBoolean" in {
				val v: JsonBoolean = true
				v.getOrElse(throw new Exception("Failed test")) mustEqual true
			}

			"it's a not null JsonString" in {
				val v: JsonString = "string"
				v.getOrElse(throw new Exception("Failed test")) mustEqual "string"
			}

			"it's a not null JsonNumber" in {
				val v: JsonNumber = 3
				v.getOrElse(throw new Exception("Failed test")) mustEqual 3
			}

			"it's a not null JsonObject" in {
				val v: JsonObject = J(name = "Jhon")
				v.getOrElse(throw new Exception("Failed test")) mustEqual Map("name" -> JsonString("Jhon"))
			}

			"it's a not null JsonArray" in {
				val v: JsonArray[_] = J(true)
				v.getOrElse(throw new Exception("Failed test")) mustEqual Vector(JsonBoolean(true))
			}

			"it's a not null complex JsonArray" in {
				val v: JsonArray[JsonArray[JsonString]] = J(J("Jhon"))
				v.getOrElse(throw new Exception("Failed test")) mustEqual Vector(J("Jhon"))
			}
		}



		"return the default value from the method getOrElse when" >> {

			"is null" in {
				val v: JsonValue = JsonNull
				v.getOrElse(Default) mustEqual Default
			}

			"it's a null JsonBoolean" in {
				val v: JsonBoolean = JsonNull
				v.getOrElse(Default) mustEqual Default
			}

			"it's a null JsonString" in {
				val v: JsonString = JsonNull
				v.getOrElse(Default) mustEqual Default
			}

			"it's a null JsonNumber" in {
				val v: JsonNumber = JsonNull
				v.getOrElse(Default) mustEqual Default
			}

			"it's a null JsonObject" in {
				val v: JsonObject = JsonNull
				v.getOrElse(Default) mustEqual Default
			}

			"it's a null JsonArray" in {
				val v: JsonArray[_] = JsonNull
				v.getOrElse(Default) mustEqual Default
			}

			"it's a null complex JsonArray" in {
				val v: JsonArray[JsonArray[JsonString]] = JsonNull
				v.getOrElse(Default) mustEqual Default
			}
		}
	}



	"The selectDynamic method" should {

		"always return JsonNull for" >> {
			"JsonNull"    ! check { (name: String) => JsonNull             .selectDynamic(name) mustEqual JsonNull}
			"JsonBoolean" ! check { (name: String) => JsonBoolean(true)    .selectDynamic(name) mustEqual JsonNull}
			"JsonString"  ! check { (name: String) => JsonString("string") .selectDynamic(name) mustEqual JsonNull}
			"JsonNumber"  ! check { (name: String) => JsonNumber(3)        .selectDynamic(name) mustEqual JsonNull}
			"JsonArray"   ! check { (name: String) => J(1, 2, 3)           .selectDynamic(name) mustEqual JsonNull}
		}

		"return JsonNull for all non existing keys in a JsonObject" ! check { (name: String) => J(name = "John", age = 32) .selectDynamic(name) mustEqual JsonNull when (name != "name" && name != "age")}

		"return the asociated value for all existing keys in a JsonObject" in {
			val v = J(name = "John", age = 32)

			v.selectDynamic("name") mustEqual JsonString("John")
			v.selectDynamic("age") mustEqual JsonNumber(32)
		}

	}

// TOOLS

	private case object Default
}


