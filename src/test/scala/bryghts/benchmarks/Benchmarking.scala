package bryghts.benchmarks

import org.specs2.mutable._
import org.specs2.ScalaCheck

abstract class Benchmarking extends Specification with ScalaCheck with BenchKissJson
{
	protected def NUMBER_OF_BENCHMARK_EXECUTIONS:Int = 500000

	""" benchmark "true" """        in bench("true")
	""" benchmark "false" """       in bench("false")
	""" benchmark "null" """        in bench("null")
	""" benchamrk "1234" """        in bench("1234")
	""" benchamrk "1234.567" """    in bench("1234.567")
	""" benchamrk "1234.567e34" """ in bench("1234.567e34")
	""" benchamrk array 1 """       in bench("array 1", arr1)
	""" benchamrk array 2 """       in bench("array 2", arr2)
	""" benchamrk object """        in bench("object", obj)
	""" benchamrk sample json """   in bench("Sample JSON", json)
	""" benchamrk long string """   in bench("Long String", longString)
	""" benchamrk short string """  in bench("Short String", shortString)


	val json = """
{
  "glossary": {
    "title": "example glossary",
    "GlossDiv": {
      "title": "S",
      "GlossList": {
        "GlossEntry": {
          "ID": "SGML",
          "SortAs": "SGML",
          "GlossTerm": "Standard Generalized Markup Language",
          "Acronym": "SGML",
          "Abbrev": "ISO 8879:1986",
          "GlossDef": {
            "para": "A meta-markup language, used to create markup languages such as DocBook.",
            "GlossSeeAlso": ["GML", "XML"]
          },
          "GlossSee": "markup"
        }
      }
    }
  },
  "glossary": {
    "title": "example glossary",
    "GlossDiv": {
      "title": "S",
      "GlossList": {
        "GlossEntry": {
          "ID": "SGML",
          "SortAs": "SGML",
          "GlossTerm": "Standard Generalized Markup Language",
          "Acronym": "SGML",
          "Abbrev": "ISO 8879:1986",
          "GlossDef": {
            "para": "A meta-markup language, used to create markup languages such as DocBook.",
            "GlossSeeAlso": ["GML", "XML"]
          },
          "GlossSee": "markup"
        }
      }
    }
  },
  "glossary": {
    "title": "example glossary",
    "GlossDiv": {
      "title": "S",
      "GlossList": {
        "GlossEntry": {
          "ID": "SGML",
          "SortAs": "SGML",
          "GlossTerm": "Standard Generalized Markup Language",
          "Acronym": "SGML",
          "Abbrev": "ISO 8879:1986",
          "GlossDef": {
            "para": "A meta-markup language, used to create markup languages such as DocBook.",
            "GlossSeeAlso": ["GML", "XML"]
          },
          "GlossSee": "markup"
        }
      }
    }
  },
  "glossary": {
    "title": "example glossary",
    "GlossDiv": {
      "title": "S",
      "GlossList": {
        "GlossEntry": {
          "ID": "SGML",
          "SortAs": "SGML",
          "GlossTerm": "Standard Generalized Markup Language",
          "Acronym": "SGML",
          "Abbrev": "ISO 8879:1986",
          "GlossDef": {
            "para": "A meta-markup language, used to create markup languages such as DocBook.",
            "GlossSeeAlso": ["GML", "XML"]
          },
          "GlossSee": "markup"
        }
      }
    }
  }
}
			   """

		val arr1 =
			"""
			  ["tijuana",
"tijuana",		   "tijuana",


"tijuana"]
			"""

		val arr2 =
			"""
			  [1234.567e34,
1234.567e34,		   1234.567e34,


1234.567e34]
			"""

		val obj =
			"""
			  {"a": 1234.567e34,
			  "b": 1234.567e34,		   "c": 1234.567e34,


			  "d": 1234.567e34}
			"""

val longString = "\"En un lugar de la mancha, de cuyo nombre no quiero acordarme, no hace mucho timpo vivia un joven idalgo llamado don quijote de la mancha. Era un tipo lector, aficionado a las novelas de caballerias i otras sandezes por el estilo\""
val shortString = "\"En un lugar de la mancha, de cuyo nombre no quiero acordarme, no hace mucho timpo vivia un joven idalgo llamado don quijote de la mancha.\""
}






