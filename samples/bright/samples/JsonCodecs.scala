package bright.samples

import com.bryghts.kissjson._
import com.bryghts.kissnumber.Number
import scala.reflect.runtime.universe._

object JsonCodecs extends App
{

	case class Company(name: String, email: String, nif: Option[String])
	case class Person(name: String, age: Number, company: Company)

	println(Person("Jhon", 42, Company("Science of Deduction", "jhon@deduction.com", Some("ABC")))
		.toJson
		.render
	)

}
