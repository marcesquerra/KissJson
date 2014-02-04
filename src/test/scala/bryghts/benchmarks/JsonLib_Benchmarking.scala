package bryghts.benchmarks

import net.sf.json.JSONSerializer


class JsonLib_Benchmarking extends Benchmarking
{

	protected val parseOther: String => Any = js => JSONSerializer.toJSON(js.trim)

	protected val otherTitle: String = "JsonLib"

}
