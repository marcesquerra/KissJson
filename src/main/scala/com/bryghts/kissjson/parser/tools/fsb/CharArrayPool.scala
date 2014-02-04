package com.bryghts.kissjson.parser.tools.fsb

import scala.collection.mutable.Queue

object CharArrayPool
{

	private val pools: Array[CharArrayPool.SingleSizePool] = {
		val r = new Array[CharArrayPool.SingleSizePool](31)

		var size = 1

		for(i <- 0 to 30){
			r(i) = new SingleSizePool(size)
			size = size << 1
		}

		r
	}


	def newArrayChar(size: Int): Array[Char] =
		pools(log2(size)).pull

	def free(old: Array[Char]): Unit =
		pools(log2(old.length)).push(old)


	private def log2(bits: Int): Int =
		if (bits == 0) 0
		else 31 - Integer.numberOfLeadingZeros(bits)


	private class SingleSizePool(private val size: Int) {

		def pull: Array[Char] = synchronized {
			if (pool.isEmpty) new Array[Char](size)
			else pool.dequeue
		}

		def push(in: Array[Char]): Unit = synchronized {
			pool.enqueue(in)
		}

		private val pool = new Queue[Array[Char]]
	}

}


