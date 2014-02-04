package bryghts.benchmarks

import com.alibaba.fastjson.JSON


class FastJson_Benchmarking extends Benchmarking
{

	protected val parseOther: String => Any = js => JSON.parse(js)

	protected val otherTitle: String = "FastJson"

}
