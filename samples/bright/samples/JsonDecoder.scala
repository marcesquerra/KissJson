package bright.samples

import com.bryghts.kissjson._
import com.bryghts.kissnumber._
import scala.util.Success
import scala.util.Failure

object JsonDecoder extends App
{

	case class JsonMessage(msg: String, age: Int, eMail: Option[String], abc: Array[Option[Array[Double]]])

	val jsonString = J(
			'msg   := "Hello World",
			'age   := 3,
			'eMail := "ABC",
			'abc   := J(J(1, 2), null, J(3)))

	jsonString.as[JsonMessage] match
	{
		case Success(msg) =>
			println(msg)
			println(msg.abc.map{_.getOrElse(Array())}.flatten.mkString(", "))

		case Failure(t) =>
			println(t)
	}

	val a = J(1, null, 3).as[Array[Option[Int]]] match
	{
		case Success(msg) =>
			println(msg.mkString(", "))

		case Failure(t) =>
			println(t)
	}


}


