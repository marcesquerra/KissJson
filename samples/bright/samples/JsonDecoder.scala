package bright.samples

import com.bryghts.kissjson._
import com.bryghts.kissnumber._
import scala.util.Success
import scala.util.Failure
import scala.util.Try


object JsonDecoder extends App
{

	case class JsonMessage(msg: String, age: Int, eMail: Option[String], abc: List[Option[Set[Double]]])

	val jsonString = J(
			msg   = "Hello World",
			age   = 3,
			eMail = "ABC",
			abc   = J(J(1, 2), null, J(3)))

	val r1 = jsonString.as[JsonMessage]
	r1 match
	{
		case Success(msg) =>
			println(msg)
			println(msg.abc.map{_.getOrElse(Set())}.flatten.mkString(", "))

		case Failure(t) =>
			println(t)
	}

	val a = J(1, 2, 3)

	val r: Try[List[Int]] = a.as[List[Int]]
	r match
	{
		case Success(msg) =>
			println(msg.mkString(", "))

		case Failure(t) =>
			println(t)
	}


}


