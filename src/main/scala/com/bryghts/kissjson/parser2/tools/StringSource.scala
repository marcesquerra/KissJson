package com.bryghts.kissjson.parser2.tools

import java.io.StringReader
import com.bryghts.kissjson.parser2.tools.uchar._
import com.bryghts.kissjson.parser2.tools.fsb.FastStringBuffer

class StringSource (src: String) //extends Source
{
	private val len = src.length
	private val l   = (src.length -1)
	private var n   = -1


	var _top : UChar = 0

	_top = if(n >= l) UChar.EOF else {
		n += 1
		src.charAt(n)
	}

	@inline def top:UChar = _top

	@inline def moveNext() : Unit = {
		n += 1
		_top =
			if(n > l)
				UChar.EOF
			else
				src.charAt(n)
	}

	def skipWhitespaces()
	{

		while( n < len) {
			if(!Character.isSpace(src.charAt(n)))
			{
				_top = src.charAt(n)
				return
			}

			n += 1
		}

		_top = UChar.EOF
	}

	def collectChars(bf: FastStringBuffer)
	{

		while( n < len) {

			if(_top == '\"') return
			if(_top == '\\') return

			bf.append(_top)
			n += 1
			_top = src.charAt(n)
		}

		throw new Exception("INVALID JSON")


	}

}

object StringSource
{

	def apply(in: String): StringSource = new StringSource(in)

}
