//package com.bryghts.kissjson.parser2.tools.fsb
//
//import com.bryghts.kissjson.parser2.tools.uchar.UChar
//import java.util.Arrays
//
//class FastStringBuffer (initialCapacity: Int)
//{
//	private var l = 0
//	private var buffer: Array[Char] = new Array[Char](if(initialCapacity < 10) 10 else initialCapacity)
//	private var limit = (if(initialCapacity < 10) 10 else initialCapacity) - 2
//
//	def clear() = l = 0
//
//	def += (c: UChar):Unit =
//	{
//
//		if(l >= limit)
//		{
//			val s = buffer.length << 1
//			buffer = Arrays.copyOf(buffer, s)
//			limit = s - 2
//		}
//
//		buffer(l) = c
//		l += 1
//
//	}
//
//	@inline def append (c: UChar):Unit =
//	{
//
//		if(l >= limit)
//		{
//			val s = buffer.length << 1
//			buffer = Arrays.copyOf(buffer, s)
//			limit = s - 2
//		}
//
//		buffer(l) = c
//		l += 1
//
//	}
//
//	private def ensureCapacity()
//	{
//		if(l >= limit)
//		{
//			val s = buffer.length << 1
//			buffer = Arrays.copyOf(buffer, s)
//			limit = s - 2
//		}
//	}
//
//	override def toString(): String = new String(buffer, 0, l)
//}
//
//object FastStringBuffer {
//	def apply(initialCapacity: Int) = new FastStringBuffer(initialCapacity)
//}