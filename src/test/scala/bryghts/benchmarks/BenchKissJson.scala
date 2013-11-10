package bryghts.benchmarks

import com.bryghts.kissjson.parser2.Parser
import com.bryghts.kissjson.parser2.tools.StringSource
import org.specs2.SpecificationFeatures
import org.specs2.execute.{Result, AsResult, ResultLike}


trait BenchKissJson extends HasBenchmarks with SpecificationFeatures
{

	private   val parseKissJson: String => Any = js => Parser(new StringSource(js))

	protected val parseOther: String => Any

	private   val kissTitle: String  = "KissJson"
	protected val otherTitle: String

	private   lazy val LENGTH       = otherTitle.length max kissTitle.length
	private   lazy val SPACES_OTHER = " " * (LENGTH - otherTitle.length)
	private   lazy val SPACES_KISSJ = " " * (LENGTH - kissTitle.length)


	def bench(title: String, js: String): Result = {

		val a = run{parseOther(js)}
		val b = run{parseKissJson(js)}


		val ta = a.toString
		val tb = b.toString
		val tl = ta.length max tb.length
		val as = " " * (tl - ta.length)
		val bs = " " * (tl - tb.length)

		println(
			s"""
			  |After running '$title'
			  |$SPACES_OTHER $otherTitle: $as$ta nanos
			  |$SPACES_KISSJ $kissTitle: $bs$tb nanos
			  |
			  |$otherTitle took ${a - b} more nanos than KissJson
			  |$otherTitle took ${"%.2f" format (a.toDouble / b.toDouble)} more times than KissJson
			  |
			  |""".stripMargin)

		b must beLessThan(a)
	}

	def bench(js: String): Result = bench(js, js)
}
