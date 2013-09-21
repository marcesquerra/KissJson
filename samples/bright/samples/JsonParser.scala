package bright.samples

import com.bryghts.kissjson._
import utils._

object JsonParser extends App
{

	// A JSON OBJECT

	val jsonObjectString =
	"""
		{
			"name": "John",
			"surname": "Whatson"
		}
	"""

	val jsonObject =
			jsonObjectString
			.asJson

	show(jsonObject)



	// A JSON ARRAY

	val jsonArrayString = """
		["John",  "Whatson"]
	"""

	val jsonArray =
			jsonArrayString
			.asJson

	show(jsonArray)




	// A JSON Boolean

	val jsonBooleanString = "true"

	val jsonBoolean =
			jsonBooleanString
			.asJson

	show(jsonBoolean)

}

