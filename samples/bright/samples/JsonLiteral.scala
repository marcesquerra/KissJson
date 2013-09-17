package bright.samples

import com.bryghts.kissjson._

object JsonLiteral extends App
{

	// A Json Object
	val json =
	J(
			`type`    = "Sample Json Object",              //  with a  String    field
			version   = 1.0,                               //       a  Numeric   field
			content   = "characters",                      //       a  String    field
			enabled   = true,                              //       a  Boolean   field
			items     =                                    //   and a  Subobject field
			J(
					characters = J("Spock", "Luck")        //  with an Array     field in it
			)
	)

//
// This JSON is equivalent to:
//	{
//		"type": "Sample Json Object",
//		"version": 1.0,
//		"content": "characters",
//		"enabled": true,
//		"items":
//		{
//			"characters": ["Spock", "Luck"]
//		}
//	}
//

	println(s"The recieved Json is: $json")
	println(s"And rendered in json format: ${json.render}")
	println(json .?. items .?. characters(1))
}