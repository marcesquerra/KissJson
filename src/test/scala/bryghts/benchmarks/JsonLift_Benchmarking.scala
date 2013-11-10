package bryghts.benchmarks

import net.liftweb.json.JsonParser


class JsonLift_Benchmarking extends Benchmarking
{

	protected val parseOther: String => Any = js => JsonParser.parse(js)

	protected val otherTitle: String = "lift-json"

}
