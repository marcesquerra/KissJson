package bright.samples

import com.bryghts.kissjson._
import com.bryghts.kissnumber.Number
import scala.util.Try
import scala.util.Success
import scala.util.Failure

object JsonDecoder extends App
{

	println("Hello")
//
//
//
// Decoding allows converting Json values into standard Scala classes. The
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

	val jsonSd = J(
			name   = "Science of Deduction Inc.",
			emails = J(
					J(login = "cases",    domain = "deducsciencie.com"),
					J(login = "sherlock", domain = "deducsciencie.com"),
					J(login = "john",     domain = "deducsciencie.com"),
					J(login = "info",     domain = "deducsciencie.com"),
					J(login = "press",    domain = "deducsciencie.com")),
			staff  = J(
					J(
						self      =
							J(
								eMails     =
									J(J(login = "john", domain = "deducsciencie.com")),
								name       = "John",
								middlename = "Hamish",
								surname    = "Watson",
								age        = 42),
						ocupation = "Chronicler"),
					J(
						self      =
							J(
								eMails     =
									J(J(login = "sherlock", domain = "deducsciencie.com")),
								name       = "Sherlock",
								surname    = "Holmes",
								age        = 38),
						ocupation = "Consultant Detective",
						subordinates = J(
								J(
										self = J(
												eMails =
													J(J(login = "john", domain = "deducsciencie.com")),
												name       = "John",
												middlename = "Hamish",
												surname    = "Watson",
												age        = 42),
										ocupation = "Chronicler")))))

//		"""
//
//	val jsonCompanyEmails = """
//		[{"login": "cases", "domain": "deducsciencie.com"}, {"login": "sherlock", "domain": "deducsciencie.com"}, {"login": "john", "domain": "deducsciencie.com"}, {"login": "info", "domain": "deducsciencie.com"}, {"login": "press", "domain": "deducsciencie.com"}]
//	"""


////////////////////////////////////////////////////////////////////////////////
// Then, we may try to encode all this data into a JsonObject
////////////////////////////////////////////////////////////////////////////////



	val sd: Try[Company] = jsonSd.as[Company]

	show(sd)



//	val jsonCompanyEmails: Try[JsonValue] = companyEmails.asJson
//
//	show(jsonCompanyEmails)






////////////////////////////////////////////////////////////////////////////////
// Some utilities
////////////////////////////////////////////////////////////////////////////////

	def show[T](v: Try[T]) = v match {
		case Success(r) =>
			println(r)

		case Failure(t)    =>
			println(s"The json could not be converted to  due to '$t'")
	}

}
