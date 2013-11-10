package bryghts.benchmarks


trait HasBenchmarks
{

	protected def NUMBER_OF_BENCHMARK_EXECUTIONS:Int

	private def executeBatch(f: => Any): Long =
	{
		// WARMUP
		var i = 0;
		while (i < NUMBER_OF_BENCHMARK_EXECUTIONS)
		{
			f
			i += 1
		}

		System.gc

		// REAL CHECK
		val a = System.nanoTime()

		i = 0;
		while (i < NUMBER_OF_BENCHMARK_EXECUTIONS)
		{
			f
			i += 1
		}

		(System.nanoTime() - a)// / 1000l
	}

	def run(f: => Any): Long = {
		val t1 = executeBatch(f)
		val t2 = executeBatch(f)
		val t3 = executeBatch(f)
		val t4 = executeBatch(f)
		val t5 = executeBatch(f)
		val t6 = executeBatch(f)
		val t7 = executeBatch(f)
		val t8 = executeBatch(f)
		val t9 = executeBatch(f)

		val t = List(t1, t2, t3, t4, t5, t6, t7, t8, t9).sorted.apply(4)

		t
	}

}
