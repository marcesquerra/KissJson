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

//	"Other Json" in
//	{
//		val json =
//			"""
//			  { "Asks":[  ], "Bids":[ [ Market, 1.00000000 ], [ 300, 19.95530083 ], [ 275, 1.00000000 ], [ 254, 8.98214390 ], [ 250, 13.22308520 ], [ 249, 1.84569370 ], [ 248.5, 20.00000000 ], [ 248, 3.00000000 ], [ 245, 10.00000000 ], [ 240, 8.30000000 ], [ 235, 5.00000000 ], [ 230, 3.68053784 ], [ 225, 10.00000000 ], [ 220, 2.00000000 ], [ 214, 10.00000000 ], [ 211, 5.00000000 ], [ 200, 1.24863251 ], [ 160, 55.94231725 ], [ 129, 0.01000000 ], [ 110.01, 91.28515746 ], [ 110, 26.29271574 ], [ 109, 18.24825842 ], [ 98, 2.24930951 ], [ 97, 1.60091670 ], [ 95, 1.00000000 ], [ 22, 4.52059128 ], [ 11, 151.00000000 ], [ 1, 1.00000000 ] ] }
//			"""
//
//		Parser(StringSource(json))
//
//		true must beTrue
//	}

	"Other Json2" in
	{
		val json =
			"""
			  { "Asks":[ [ 287.47, 0.21896201 ], [ 287.48, 2.88031132 ], [ 287.5, 4.98990000 ], [ 288, 0.33171429 ], [ 288.5, 0.20000000 ], [ 289, 2.03000000 ], [ 289.9, 1.50000000 ], [ 290, 3.99336477 ], [ 299, 0.02873260 ], [ 299.99, 9.01475000 ], [ 300, 28.53450466 ], [ 301.82, 0.36343155 ], [ 304.5, 2.52240651 ], [ 305, 1.00000000 ], [ 308, 1.07754589 ], [ 309.98, 0.01500000 ], [ 310, 0.99839024 ], [ 314.98, 0.01500000 ], [ 315, 11.00000000 ], [ 320, 1.80000000 ], [ 321, 1.11000000 ], [ 324.29, 0.31548969 ], [ 325, 12.28700000 ], [ 325.99, 10.00000000 ], [ 328, 0.06120184 ], [ 330, 1.06082752 ], [ 332.99, 5.50000000 ], [ 333, 0.06027454 ], [ 334.88, 5.00000000 ], [ 335, 1.30991144 ], [ 337, 0.05955269 ], [ 339.88, 10.00000000 ], [ 340, 1.35919821 ], [ 343, 2.55884792 ], [ 344.88, 10.00000000 ], [ 345, 4.98610578 ], [ 348.1, 10.00000000 ], [ 348.74, 23.00000000 ], [ 349, 0.18545300 ], [ 349.88, 10.00000000 ], [ 349.95, 0.30000000 ], [ 350, 108.67082472 ], [ 351.15, 1.00000000 ], [ 352, 5.92984170 ], [ 354.88, 10.00000000 ], [ 354.9, 0.01000000 ], [ 355, 0.36536081 ], [ 355.26, 1.00000000 ], [ 360, 2.50000000 ], [ 360.59, 1.00000000 ], [ 361, 0.50000000 ], [ 363.03, 3.00000000 ], [ 364.23, 4.00000000 ], [ 364.99, 13.74309720 ], [ 365, 0.25000000 ], [ 365.01, 5.00000000 ], [ 367.1, 0.01000000 ], [ 370, 0.25000000 ], [ 370.59, 1.00000000 ], [ 375, 1.96000000 ], [ 376, 4.69000000 ], [ 376.88, 1.32000000 ], [ 378.8, 0.50000000 ], [ 380, 0.25000000 ], [ 380.14, 1.00000000 ], [ 385, 1.67000000 ], [ 389.1, 10.00000000 ], [ 389.98, 1.00000000 ], [ 390, 0.46047333 ], [ 391, 0.24830139 ], [ 399.95, 1.00000000 ], [ 399.99, 13.00000000 ], [ 400, 17.35180000 ], [ 405, 10.00000000 ], [ 419.6, 0.50000000 ], [ 420, 1.00000000 ], [ 425, 1.00000000 ], [ 448.8, 0.50000000 ], [ 450, 1.28000000 ], [ 460, 0.67900000 ], [ 495, 0.04000000 ], [ 499.75, 0.99990000 ], [ 500, 141.39461661 ], [ 540, 0.30000000 ], [ 550, 0.09990000 ], [ 749, 0.03000000 ], [ 999.98, 11.00000000 ], [ 999.99, 6.00000000 ], [ 1000, 8.88000000 ], [ 1000.01, 6.00000000 ], [ 1000.02, 6.00000000 ], [ 1100, 1.25000000 ], [ 1110, 0.02000000 ], [ 1111.1, 1.00000000 ] ], "Bids":[ [ 281.01, 0.66700000 ], [ 281, 1.00000000 ], [ 280.01, 0.51400000 ], [ 280, 0.77000000 ], [ 274.01, 5.05700000 ], [ 274, 20.00000000 ], [ 273.51, 0.46000000 ], [ 273.5, 100.00000000 ], [ 272.75, 0.15632256 ], [ 270.49, 1.02195879 ], [ 270.03, 4.41098887 ], [ 270, 0.45000000 ], [ 265, 1.32658115 ], [ 262.22, 0.44821142 ], [ 260, 9.39259285 ], [ 259.51, 0.05244000 ], [ 259, 0.12741313 ], [ 258, 0.03000000 ], [ 254.12, 0.33265801 ], [ 253.25, 0.05374000 ], [ 252, 0.13095238 ], [ 251.28, 0.01031000 ], [ 251.25, 1.00000000 ], [ 251.15, 0.07262464 ], [ 251.11, 4.00000000 ], [ 251, 9.96208992 ], [ 250.65, 1.00000000 ], [ 250.27, 1.00000000 ], [ 250, 10.14658327 ], [ 249.5, 0.10000000 ], [ 247.18, 0.05506000 ], [ 247, 0.40500000 ], [ 246.75, 1.31874487 ], [ 246.7, 1.13000000 ], [ 245.5, 0.10000000 ], [ 245.01, 1.00000000 ], [ 245, 0.15499040 ], [ 244.3, 0.50000000 ], [ 244.01, 0.50000000 ], [ 241.5, 0.10000000 ], [ 241.31, 0.05640000 ], [ 241, 5.00000000 ], [ 240, 11.02071937 ], [ 238.5, 0.10000000 ], [ 238, 0.13865546 ], [ 236.75, 0.31505704 ], [ 236.72, 1.00000000 ], [ 235.62, 0.05776000 ], [ 235.5, 0.10000000 ], [ 235.1, 0.01000000 ], [ 235, 7.00320310 ], [ 234, 1.31860238 ], [ 232.5, 0.10000000 ], [ 231, 0.14285714 ], [ 230.11, 0.05127000 ], [ 230, 4.02068037 ], [ 229.5, 0.10000000 ], [ 226.5, 0.10000000 ], [ 225, 4.99523176 ], [ 224, 0.14732143 ], [ 223.5, 0.10000000 ], [ 223.49, 0.05279000 ], [ 222, 1.70000000 ], [ 221.25, 2.63751625 ], [ 220.2, 0.53606164 ], [ 220, 2.07426311 ], [ 217.89, 0.05415000 ], [ 217, 5.15207373 ], [ 216.5, 1.10000000 ], [ 216, 2.27333001 ], [ 215, 0.02312860 ], [ 213.3, 0.50000000 ], [ 213, 0.01000000 ], [ 212.48, 0.05553000 ], [ 210.75, 2.52414802 ], [ 210, 5.17896027 ], [ 207.24, 0.05693000 ], [ 207, 3.12719654 ], [ 206.58, 3.99583681 ], [ 205, 1.01000000 ], [ 204.5, 1.05000000 ], [ 203, 0.16256158 ], [ 202.17, 0.05836000 ], [ 202, 0.99453007 ], [ 200.01, 0.49932858 ], [ 200, 9.39906016 ], [ 199.98, 3.15252932 ], [ 199.5, 3.99806078 ], [ 198.08, 0.06140499 ], [ 198, 3.00000000 ], [ 197.25, 0.05059000 ], [ 196, 0.16836735 ], [ 195, 3.07500000 ], [ 194, 0.20000000 ], [ 192.72, 4.00015563 ], [ 192.64, 1.24378211 ], [ 192.36, 6.01000000 ], [ 192.25, 0.50000000 ], [ 192, 1.55000000 ], [ 191.55, 9.24666357 ], [ 191.3, 1.83080000 ], [ 191, 4.66049293 ], [ 190.01, 1.25026788 ], [ 190, 2.27000000 ], [ 189.51, 4.00000000 ], [ 189, 1.11630212 ], [ 188.66, 0.51650618 ], [ 188.53, 12.65152201 ], [ 188.5, 0.50000000 ], [ 186, 5.34426246 ], [ 185.01, 0.50000000 ], [ 185, 6.41583828 ], [ 183, 3.75166094 ], [ 182.32, 0.56789000 ], [ 182.2, 0.50000000 ], [ 182, 0.18131868 ], [ 180, 7.76808053 ], [ 179, 0.70561631 ], [ 175, 9.69649331 ], [ 173.5, 1.18000000 ], [ 170, 14.56718049 ], [ 168.5, 3.00000000 ], [ 167, 0.18699547 ], [ 166.25, 0.59821357 ], [ 165, 5.61245422 ], [ 162.02, 1.00000000 ], [ 162, 0.60000000 ], [ 161, 2.22997117 ], [ 160, 7.31700000 ], [ 159, 1.25098123 ], [ 158.5, 6.35000000 ], [ 158, 11.22698575 ], [ 155, 15.39917548 ], [ 153.3, 0.50000000 ], [ 152.02, 1.00000000 ], [ 151.23, 0.50000000 ], [ 150.01, 0.33000000 ], [ 150, 32.21273492 ], [ 148, 0.40318786 ], [ 145, 1.00000000 ], [ 142.65, 5.00000000 ], [ 142.02, 0.46232133 ], [ 142, 2.14111989 ], [ 140.98, 0.05000000 ], [ 140.65, 5.64219932 ], [ 140, 48.60577820 ], [ 138, 5.00000000 ], [ 135, 1.47148092 ], [ 130, 10.75516198 ], [ 129.99, 0.33400000 ], [ 128.4, 0.50000000 ], [ 127, 3.30000000 ], [ 125.96, 0.67000000 ], [ 125, 20.50812527 ], [ 124.41, 0.12214789 ], [ 124, 8.82244429 ], [ 122.01, 0.47366726 ], [ 122, 0.27308817 ], [ 120.1, 3.00000000 ], [ 120.01, 26.62491534 ], [ 120, 16.71712161 ], [ 119.78, 0.41156000 ], [ 118, 5.37000000 ], [ 115.96, 1.46743788 ], [ 115, 14.15347760 ], [ 112, 1.00000000 ], [ 110, 24.10000000 ], [ 109.75, 1.24500000 ], [ 108, 1.00000000 ], [ 105.07, 2.00000000 ], [ 105, 5.34204730 ], [ 103.25, 0.44000000 ], [ 103.03, 1.16123428 ], [ 101, 0.10000000 ], [ 100.03, 0.45744605 ], [ 100.01, 2.98886127 ], [ 100, 56.67277268 ], [ 99.75, 10.00000000 ], [ 99.71, 0.37190000 ], [ 99.45, 1.00000000 ], [ 99.05, 2.00000000 ], [ 99, 10.00000000 ], [ 98.7, 10.07377357 ], [ 98, 5.00000000 ], [ 97.79, 0.13179000 ], [ 97, 12.49991028 ], [ 95.03, 1.00000000 ], [ 95, 1.86999920 ], [ 94, 4.57885882 ], [ 93.14, 1.81830000 ], [ 92.5, 3.42096845 ], [ 92, 1.16846474 ], [ 91.25, 4.00000000 ], [ 90.2, 22.00000000 ], [ 90.03, 1.00000000 ], [ 90.01, 1.00000000 ], [ 90, 9.77675559 ], [ 88, 17.19008181 ], [ 85.03, 1.00000000 ], [ 85, 10.67391111 ], [ 83, 0.10000000 ], [ 80.03, 1.00000000 ], [ 80.01, 0.55003069 ], [ 80, 11.40790650 ], [ 76, 4.00000000 ], [ 75.62, 0.03000000 ], [ 75.55, 6.58206468 ], [ 75.5, 3.00000000 ], [ 75.03, 1.00000000 ], [ 75, 15.35866566 ], [ 72, 19.90441460 ], [ 71.5, 1.16000000 ], [ 70.03, 0.56053265 ], [ 70, 3.40000000 ], [ 68.5, 6.50000000 ], [ 65.01, 67.00000000 ], [ 65, 15.98906016 ], [ 60.13, 0.30333745 ], [ 60, 23.10160781 ], [ 59.42, 1.00005338 ], [ 57, 2.00000000 ], [ 51.5, 1.14000000 ], [ 50, 18.05000000 ], [ 49.05, 30.00000000 ], [ 47.33, 0.05000000 ], [ 42.18, 0.07000000 ], [ 42.07, 5.00000000 ], [ 41.83, 2.00000000 ], [ 40, 0.10000000 ], [ 37.7, 2.25000000 ], [ 35.06, 0.08000000 ], [ 27, 128.92056650 ], [ 26.6, 1.22000000 ], [ 21, 0.20000000 ], [ 18.25, 0.82182000 ], [ 18.2, 163.93353041 ], [ 14, 0.10000000 ], [ 12.5, 238.68722028 ], [ 10, 0.05000000 ], [ 9, 0.07000000 ], [ 7.92, 0.02648000 ], [ 2.67, 0.02000000 ], [ 2.5, 0.01000000 ], [ 1, 36.05000000 ], [ 0.5, 0.01000000 ], [ 0.2, 1.00000000 ], [ 0.16, 0.01000000 ], [ 0.12, 931.55344934 ] ] }
			"""

		Parser(StringSource(json))

		true must beTrue
	}
}


