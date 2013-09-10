package bright.samples

import com.bryghts.kissjson._
import com.bryghts.kissnumber._
import scala.util.Success
import scala.util.Failure

object JsonDecoder extends App
{

	case class JsonMessage(msg: String)

	val jsonString = J(
			"msg" := "Hello World")

	println(jsonString.as[JsonMessage])

}

