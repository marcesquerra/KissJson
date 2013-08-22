package bright.samples

import com.bryghts.kissjson._

object JsonCodecs extends App
{

	case class Company(name: String, email: String)
	case class Person(name: String, age: Int, company: Company)

	println(Person("Jhon", 42, Company("Science of Deduction", "jhon@deduction.com"))
		.toJson
		.render
	)

}
