package bright.samples

import com.bryghts.kissjson._
import com.bryghts.kissnumber._
import scala.util.Success
import scala.util.Failure

object JsonDecoder extends App
{

	case class JsonMessage(msg: String, age: Int)(val t: Boolean)
	{
		
	}

	val jsonString = J(
			"msg" := "Hello World",
			"age" := 3,
			"t"   := true)

	jsonString.as[JsonMessage] match
	{
		case Success(msg) =>
			println(msg)
			println(msg.t)

		case Failure(t) =>
	}

	println()
	println()

}

