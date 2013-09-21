package bright.samples

import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.universe.typeOf
import scala.util.Try
import scala.util.Success
import scala.util.Failure

import com.bryghts.kissjson._

package object utils
{





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




	def show(v: Try[_]) = v match
	{
		case Success(r) if r.isInstanceOf[JsonValue] =>
			println(r.asInstanceOf[JsonValue].render)

		case Success(r) if r.isInstanceOf[Array[_]]=>
			println(r.asInstanceOf[Array[_]].mkString("[", ", ", "]"))

		case Success(r) =>
			println(r)

		case Failure(t)    =>
			println(s"The json could not be converted to  due to '$t'")
	}

}