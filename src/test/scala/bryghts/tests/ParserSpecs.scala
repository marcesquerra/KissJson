package bryghts.tests

import org.specs2.mutable._
import org.specs2.ScalaCheck
import com.bryghts.kissjson._
import com.bryghts.kissjson.parser2.tools.StringSource
import com.bryghts.kissjson.parser2.Parser

class ParserSpecs extends Specification with ScalaCheck
{
	def StringSource(in: String) = new StringSource(in)

//	"Invalid strings" in {
//		Parser(StringSource("abc"))          must throwAn[Exception]
//	}

	"Parse booleans correctly" in {
		Parser(StringSource("false"))        mustEqual JsonBoolean(false)
		Parser(StringSource("true"))         mustEqual JsonBoolean(true)
		Parser(StringSource(" 	false"))     mustEqual JsonBoolean(false)
		Parser(StringSource(" 	true"))      mustEqual JsonBoolean(true)
		Parser(StringSource("false 	"))      mustEqual JsonBoolean(false)
		Parser(StringSource("true 	"))      mustEqual JsonBoolean(true)
		Parser(StringSource(" 	false 	"))  mustEqual JsonBoolean(false)
		Parser(StringSource(" 	true 	"))  mustEqual JsonBoolean(true)

		Parser(StringSource("fals"))         must throwAn[Exception]
		Parser(StringSource("tru"))          must throwAn[Exception]
		Parser(StringSource("true a"))       must throwAn[Exception]
		Parser(StringSource("truea"))        must throwAn[Exception]
		Parser(StringSource("falsea"))       must throwAn[Exception]
		Parser(StringSource("true ,"))       must throwAn[Exception]
		Parser(StringSource("true }"))       must throwAn[Exception]
		Parser(StringSource("true ]"))       must throwAn[Exception]
		Parser(StringSource("t true"))       must throwAn[Exception]
	}

	"Parse null correctly" in {
		Parser(StringSource("null"))         mustEqual JsonNull
		Parser(StringSource(" 	null"))      mustEqual JsonNull
		Parser(StringSource("null 	"))      mustEqual JsonNull
		Parser(StringSource(" 	null 	"))  mustEqual JsonNull

		Parser(StringSource("nul"))          must throwAn[Exception]
		Parser(StringSource("null a"))       must throwAn[Exception]
		Parser(StringSource("nulla"))        must throwAn[Exception]
		Parser(StringSource("null ,"))       must throwAn[Exception]
		Parser(StringSource("null }"))       must throwAn[Exception]
		Parser(StringSource("null ]"))       must throwAn[Exception]
		Parser(StringSource("t null"))       must throwAn[Exception]
	}

	"Parse Strings correctly" in {
		Parser(StringSource(""" "abc" """)) mustEqual JsonString("abc")
		Parser(StringSource(" \"ab\\\"c\\\\b\\/b\\byyy\\fyyy\\nyyy\\ryyy\\tyyy\\u0123yyy\\u4567yyy\\u89AByyy\\uCDEFyyy\" ")) mustEqual JsonString("ab\"c\\b/b\byyy\fyyy\nyyy\ryyy\tyyy\u0123yyy\u4567yyy\u89AByyy\uCDEFyyy")
		Parser(StringSource(" \"ab\\\"c\\\\b\\/b\\byyy\\fyyy\\nyyy\\ryyy\\tyyy\\u0123yyy\\u4567yyy\\u89abyyy\\ucdefyyy\" ")) mustEqual JsonString("ab\"c\\b/b\byyy\fyyy\nyyy\ryyy\tyyy\u0123yyy\u4567yyy\u89AByyy\uCDEFyyy")

		Parser(StringSource(" \"\\uABCG\" ")) must throwAn[Exception]
		Parser(StringSource(" \"\\uABGC\" ")) must throwAn[Exception]
		Parser(StringSource(" \"\\uAGCC\" ")) must throwAn[Exception]
		Parser(StringSource(" \"\\uGBCC\" ")) must throwAn[Exception]
	}

