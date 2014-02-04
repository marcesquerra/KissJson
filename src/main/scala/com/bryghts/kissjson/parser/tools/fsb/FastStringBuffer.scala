package com.bryghts.kissjson.parser.tools.fsb

class FastStringBuffer
{

	private var l: Int = 0
	private var b: Array[Char] = CharArrayPool.newArrayChar(256)
	private var limit: Int = 254

	def append(c: Char) {
		if (l >= limit) {
			val s: Int = b.length << 1
			val copy: Array[Char] = CharArrayPool.newArrayChar(s)
			System.arraycopy(b, 0, copy, 0, l)
			CharArrayPool.free(b)
			b = copy
			limit = s - 2
		}
		b(l) = c
		l += 1
	}

	def append(src: Array[Char], srcPos: Int, count: Int) {
		if ((l + count - 1) >= limit) {
			val n: Int = l + count - 1
			var s: Int = b.length << 1
			while (n >= s - 2) s = s << 1
			val copy: Array[Char] = CharArrayPool.newArrayChar(s)
			System.arraycopy(b, 0, copy, 0, l)
			CharArrayPool.free(b)
			b = copy
			limit = s - 2
		}
		System.arraycopy(src, srcPos, b, l, count)
		l += count
	}

	override def toString: String = {
		val r: String = new String(b, 0, l)
		CharArrayPool.free(b)
		r
	}
}

