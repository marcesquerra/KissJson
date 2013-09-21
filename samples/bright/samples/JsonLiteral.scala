package bright.samples

import com.bryghts.kissjson._


object JsonLiteral extends App
{

//
//
// KissJson offers a simple way to create JSON values with the ability to
// create JSON literals directly in the Scala code. It's mainly done with the
// 'J' method.
//
//


////////////////////////////////////////////////////////////////////////////////
// THE 'J' METHOD
////////////////////////////////////////////////////////////////////////////////



	// THIS JSON SCALA LITERAL   is the equivalent to         	THIS JSON DOCUMENT
	                                                        //
	val json =                                              //
	J(                                                      //	{
	    `type`     = "Sample Json Object",                  //		"type":     "Sample Json Object",
	     id        = 12,                                    //		"id":       12,
	     rating    = 3.7,                                   //		"rating":   3.7,
	     content   = "characters",                          //		"content":  "characters",
	     enabled   = true,                                  //		"enabled":  true,
	     items     =                                        //		"items":
	     J(                                                 //		{
	             characters = J("Spock", "Luck")            //			"characters": ["Spock", "Luck"]
	     )                                                  //		}
	)                                                       //	}


	println(s"The Json Object is: ${json.render}")

	val jarray = J(J(1, 2, 3), J(4, 5, 6))                  //	[[1, 2, 3], [4, 5, 6]]

	println(s"The Json Array is: ${jarray.render}")


	// JsonValues

	val s1: JsonString  =              "Hello 1"
	val s2              = JsonString  ("Hello 2")
	val s3: JsonValue   =              "Hello 3"

	println(s1.render)
	println(s2.render)
	println(s3.render)

	val i1: JsonInteger =              1
	val i2              = JsonInteger (2)
	val i3: JsonNumber  =              3
	val i4              = JsonNumber  (4)
	val i5: JsonValue   =              5

	println(i1.render)
	println(i2.render)
	println(i3.render)
	println(i4.render)
	println(i5.render)

	val r1: JsonReal    =              1.1
	val r2              = JsonReal    (2.2)
	val r3: JsonNumber  =              3.3
	val r4              = JsonNumber  (4.4)
	val r5: JsonValue   =              5.5

	println(r1.render)
	println(r2.render)
	println(r3.render)
	println(r4.render)
	println(r5.render)

	val b1: JsonBoolean =              true
	val b2              = JsonBoolean (false)
	val b3: JsonValue   =              true

	println(b1.render)
	println(b2.render)
	println(b3.render)


	// JsonNull

	val n1              = JsonNull
	val n2: JsonValue   = JsonNull
	val n3: JsonBoolean = JsonNull
	val n4: JsonString  = JsonNull
	val n5: JsonNumber  = JsonNull
	val n6: JsonInteger = JsonNull
	val n7: JsonReal    = JsonNull
	val n8: JsonArray   = JsonNull
	val n9: JsonObject  = JsonNull

	println(n1.render)
	println(n2.render)
	println(n3.render)
	println(n4.render)
	println(n5.render)
	println(n6.render)
	println(n7.render)
	println(n8.render)
	println(n9.render)
}