	"Parse Numbers correctly" in {
		Parser(StringSource(""" 0 """)) mustEqual JsonNumber(0)
		Parser(StringSource(""" -0 """)) mustEqual JsonNumber(0)
		Parser(StringSource(""" 123 """)) mustEqual JsonNumber(123)
		Parser(StringSource(""" -123 """)) mustEqual JsonNumber(-123)

		Parser(StringSource(""" 0.0 """)) mustEqual JsonNumber(0)
		Parser(StringSource(""" -0.0 """)) mustEqual JsonNumber(0)
		(Parser(StringSource(""" 123.0 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception(""))).toDouble must beCloseTo (123.0, 0.000000001)
		Parser(StringSource(""" -123.0 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123, 0.000000001)

		Parser(StringSource(""" 0.123 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0.123, 0.000000001)
		Parser(StringSource(""" -0.123 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-0.123, 0.000000001)
		Parser(StringSource(""" 123.123 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123.123, 0.000000001)
		Parser(StringSource(""" -123.123 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123.123, 0.000000001)




		Parser(StringSource(""" 0e12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0e12, 0.000000001e12)
		Parser(StringSource(""" -0e12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0e12, 0.000000001e12)
		Parser(StringSource(""" 123e12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123e12, 0.000000001e12)
		Parser(StringSource(""" -123e12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123e12, 0.000000001e12)

		Parser(StringSource(""" 0.0e12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0e12, 0.000000001e12)
		Parser(StringSource(""" -0.0e12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0e12, 0.000000001e12)
		Parser(StringSource(""" 123.0e12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123e12, 0.000000001e12)
		Parser(StringSource(""" -123.0e12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123e12, 0.000000001e12)

		Parser(StringSource(""" 0.123e12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0.123e12, 0.000000001e12)
		Parser(StringSource(""" -0.123e12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-0.123e12, 0.000000001e12)
		Parser(StringSource(""" 123.123e12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123.123e12, 0.000000001e12)
		Parser(StringSource(""" -123.123e12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123.123e12, 0.000000001e12)




		Parser(StringSource(""" 0e-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0e-12, 0.000000001e-12)
		Parser(StringSource(""" -0e-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0e-12, 0.000000001e-12)
		Parser(StringSource(""" 123e-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123e-12, 0.000000001e-12)
		Parser(StringSource(""" -123e-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123e-12, 0.000000001e-12)

		Parser(StringSource(""" 0.0e-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0e-12, 0.000000001e-12)
		Parser(StringSource(""" -0.0e-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0e-12, 0.000000001e-12)
		Parser(StringSource(""" 123.0e-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123e-12, 0.000000001e-12)
		Parser(StringSource(""" -123.0e-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123e-12, 0.000000001e-12)

		Parser(StringSource(""" 0.123e-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0.123e-12, 0.000000001e-12)
		Parser(StringSource(""" -0.123e-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-0.123e-12, 0.000000001e-12)
		Parser(StringSource(""" 123.123e-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123.123e-12, 0.000000001e-12)
		Parser(StringSource(""" -123.123e-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123.123e-12, 0.000000001e-12)




		Parser(StringSource(""" 0e+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0e+12, 0.000000001e+12)
		Parser(StringSource(""" -0e+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0e+12, 0.000000001e+12)
		Parser(StringSource(""" 123e+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123e+12, 0.000000001e+12)
		Parser(StringSource(""" -123e+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123e+12, 0.000000001e+12)

		Parser(StringSource(""" 0.0e+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0e+12, 0.000000001e+12)
		Parser(StringSource(""" -0.0e+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0e+12, 0.000000001e+12)
		Parser(StringSource(""" 123.0e+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123e+12, 0.000000001e+12)
		Parser(StringSource(""" -123.0e+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123e+12, 0.000000001e+12)

		Parser(StringSource(""" 0.123e+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0.123e+12, 0.000000001e+12)
		Parser(StringSource(""" -0.123e+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-0.123e+12, 0.000000001e+12)
		Parser(StringSource(""" 123.123e+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123.123e+12, 0.000000001e+12)
		Parser(StringSource(""" -123.123e+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123.123e+12, 0.000000001e+12)




		Parser(StringSource(""" 0E12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0E12, 0.000000001e+12)
		Parser(StringSource(""" -0E12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0E12, 0.000000001e+12)
		Parser(StringSource(""" 123E12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123E12, 0.000000001e+12)
		Parser(StringSource(""" -123E12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123E12, 0.000000001e+12)

		Parser(StringSource(""" 0.0E12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0E12, 0.000000001e+12)
		Parser(StringSource(""" -0.0E12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0E12, 0.000000001e+12)
		Parser(StringSource(""" 123.0E12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123E12, 0.000000001e+12)
		Parser(StringSource(""" -123.0E12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123E12, 0.000000001e+12)

		Parser(StringSource(""" 0.123E12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0.123E12, 0.000000001e+12)
		Parser(StringSource(""" -0.123E12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-0.123E12, 0.000000001e+12)
		Parser(StringSource(""" 123.123E12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123.123E12, 0.000000001e+12)
		Parser(StringSource(""" -123.123E12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123.123E12, 0.000000001e+12)




		Parser(StringSource(""" 0E-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0E-12, 0.000000001e-12)
		Parser(StringSource(""" -0E-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0E-12, 0.000000001e-12)
		Parser(StringSource(""" 123E-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123E-12, 0.000000001e-12)
		Parser(StringSource(""" -123E-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123E-12, 0.000000001e-12)

		Parser(StringSource(""" 0.0E-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0E-12, 0.000000001e-12)
		Parser(StringSource(""" -0.0E-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0E-12, 0.000000001e-12)
		Parser(StringSource(""" 123.0E-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123E-12, 0.000000001e-12)
		Parser(StringSource(""" -123.0E-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123E-12, 0.000000001e-12)

		Parser(StringSource(""" 0.123E-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0.123E-12, 0.000000001e-12)
		Parser(StringSource(""" -0.123E-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-0.123E-12, 0.000000001e-12)
		Parser(StringSource(""" 123.123E-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123.123E-12, 0.000000001e-12)
		Parser(StringSource(""" -123.123E-12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123.123E-12, 0.000000001e-12)




		Parser(StringSource(""" 0E+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0E+12, 0.000000001e+12)
		Parser(StringSource(""" -0E+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0E+12, 0.000000001e+12)
		Parser(StringSource(""" 123E+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123E+12, 0.000000001e+12)
		Parser(StringSource(""" -123E+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123E+12, 0.000000001e+12)

		Parser(StringSource(""" 0.0E+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0E+12, 0.000000001e+12)
		Parser(StringSource(""" -0.0E+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0E+12, 0.000000001e+12)
		Parser(StringSource(""" 123.0E+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123E+12, 0.000000001e+12)
		Parser(StringSource(""" -123.0E+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123E+12, 0.000000001e+12)

		Parser(StringSource(""" 0.123E+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (0.123E+12, 0.000000001e+12)
		Parser(StringSource(""" -0.123E+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-0.123E+12, 0.000000001e+12)
		Parser(StringSource(""" 123.123E+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (123.123E+12, 0.000000001e+12)
		Parser(StringSource(""" -123.123E+12 """)).asInstanceOf[JsonNumber].getOrElse(throw new Exception("")).toDouble must beCloseTo (-123.123E+12, 0.000000001e+12)

//		Parser(StringSource(" 00 "))    must throwAn[Exception]
		Parser(StringSource(" +1 "))    must throwAn[Exception]
		Parser(StringSource(" .0 "))    must throwAn[Exception]
		Parser(StringSource(" 12ee1 ")) must throwAn[Exception]
	}

	"Parse Arrays" in {
		Parser(StringSource(""" [] """)) mustEqual J.emptyArray()
		Parser(StringSource(""" [1, 2, 3] """)) mustEqual J(1, 2, 3)
		Parser(StringSource(""" [1, true, "3"] """)) mustEqual J(1, true, "3")
	}

	"Parse Objects" in {
		Parser(StringSource(""" {} """)) mustEqual J.emptyObject()
		Parser(StringSource(""" {"name" : "John", "age" : 32, "emails" : ["a@gmail.com", "b@gmail.com"]} """)) mustEqual J(name = "John", age = 32, emails = J("a@gmail.com", "b@gmail.com"))
	}

	"Parse Sample Values" in {
		val json =
			"""
			  |{
			  |  "glossary": {
			  |    "title": "example glossary",
			  |    "GlossDiv": {
			  |      "title": "S",
			  |      "GlossList": {
			  |        "GlossEntry": {
			  |          "ID": "SGML",
			  |          "SortAs": "SGML",
			  |          "GlossTerm": "Standard Generalized Markup Language",
			  |          "Acronym": "SGML",
			  |          "Abbrev": "ISO 8879:1986",
			  |          "GlossDef": {
			  |            "para": "A meta-markup language, used to create markup languages such as DocBook.",
			  |            "GlossSeeAlso": ["GML", "XML"]
			  |          },
			  |          "GlossSee": "markup"
			  |        }
			  |      }
			  |    }
			  |  }
			  |}
			""".stripMargin
		Parser(StringSource(json)) mustEqual J(
			glossary = J (
				title = "example glossary",
				GlossDiv = J(
					title = "S",
					GlossList = J(
						GlossEntry = J(
							ID = "SGML",
							SortAs = "SGML",
							GlossTerm = "Standard Generalized Markup Language",
							Acronym = "SGML",
							Abbrev = "ISO 8879:1986",
							GlossDef = J(
								para = "A meta-markup language, used to create markup languages such as DocBook.",
								GlossSeeAlso = J("GML", "XML")
							),
							GlossSee = "markup"
						)
					)
				)
			)
		)
	}
}


