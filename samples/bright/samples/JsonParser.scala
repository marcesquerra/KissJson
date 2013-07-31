package bright.samples

import com.bryghts.kissjson._

object JsonParser extends App
{

	// A JSON OBJECT

	val jsonObjectString = """
	{
		"name": "John",
		"surname": "Whatson"
	}
	"""

	val jsonObject =
			jsonObjectString
			.asJson
			.getOrElse(throw new Exception("Invalid Json"))
			.asObject
			.getOrElse(throw new Exception("This Json is not an Object"))

	// The Json Values are converted to their json representation
	println(jsonObject.name + " " + jsonObject.surname)




	// A JSON ARRAY

	val jsonArrayString = """
		["John",  "Whatson"]
	"""

	val jsonArray =
			jsonArrayString
			.asJson
			.getOrElse(throw new Exception("Invalid Json"))
			.asArray
			.getOrElse(throw new Exception("This Json is not an Object"))

	// The Json Values are converted to their json representation
	println(jsonArray(0) + " " + jsonArray(1))

}