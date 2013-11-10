package com.bryghts.kissjson
package parser2

import tools.StringSource
import tools.uchar._

import com.bryghts.kissjson.parser2.tools.fsb.FastStringBuffer
import com.bryghts.kissnumber.Number
import scala.collection.immutable

object Parser
{

	def apply(in: StringSource): JsonValue =
	{

		in.skipWhitespaces()

		val r = valueParsers(in.top.toInt).apply(in, null)

		in.skipWhitespaces()

		if(in.top != UChar.EOF) error()
		else r
	}


	def doParseString(in: StringSource, bf: FastStringBuffer): JsonValue = new JsonString(parseString(in, bf))

	def err(in: StringSource, bf: FastStringBuffer): Nothing = error()

	private def parseArray(in: StringSource, ibf: FastStringBuffer): JsonArray[_] = {
		val bf = if(ibf == null) new FastStringBuffer(INTIAL_BUFFER) else ibf
		var r = Vector.newBuilder[JsonValue]

		in.moveNext()

		while(in.top != ']') {
			in.skipWhitespaces()
			r += valueParsers(in.top)(in, bf)
			in.skipWhitespaces()
			if(in.top == ',') in.moveNext()
			else if(in.top != ']') error()
		}

		in.moveNext()

		new JsonArray(r.result())(_ => JsonNull)
	}

	private def parseObject(in: StringSource, ibf: FastStringBuffer): JsonObject =
	{
		val bf = if(ibf == null) new FastStringBuffer(INTIAL_BUFFER) else ibf
		val r = immutable.TreeMap.newBuilder[String, JsonValue]

		in.moveNext()

		while(in.top != '}') {
			in.skipWhitespaces()

			if(in.top != '"') error()

			val k = parseString(in, bf)

			in.skipWhitespaces()

			if(in.top != ':') error()
			in.moveNext()

			in.skipWhitespaces()

			r += new Pair(k, valueParsers(in.top)(in, bf))

			in.skipWhitespaces()
			if(in.top == ',') in.moveNext()
			else if(in.top != '}') error()
		}

		in.moveNext()

		new JsonObject(r.result())
	}

	private def parseBoolean(in: StringSource, bf: FastStringBuffer): JsonBoolean =

		if(in.top == 't')
		{

			in.moveNext()
			if(in.top != 'r') error()

			in.moveNext()
			if(in.top != 'u') error()

			in.moveNext()
			if(in.top != 'e') error()

			in.moveNext()

			new JsonBoolean(true)
		}
		else
		{

			in.moveNext()
			if(in.top != 'a') error()

			in.moveNext()
			if(in.top != 'l') error()

			in.moveNext()
			if(in.top != 's') error()

			in.moveNext()
			if(in.top != 'e') error()

			in.moveNext()

			new JsonBoolean(false)
		}

	private def parseNull(in: StringSource, bf: FastStringBuffer): JsonNull =
	{
		in.moveNext()
		if(in.top != 'u') error()

		in.moveNext()
		if(in.top != 'l') error()

		in.moveNext()
		if(in.top != 'l') error()

		in.moveNext()

		JsonNull
	}

	private val INTIAL_BUFFER = 256

	def encodeScape(in: StringSource, bf: FastStringBuffer): Unit = {
		bf append {
//			bf.revert()
			in.moveNext()

			if(in.top > 117) error()
			val escaped: UChar = toScape(in.top)
			if(escaped == 0x00) error()
			if(escaped == 'u')
			{
				in.moveNext()
				if(in.top > 102) error()
				var cp: UChar = toHex(in.top)

				if(cp == 0xFF) error()

				in.moveNext()

				if(in.top > 102) error()
				var tmp = toHex(in.top)

				if(tmp == 0xFF) error()

				cp = (cp << 4).asInstanceOf[UChar]
				cp = (cp | tmp).asInstanceOf[UChar]

				in.moveNext()
				if(in.top > 102) error()
				tmp = toHex(in.top)

				if(tmp == 0xFF) error()

				cp = (cp << 4).asInstanceOf[UChar]
				cp = (cp | tmp).asInstanceOf[UChar]

				in.moveNext()
				if(in.top > 102) error()
				tmp = toHex(in.top)

				if(tmp == 0xFF) error()

				cp = (cp << 4).asInstanceOf[UChar]
				cp = (cp | tmp).asInstanceOf[UChar]

				cp
			}
			else escaped

		}
	}

	private def parseString(in: StringSource, ibf: FastStringBuffer): String = {

		val bf = if(ibf == null) new FastStringBuffer(INTIAL_BUFFER) else{
			ibf.clear()
			ibf
		}

		in.moveNext()

		while(in.top != '"')
		{
//			if(in.top == UChar.EOF)
//				error()

			if(in.top == '\\')
			{
				encodeScape(in, bf)
				in.moveNext()
			}
			else in.collectChars(bf)
		}

		in.moveNext()

		bf.toString
	}

	private val LOG2 : Double = Math.log10(2.0)
	private val POSITIVE: Long = 0x0000000000000000l
	private val NEGATIVE: Long = 0x8000000000000000l
	private val BODY_MASK: Long = 0xFFFFFFFFFFFFFFFFl >>> 12

