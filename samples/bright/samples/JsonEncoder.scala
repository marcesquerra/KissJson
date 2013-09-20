package bright.samples

import com.bryghts.kissjson._
import com.bryghts.kissnumber.Number
import scala.util.Try
import scala.util.Success
import scala.util.Failure

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


////////////////////////////////////////////////////////////////////////////////
// SOME PLAYGROUND CASE CLASSES
////////////////////////////////////////////////////////////////////////////////


	case class EMail(
			login:        String,
			domain:       String)

	case class Person(
			name:         String,
			middlename:   Option[String],
			surname:      String,
			age:          Int,
			eMails:       Array[EMail])

	case class Employee(
			self:         Person,
			ocupation:    String,
			subordinates: Option[List[Employee]] = None)

	case class Company(
			name:         String,
			emails:       Array[EMail],
			staff:        List[Employee])





////////////////////////////////////////////////////////////////////////////////
// SOME SAMPLE DATA
////////////////////////////////////////////////////////////////////////////////

	val john = Employee(
			self = Person (
					name        = "John",
					middlename  = Some("Hamish"),
					surname     = "Watson",
					age         = 42,
					eMails      = Array(EMail("john", "deducsciencie.com"))),
			ocupation = "Chronicler"
	)

	val sherlock = Employee(
			self = Person (
					name        = "Sherlock",
					middlename  = None,
					surname     = "Holmes",
					age         = 38,
					eMails      = Array(EMail("sherlock", "deducsciencie.com"))),
			ocupation    = "Consultant Detective",
			subordinates = Some(List(john))
	)

	val companyEmails = Array(
			EMail("cases",    "deducsciencie.com"),
			EMail("sherlock", "deducsciencie.com"),
			EMail("john",     "deducsciencie.com"),
			EMail("info",     "deducsciencie.com"),
			EMail("press",    "deducsciencie.com")
	)

	val sd = Company(
			name   = "Science of Deduction Inc.",
			emails = companyEmails,
			staff  = List(john, sherlock)
	)


////////////////////////////////////////////////////////////////////////////////
// Then, we may try to encode all this data into a JsonObject
////////////////////////////////////////////////////////////////////////////////



	val jsonSd: Try[JsonValue] = sd.asJson

	show(jsonSd)



	val jsonCompanyEmails: Try[JsonValue] = companyEmails.asJson

	show(jsonCompanyEmails)






////////////////////////////////////////////////////////////////////////////////
// Some utilities
////////////////////////////////////////////////////////////////////////////////

	def show(v: Try[JsonValue]) = v match {
		case Success(json) =>
			println(json.render)

		case Failure(t)    =>
			println(s"The json could not be created due to '$t'")
	}

}
