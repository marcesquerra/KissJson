package bryghts.benchmarks

import scala.util.parsing.json.JSON


class StandardScalaApiJsonParser_Benchmarking extends Benchmarking
{
	override protected def NUMBER_OF_BENCHMARK_EXECUTIONS:Int = 100

	protected val parseOther: String => Any = js => JSON.parseFull(js)

	protected val otherTitle: String = "Standard Scala"

}
