package bryghts.benchmarks

import org.codehaus.jackson._
import org.codehaus.jackson.map._
import net.minidev.json.JSONValue

class JsonSmart_Benchmarking extends Benchmarking
{

	protected val parseOther: String => Any = js => JSONValue.parse(js)

	protected val otherTitle: String = "JsonSmart"

}
