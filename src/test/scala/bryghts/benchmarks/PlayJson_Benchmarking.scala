package bryghts.benchmarks

import play.api.libs.json.Json


class PlayJson_Benchmarking extends Benchmarking
{

	protected val parseOther: String => Any = js => Json.parse(js)

	protected val otherTitle: String = "PlayJson"

}
