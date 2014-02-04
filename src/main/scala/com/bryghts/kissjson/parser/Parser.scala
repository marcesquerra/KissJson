package com.bryghts.kissjson
package parser

import tools.StringSource
import scala.util.Try
import scala.collection.immutable.TreeMap

object Parser
{

	def apply(in: String): Try[Any] =
		Try(parse(StringSource(in)))

	private def parse(in: StringSource): Any =
	{

		in.skipWhitespaces()

		val r = valueParsers(in.top)(in)

		in.skipWhitespaces()

		if(in.top != 0x00)  error()
		else                r
	}


	private def err(in: StringSource): Nothing = error()

	private def parseArray(in: StringSource): Vector[Any] = {
		var r = Vector.newBuilder[Any]

		in.moveNext()

		while(in.top != ']') {
			in.skipWhitespaces()
			r += valueParsers(in.top)(in)
			in.skipWhitespaces()
			if(in.top == ',') in.moveNext()
			else if(in.top != ']') error()
		}

		in.moveNext()

		r.result()
	}

	private def parseObject(in: StringSource): Map[String, Any] =
	{
		val r = TreeMap.newBuilder[String, Any]

		in.moveNext()

		while(in.top != '}') {
			in.skipWhitespaces()

			if(in.top != '"') error()

			val k = parseString(in)

			in.skipWhitespaces()

			if(in.top != ':') error()
			in.moveNext()

			in.skipWhitespaces()

			r += new Pair(k, valueParsers(in.top)(in))

			in.skipWhitespaces()
			if(in.top == ',') in.moveNext()
			else if(in.top != '}') error()
		}

		in.moveNext()

		r.result()
	}

	private def parseBoolean(in: StringSource): Boolean =

		if(in.top == 't')
		{

			in.jumpTrue()

			true
		}
		else
		{

			in.jumpFalse()

			false
		}

	private def parseNull(in: StringSource): Null =
	{
		in.jumpNull()

		null
	}

	private def parseString(in: StringSource): String = in.collectChars()

	private def parseNumber(in: StringSource): Any = atof(in)

	private def error(): Nothing = throw new Exception("Invalid JSON")

	def isRegular(i: Char) = try{isRegularTable(i)}catch{case t: IndexOutOfBoundsException => true}

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


	private val valueParsers = Array[(StringSource => Any)](
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , err _           , err _           , err _           ,
		err _           , err _           , err _           , err _           , parseString _   , err _           ,
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


	private def valid_digit(c: Char): Boolean = (c >= '0' && c <= '9')

	private def atof (p: StringSource): Any =
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
