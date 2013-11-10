package bryghts.benchmarks

import org.codehaus.jackson._
import org.codehaus.jackson.map._

class Jackson_Benchmarking extends Benchmarking
{

	private val mapper = new ObjectMapper
	protected val parseOther: String => Any = js => mapper.readValue(json, classOf[JsonNode])

	protected val otherTitle: String = "Jackson"

}
