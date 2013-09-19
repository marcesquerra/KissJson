package bright.samples

import com.bryghts.kissjson._
import com.bryghts.kissnumber.Number
import scala.reflect.runtime.universe._

object JsonEncoder extends App
{

//
//
//
// Encoding allows converting standard Scala classes into Json values. The
// following types are supported out of the box:
//
//   * String
//
//   * Byte
//
//   * Char              (as an integer value)
//
//   * Short
//
//   * Int
//
//   * Long
//
//   * Float
//
//   * Double
//
//   * Number            (from KissNumber)
//
//   * IntegerNumber     (from KissNumber)
//
//   * RealNumber        (from KissNumber)
//
//   * Boolean
//
//   * Array[T]          (where T is any other supported type)
//
//   * "Collection"[T]   (where "Collection" is any type that inherits from
//                        Traversable and T is any other supported type)
//
//   * Option[T]         (where T is any other supported type)
//
//   * "CaseClass"       (where "CaseClass" is any case class whose fields are
//                        from a type which is also supported)
//
//
// Also, custom encoders can be created to add more supported types
//
//
//



case class Company(name: String, emails: List[Option[String]], nif: Option[String])
case class Person(name: String, age: Number, company: Company)

	val n: Number = 3

	println(Person("Jhon", 42, Company("Science of Deduction", Some("jhon@deduction.com") :: None :: Some("aaaa@b.c") :: Some("a23@b.c") :: Nil, Some("123")))
		.asJson
		.map(_.render)
		.recover{case t => s"Invalid conversion due to $t"}
		.get
	)

}
