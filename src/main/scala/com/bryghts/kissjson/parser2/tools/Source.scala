package com.bryghts.kissjson.parser2.tools



trait Source
{

	def top        : Int
	def d1         : Int
	def d2         : Int
	def moveNext() : Unit

}



object Source
{
	val EOF = -1
}



