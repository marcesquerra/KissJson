package bright.samples

import com.bryghts.kissjson._
import com.bryghts.kissnumber._
import scala.util.Success
import scala.util.Failure

object JsonDecoder extends App
{

	case class JsonMessage(msg: String, age: Int, eMail: Option[String], abc: Array[Double])

	val jsonString = J(
			"msg"   := "Hello World",
			"age"   := 3,
			"eMail" := "ABC",
			"abc"   := J(1, 2, 3))

	jsonString.as[JsonMessage] match
	{
		case Success(msg) =>
			println(msg)

		case Failure(t) =>
			println(t)
	}

	println()
	println()

}


