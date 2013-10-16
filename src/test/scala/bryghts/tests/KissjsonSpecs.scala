package bryghts.tests

import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.scalacheck._
import org.junit.runner.RunWith
import com.bryghts.kissjson._
import org.specs2.ScalaCheck
import com.bryghts.kissjson.renderer.Renderer
import org.specs2.execute.Result

@RunWith(classOf[JUnitRunner])
class KissjsonSpecs extends Specification with ScalaCheck
{



	"Reqarding equality a JsonValue" should
	{

		"be equal to another JsonValue when both have the same value and are not null" in{
			forallItemsIn(sampleValuesAB){case (a, b) => a mustEqual b}
		}

		"be equal to another JsonValue when both are null" in{
			forallItemsIn(nulls2){case (n1, n2) => n1 mustEqual n2}
		}

		"not be equal to another JsonValue when it's null and the other is not" in {
			forallNulls{n =>
				forallItemsIn(sampleValuesA){b => n mustNotEqual b}
			}
		}

		"not be equal to another JsonValue when it's not null and the other is" in {
			forallItemsIn(sampleValuesA){a =>
				forallNulls{n => a mustNotEqual n}
			}
		}

		"not be equal to another JsonValue neither is null but they are different" in {
			forallItemsIn(sampleValuesAiBi){case ((a, ai), (b, bi)) =>
				a mustNotEqual b when(ai != bi)
			}
		}

	}



	"""Regarding "Option" like behaviour, a JsonValue""" should {

		"identify himself as null when is null" in {
			forallNulls{n => n.isNull must beTrue}
		}


		"not identify himself as null when is not null" in {
			forallItemsIn(sampleValuesA){a => a.isNull must beFalse}
		}




		"return it's content with the method getOrElse when is not null" in {
			forallItemsIn(sampleValuesWithContent){case (v, r) =>
				v.getOrElse(throw new Exception("Failed test")) mustEqual r
			}
		}



		"return the default value from the method getOrElse when" in {

			forallNulls{case n => n.getOrElse(Default) mustEqual Default}
		}
	}



	"The selectDynamic method" should {

		"always return JsonNull for" >> {
			"JsonNull"    ! check { (name: String) =>forallNulls{n => n   .selectDynamic(name) mustEqual JsonNull}}

			"any non JsonObject" ! check{ (name: String) =>
				forallItemsIn(sampleValuesA.filterNot(_.isInstanceOf[JsonObject])){a =>
					a.selectDynamic(name) mustEqual JsonNull
				}
			}
		}

		"return JsonNull for all non existing keys in a JsonObject" ! check
		{ (name: String) =>
			J(name = "John", age = 32) .selectDynamic(name) mustEqual JsonNull when (name != "name" && name != "age")
		}

		"return the asociated value for all existing keys in a JsonObject" in {
			val v = J(name = "John", age = 32)

			v.selectDynamic("name") mustEqual JsonString("John")
			v.selectDynamic("age") mustEqual JsonNumber(32)
		}

	}

	"The toString method" should {

		"return \"null\" for JsonNull" in
			{forallNulls{n => n             .toString mustEqual "null"}}

		"return the apropriate text for non null values" in
			{forallItemsIn(sampleValuesWithStringRepresentations){case (v, l) => l.contains(v.toString) must beTrue}}

	}

	"The render method" should {

		case class MockRenderer() extends Renderer {

			var value: JsonValue = null

			def renderNull    (in: JsonNull,      out: StringBuilder): StringBuilder = {value = in; out}
			def renderNumber  (in: JsonNumber,    out: StringBuilder): StringBuilder = {value = in; out}
			def renderString  (in: JsonString,    out: StringBuilder): StringBuilder = {value = in; out}
			def renderBoolean (in: JsonBoolean,   out: StringBuilder): StringBuilder = {value = in; out}
			def renderArray   (in: JsonArray[_],  out: StringBuilder): StringBuilder = {value = in; out}
			def renderObject  (in: JsonObject,    out: StringBuilder): StringBuilder = {value = in; out}

		}

		"work with the render directly supplied"    in {forallValsAndNulls  {n =>          val r = MockRenderer(); n.render(r); n mustEqual r.value}}
		"work with the render implicitly supplied"  in {forallValsAndNulls  {n => implicit val r = MockRenderer(); n.render;    n mustEqual r.value}}

	}

