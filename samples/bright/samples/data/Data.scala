package bright.samples.data



case class EMail(
		login:        String,
		domain:       String)                                                   extends ImprovedToString[EMail]

case class Person(
		name:         String,
		middlename:   Option[String],
		surname:      String,
		age:          Int,
		eMails:       Array[EMail])                                             extends ImprovedToString[Person]

case class Employee(
		self:         Person,
		ocupation:    String,
		subordinates: Option[List[Employee]] = None)                            extends ImprovedToString[Employee]

case class Company(
		name:         String,
		emails:       Array[EMail],
		staff:        List[Employee])                                           extends ImprovedToString[Company]



import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.universe.typeOf

abstract class ImprovedToString[T <: Product : TypeTag] extends Product
{

	private def typeName = typeOf[T].typeSymbol.name.decoded

	private val stringize: Any => String = {
		case a: Array[_]       => a.map(stringize).mkString("[", ", ", "]")
		case t: Traversable[_] => t.mkString("[", ", ", "]")
		case s: String         => s""""$s""""
		case b                 => b.toString
	}

	override def toString() =
		productIterator
			.map(stringize)
			.mkString(typeName + "(", ", ", ")")

}
