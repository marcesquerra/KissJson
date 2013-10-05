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


/////////////////////////////////////////////////////////////////////////////////////////////////////////
// THE 'J' METHOD
/////////////////////////////////////////////////////////////////////////////////////////////////////////



	// THIS JSON SCALA LITERAL   is the equivalent to         	THIS JSON OBJECT DOCUMENT
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






	// THIS JSON SCALA LITERAL   is the equivalent to         	THIS JSON ARRAY DOCUMENT
	                                                        //
	val jarray = J(J(1, 2, 3), J(4, 5, 6))                  //	[[1, 2, 3], [4, 5, 6]]
	val jarray1 = J(J(true), J(false, false))                  //	[[1, 2, 3], [4, 5, 6]]

	println(s"The Json Array is: ${jarray.render}")





/////////////////////////////////////////////////////////////////////////////////////////////////////////
// BASIC TYPES
/////////////////////////////////////////////////////////////////////////////////////////////////////////







	// Strings

	val s1: JsonString  =              "Hello 1"
	val s2              = JsonString  ("Hello 2")
	val s3: JsonValue   =              "Hello 3"



	println(s1.render)
	println(s2.render)
	println(s3.render)









	// Numbers

	val n1: JsonNumber  =              1
	val n2              = JsonNumber  (2)
	val n3: JsonValue   =              3
	val n4: JsonNumber  =              4.4
	val n5              = JsonNumber  (5.5)
	val n6: JsonValue   =              6.6



	println(n1.render)
	println(n2.render)
	println(n3.render)
	println(n4.render)
	println(n5.render)
	println(n6.render)









	// Booleans

	val b1: JsonBoolean =              true
	val b2              = JsonBoolean (false)
	val b3: JsonValue   =              true



	println(b1.render)
	println(b2.render)
	println(b3.render)









	// nulls

	val nl1               = JsonNull
	val nl2: JsonValue    = JsonNull
	val nl3: JsonBoolean  = JsonNull
	val nl4: JsonString   = JsonNull
	val nl5: JsonNumber   = JsonNull
	val nl6: JsonArray[_] = JsonNull
	val nl7: JsonObject   = JsonNull



	println(nl1.render)
	println(nl2.render)
	println(nl3.render)
	println(nl4.render)
	println(nl5.render)
	println(nl6.render)
	println(nl7.render)




}




