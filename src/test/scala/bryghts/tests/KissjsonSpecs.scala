package bryghts.tests

import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import com.bryghts.kissjson._

@RunWith(classOf[JUnitRunner])
class KissjsonSpecs extends Specification
{



	"Reqarding equality, a JsonValue" should {

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

		"not be equal to another JsonValue when it's null and the other is not null" in{
			val a = JsonNull
			val b = JsonNumber(2)
			a mustNotEqual b
		}

		"not be equal to another JsonValue when it's not null and the other is null" in{
			val a = JsonNumber(2)
			val b = JsonNull
			a mustNotEqual b
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








		"be equal to JsonNull when" >> {
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




// TOOLS

	private case object Default
}


