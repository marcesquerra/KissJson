package com.bryghts.kissjson.parser2.tools

import java.io.Reader


class ReaderSource(val src: Reader) extends Source
{

	private var _top : Int = 0
	private var _d1  : Int = 0
	private var _d2  : Int = 0

	_top = src.read()
	_d1  = src.read()
	_d2  = src.read()

	def top        : Int = _top
	def d1         : Int = _d1
	def d2         : Int = _d2

	def moveNext() : Unit = {
		_top = _d1
		_d1  = _d2
		_d2  = src.read()
	}


}
