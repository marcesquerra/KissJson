package bright.samples

import com.bryghts.kissjson._
import scala.util.Random
import utils._

object JsonUsage extends App
{



//
// A JsonValue acts as an Option that holds the native value
//

	def isPair(in: Int) = in % 2 == 0

	val pair: JsonInteger = {
		val r = Random.nextInt

		if(isPair(r)) r else JsonNull
	}

	// You can access it's content with pattern matching
	pair match
	{
		case JsonInteger(i) => println(s"It's the pair number $i")
		case JsonNull       => println("It's not a pair number")
	}

	// or with a .getOrElse method
	println("The pair holds: " + pair.getOrElse(-1))




////////////////////////////////////////////////////////////////////////////////



//
// A JsonObject allows to access to dynamic fields in a safer way with the ".?."
// operator.
//

	val json = J(
			name    = "John",
			surname = "Whatson"
	)



	// you can access it's existing fields safely

	println("The character name is: " + json.?.name)



	// and if you ask for an invalid field, you get a JsonNull

	println("The character age is: " + json.?.age)




////////////////////////////////////////////////////////////////////////////////


//
// Using a JsonArray allows all the standard operations
//


	val array = J("John", "Sherlock", "James")


	// you can perform an standard array access

	println(array(1))



	// and all the Scala collections methods

	val lengths = array
		.map{case JsonString(s) => s.length()}
		.mkString(", ")

	println("All the name lengths: " + lengths)


}

