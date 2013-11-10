package com.bryghts.kissjson.parser2.tools

/**
 * Created with IntelliJ IDEA.
 * User: dunlord
 * Date: 19/10/2013
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
package object uchar {

	type UChar = Char



	implicit class UCharOps(val c: UChar) extends AnyVal
	{

		def isSpace   = Character.isWhitespace(c)//isSpace.contains(c)
		def isCrl = Character.isISOControl(c)

	}

	object UChar
	{

		val EOF:UChar = 0x00

	}
//
//	private val isSpace = Map(
//		0x00000009 -> 0x00000009,
//		0x0000000A -> 0x0000000A,
//		0x0000000B -> 0x0000000B,
//		0x0000000C -> 0x0000000C,
//		0x0000000D -> 0x0000000D,
//		0x0000001C -> 0x0000001C,
//		0x0000001D -> 0x0000001D,
//		0x0000001E -> 0x0000001E,
//		0x0000001F -> 0x0000001F,
//		0x00000020 -> 0x00000020,
//		0x00001680 -> 0x00001680,
//		0x0000180E -> 0x0000180E,
//		0x00002000 -> 0x00002000,
//		0x00002001 -> 0x00002001,
//		0x00002002 -> 0x00002002,
//		0x00002003 -> 0x00002003,
//		0x00002004 -> 0x00002004,
//		0x00002005 -> 0x00002005,
//		0x00002006 -> 0x00002006,
//		0x00002008 -> 0x00002008,
//		0x00002009 -> 0x00002009,
//		0x0000200A -> 0x0000200A,
//		0x00002028 -> 0x00002028,
//		0x00002029 -> 0x00002029,
//		0x0000205F -> 0x0000205F,
//		0x00003000 -> 0x00003000)

}