	private def parseNumber(in: StringSource, ibf: FastStringBuffer): JsonNumber = { new JsonNumber(atof(in))
//
//
//		val sign = if(in.top == '-') {in.moveNext(); -1} else 1
//
//		var body:Long = 0l
//		var delta     = 0
//
//		if(in.top == '0') in.moveNext()
//		else {
//			while(in.top >= '0' && in.top <= '9') {
//				body = (body * 10) + (in.top - '0')
//				in.moveNext()
//			}
//		}
//
//		if(in.top == '.') {
//			in.moveNext()
//
//			if(in.top < '0' || in.top > '9') error()
//
//			while(in.top >= '0' && in.top <= '9') {
//				body = (body * 10) + (in.top - '0')
//				in.moveNext()
//				delta += 1
//			}
//		}
//
//		var expSign = 1
//		var expBody = 0
//
//		if(in.top == 'e' || in.top == 'E')
//		{
//			in.moveNext()
//
//			     if(in.top == '+') in.moveNext()
//			else if(in.top == '-') {expSign = -1; in.moveNext()}
//
//			while(in.top >= '0' && in.top <= '9') {
//				expBody = (expBody * 10) + (in.top - '0')
//				in.moveNext()
//			}
//
//		}
//
////		if(body != 0)
////			while (body % 10 == 0) {
////				body = body / 10
////				delta -= 1
////			}
//
//		var exp = (expBody * expSign) - delta
//
//		if(body == 0)  new JsonNumber(0)
//		else if(exp == 0) new JsonNumber(body * sign)
//		else {
//
////			var r = sign * body.toDouble
////			while(exp > 0)
////			{
////				r = r * 10
////				exp -= 1
////			}
////			while(exp < 0)
////			{
////				r = r / 10
////				exp += 1
////			}
////			new JsonNumber(r)
//
//
//			var e = (exp / LOG2)
//
//
//			while((body & 0x8000000000000l) == 0) {
//				body = body << 1
//				e -= 1
//			}
//
//			body = body << 1
//			e -= 1
//
//			body = body & BODY_MASK
//			val binary_exp = (e + 1023).toLong
//			val eb: Long = binary_exp << 52
//			val s = if(sign == 1) POSITIVE else NEGATIVE
//
//			val rrr = s | eb | body
//			new JsonNumber(java.lang.Double.longBitsToDouble(rrr))
//		}
	}

	private val toHex = Array[UChar](
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

	private val toScape = Array[UChar](
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

	private def error(): Nothing = throw new Exception("Invalid JSON")

	def isRegular(i: UChar) = try{isRegularTable(i)}catch{case t: IndexOutOfBoundsException => true}

	private val isRegularTable = Array(
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		true , true , true , true , true , true , true , true ,
		true , true , true , true , true , true , true , true ,
		true , true , true , true , true , true , true , true ,
		true , true , true , true , true , true , true , true ,
		true , true , true , true , true , true , true , true ,
		true , true , true , true , true , true , true , true ,
		true , true , true , true , true , true , true , true ,
		true , true , true , true , false, true , true , true ,
		true , true , true , true , true , true , true , true ,
		true , true , true , true , true , true , true , true ,
		true , true , true , true , true , true , true , true ,
		true , true , true , true , true , true , true , false)


	private val valueParsers = Array[(StringSource, FastStringBuffer) => JsonValue](
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , err _           , doParseString _ , err _           ,
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , parseNumber _   , err _           , err _           ,
		parseNumber _   , parseNumber _   , parseNumber _   , parseNumber _   , parseNumber _   , parseNumber _   ,
		parseNumber _   , parseNumber _   , parseNumber _   , parseNumber _   , err _           , err _           ,
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , parseArray _    , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , err _           , err _           , err _           ,
		parseBoolean _  , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , parseNull _     , err _           , err _           , err _           ,
		err _           , err _           , parseBoolean _  , err _           , err _           , err _           ,
		err _           , err _           , err _           , parseObject _ )




























	def valid_digit(c: Char): Boolean = (c >= '0' && c <= '9')

	def atof (p: StringSource): Number =
	{
		var integer = true
		var frac: Boolean = false
		var sign: Double  = 0
		var ivalue: Long = 0
		var scale: Double = 0


		// Get sign, if any.

		sign = 1.0;

		if (p.top == '-')
		{
			sign = -1.0;
			p.moveNext();

		}
		else if (p.top == '+')
		{
			p.moveNext();
		}

		// Get digits before decimal point or exponent, if any.

		while (valid_digit(p.top))
		{
			ivalue = ivalue * 10 + (p.top - '0');
			p.moveNext()
		}

		var value:Double = ivalue.toDouble
		if (p.top == '.')
		{
			integer = false
			var pow10 = 10.0;
			p.moveNext();
			while (valid_digit(p.top))
			{
				value += (p.top - '0') / pow10;
				pow10 *= 10.0;
				p.moveNext();
			}
		}

		// Handle exponent, if any.

		scale = 1.0;
		if ((p.top == 'e') || (p.top == 'E'))
		{
			integer = false
			var expon: Int = 0

			// Get sign of exponent, if any.

			p.moveNext();
			if (p.top == '-')
			{
				frac = true;
				p.moveNext();

			}
			else if (p.top == '+')
			{
				p.moveNext();
			}

			// Get digits of exponent, if any.

			expon = 0
			while (valid_digit(p.top)) {
				expon = expon * 10 + (p.top - '0');
				p.moveNext()
			}

			if (expon > 308) expon = 308;

			// Calculate scaling factor.

			while (expon >= 50) { scale *= 1E50; expon -= 50; }
			while (expon >=  8) { scale *= 1E8;  expon -=  8; }
			while (expon >   0) { scale *= 10.0; expon -=  1; }
		}

			// Return signed and scaled floating point result.

		if(integer)
			sign * ivalue
		else
			sign * (if(frac) (value / scale) else (value * scale));
	}
}