	"The asOption method" should {

		"return None for null values" in {forallNulls{n => n.asOption mustEqual None}}
		"return Some(theWrappedValue) for non null values" in {forallItemsIn(sampleValuesWithContent){case (v, c) => v.asOption mustEqual Some(c)}}

	}

	"The asMap method" should {

		"return an empty Map for null values" in
			{ forallNulls{n => n.asMap mustEqual Map()} }

		"return an empty Map for non JsonObject values" in
			{ forallItemsIn(sampleValuesA.filterNot(_.isInstanceOf[JsonObject])){v => v.asMap mustEqual Map()}}

		"return the wrapped Map for JsonObjects" in
			{ forallItemsIn(sampleValuesWithContent.filter{case (v, _) => v.isInstanceOf[JsonObject]}){case (v, c) => v.asMap mustEqual c}}

	}


// TOOLS

	private case object Default
	private val nulls = List(
		JsonNull,
		JsonNull: JsonNumber,
		JsonNull: JsonString,
		JsonNull: JsonBoolean,
		JsonNull: JsonArray[_],
		JsonNull: JsonObject)

	private val nulls2 = nulls.flatMap{n1 => nulls.map{n2 => (n1, n2)}}

	private def forallItemsIn[T](l: List[T])(f: T => Result): Result = l.foldLeft((1 === 1) : Result){(r: Result, n: T) => r and f(n)}

	private def forallNulls        (f: JsonValue => Result): Result = forallItemsIn(nulls)(f)
	private def forallValues       (f: JsonValue => Result): Result = forallItemsIn(sampleValuesA)(f)
	private def forallValsAndNulls (f: JsonValue => Result): Result = forallItemsIn(valsAndNulls)(f)

	private val sampleValuesA = List(
		JsonNumber(3),
		JsonNumber(4),
		JsonString("string"),
		JsonString("other string"),
		JsonBoolean(true),
		JsonBoolean(false),
		J(1, 2, 3),
		J(1, 2, 3, "string"),
		J(name = "John"),
		J(name = "John", age = 32)
	)

	private val sampleValuesAi = sampleValuesA.zipWithIndex

	private val sampleValuesB = List(
		JsonNumber(3),
		JsonNumber(4),
		JsonString("string"),
		JsonString("other string"),
		JsonBoolean(true),
		JsonBoolean(false),
		J(1, 2, 3),
		J(1, 2, 3, "string"),
		J(name = "John"),
		J(name = "John", age = 32)
	)

	private val sampleValuesBi = sampleValuesB.zipWithIndex

	private val sampleValuesAB   = sampleValuesA  zip sampleValuesB
	private val sampleValuesAiBi = sampleValuesAi zip sampleValuesBi

	private val containedValues = List(
		3,
		4,
		"string",
		"other string",
		true,
		false,
		Vector(JsonNumber(1), JsonNumber(2), JsonNumber(3)),
		Vector(JsonNumber(1), JsonNumber(2), JsonNumber(3), JsonString("string")),
		Map("name" -> JsonString("John")),
		Map("name" -> JsonString("John"), "age" -> JsonNumber(32))
	)


	private val stringRepresentationsValues = List(
		"3" :: Nil,
		"4" :: Nil,
		"string" :: Nil,
		"other string" :: Nil,
		"true" :: Nil,
		"false" :: Nil,
		"[1, 2, 3]" :: Nil,
		"[1, 2, 3, string]" :: Nil,
		"{name = John}" :: Nil,
		"{name = John, age = 32}" :: "{age = 32, name = John}" :: Nil
	)

	private val sampleValuesWithContent = sampleValuesA zip containedValues
	private val sampleValuesWithStringRepresentations = sampleValuesA zip stringRepresentationsValues
	private val valsAndNulls = sampleValuesA ++ nulls
}


