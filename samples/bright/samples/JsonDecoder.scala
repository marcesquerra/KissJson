package bright.samples

import com.bryghts.kissjson._
import com.bryghts.kissnumber._
import scala.util.Success
import scala.util.Failure

object JsonDecoder extends App
{

	val jsonString: JsonString = "Hello World"

	jsonString.as[String] match
	{
		case Success(string) =>
			println(string)
		case Failure(t) =>
			println("ERROR")
	}

	val jsonLong: JsonReal = 1234

	jsonLong.as[Long] match
	{
		case Success(l) =>
			println(l)
		case Failure(t) =>
			println("ERROR")
	}
}

