package com.bryghts.kissjson.parser
package tools

import com.bryghts.kissjson.parser.tools.fsb.FastStringBuffer
import sun.misc.Unsafe


class StringSource (str: String, src: Array[Char]) //extends Source
{

	import StringSource._

	private[parser] val len = src.length
	private val l   = (src.length -1)
	private var n   = -1

//	def this(in: String) = this(in.toCharArray)

	private[parser] var top : Char = 0


	top = if(n >= l) 0x00 else {
		n += 1
		src(n)
	}


	@inline def moveNext() : Unit = {
		n += 1
		top =
			if(n > l)
				0x00
			else
				src(n)
	}

	def skipWhitespaces()
	{

		while( n < len) {
			if(!isSpace(src(n)))
			{
				top = src(n)
				return
			}

			n += 1
		}

		top = 0x00
	}


	private def encodeScape(bf: FastStringBuffer): Unit = {
		bf append {
			//			bf.revert()
			n += 1
			top = src(n)

			if(top > 117) error()
			val escaped = toScape(top)
			if(escaped == 0x00) error()
			if(escaped == 'u')
			{
				n += 1
				top = src(n)
				if(top > 102) error()
				var cp = toHex(top)

				if(cp == 0xFF) error()

				n += 1
				top = src(n)

				if(top > 102) error()
				var tmp = toHex(top)

				if(tmp == 0xFF) error()

				cp = (cp << 4).asInstanceOf[Char]
				cp = (cp | tmp).asInstanceOf[Char]

				n += 1
				top = src(n)
				if(top > 102) error()
				tmp = toHex(top)

				if(tmp == 0xFF) error()

				cp = (cp << 4).asInstanceOf[Char]
				cp = (cp | tmp).asInstanceOf[Char]

				n += 1
				top = src(n)
				if(top > 102) error()
				tmp = toHex(top)

				if(tmp == 0xFF) error()

				cp = (cp << 4).asInstanceOf[Char]
				cp = (cp | tmp).asInstanceOf[Char]

				cp
			}
			else escaped

		}
	}


	@inline def collectChars(): String =
	{
		n += 1
		top = src(n)

		var notFound = top != '\"' && top != '\\'

		var d = n

		while(notFound) {
			d += 1
			top = src(d)
			if(top == '\"') notFound = false
			else if(top == '\\') notFound = false
		}

		if(top == '\"')
		{
			val r = new String(src, n, d - n)
			n = d
			moveNext()
			r
		}
		else
		{
			val bf: FastStringBuffer = new FastStringBuffer

			bf.append(src, n, d - n)

			n = d

			encodeScape(bf)
			n += 1
			top = src(n)


			while(top != '\"')
			{
				if(top == '\\')
				{
					encodeScape(bf)
					n += 1
					top = src(n)
				}
				else
				{
					notFound = true

					var d = n

					while(notFound) {
						d += 1
						top = src(d)
						if(top == '\"') notFound = false
						else if(top == '\\') notFound = false
					}

					bf.append(src, n, d - n)

					n = d
				}
			}

			moveNext()
			bf.toString
		}

	}


	@inline def jumpFalse() {
		n+=1; if(src(n) != 'a') error()
		n+=1; if(src(n) != 'l') error()
		n+=1; if(src(n) != 's') error()
		n+=1; if(src(n) != 'e') error()
		moveNext()
	}

	@inline def jumpTrue() {
		n+=1; if(src(n) != 'r') error()
		n+=1; if(src(n) != 'u') error()
		n+=1; if(src(n) != 'e') error()
		moveNext()
	}

	@inline def jumpNull() {
		n+=1; if(src(n) != 'u') error()
		n+=1; if(src(n) != 'l') error()
		n+=1; if(src(n) != 'l') error()
		moveNext()
	}

	private def error(): Nothing = throw new Exception("Invalid JSON")
}

object StringSource
{


	private val unsafe = scala.concurrent.util.Unsafe.instance

	private val offset = {
		val f = classOf[String].getDeclaredField("value")
		f.setAccessible(true)
		val r = unsafe.objectFieldOffset(f)
		r
	}

	def apply(in: String): StringSource = new StringSource(in, unsafe.getObject(in, offset).asInstanceOf[Array[Char]])


	private val toHex = Array[Char](
		0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
		0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
		0x08, 0x09, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F)


	private val toScape = Array[Char](
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, '\"', 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  '/',
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, '\\', 0x00, 0x00, 0x00,
		0x00, 0x00, '\b', 0x00, 0x00, 0x00, '\f', 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, '\n', 0x00,
		0x00, 0x00, '\r', 0x00, '\t',  'u')

	@inline private def isSpace(ch: Char): Boolean = {
		return (ch <= 0x0020) && (((((1L << 0x0009) | (1L << 0x000A) | (1L << 0x000C) | (1L << 0x000D) | (1L << 0x0020)) >> ch) & 1L) != 0)
	}
}
