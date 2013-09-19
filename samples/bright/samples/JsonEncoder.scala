package bright.samples

import com.bryghts.kissjson._
import com.bryghts.kissnumber.Number
import scala.reflect.runtime.universe._

object JsonCodecs extends App
{

	case class Company(name: String, emails: List[Option[String]], nif: Option[String])
	case class Person(name: String, age: Number, company: Company)

	println(Person("Jhon", 42, Company("Science of Deduction", Some("jhon@deduction.com") :: None :: Some("aaaa@b.c") :: Some("a23@b.c") :: Nil, Some("123")))
		.asJson
		.map(_.render)
		.recover{case t => s"Invalid conversion due to $t"}
		.get
	)

}
