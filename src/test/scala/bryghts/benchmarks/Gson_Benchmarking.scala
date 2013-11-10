package bryghts.benchmarks

import com.google.gson.JsonParser


class Gson_Benchmarking extends Benchmarking
{

	protected val parseOther: String => Any = js => new JsonParser().parse(js)

	protected val otherTitle: String = "Gson"

}
