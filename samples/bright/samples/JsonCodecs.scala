package bright.samples

import com.bryghts.kissjson._
import com.bryghts.kissnumber.Number
import scala.reflect.runtime.universe._

object JsonCodecs extends App
{

	case class Company(name: String, emails: Array[String], nif: Option[String])
	case class Person(name: String, age: Number, company: Company)

	println(Person("Jhon", 42, Company("Science of Deduction", Array("jhon@deduction.com", "a@b.c"), Some("123")))
		.toJson
		.map(_.render)
		.recover{case t => s"Invalid conversion due to $t"}
		.get
	)

}
