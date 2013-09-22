KissJson
========

KissJson is Scala library designed to work with the JSON data exchange format. This library has three main characteristics:

* A collection of types that can represent any kind of JSON value.
* Conversors that can convert
    * from JsonValue to a string representation (and vice versa)
    * from JsonValue to scala types (and vice versa)
* Tools to work directly with the JsonValues

Examples
--------

The usage of the library is documented in the form of examples in sourcecode:

* [How to write JSON literals](https://github.com/marcesquerra/KissJson/blob/master/samples/bright/samples/JsonLiteral.scala)
* [Converting from Scala classes to JsonValues](https://github.com/marcesquerra/KissJson/blob/master/samples/bright/samples/JsonEncoder.scala)
* [Converting from JsonValues to Scala classes](https://github.com/marcesquerra/KissJson/blob/master/samples/bright/samples/JsonDecoder.scala)
* [Parsing from a String to a JsonValues](https://github.com/marcesquerra/KissJson/blob/master/samples/bright/samples/JsonParser.scala)
* [Some JsonValue usage examples](https://github.com/marcesquerra/KissJson/blob/master/samples/bright/samples/JsonUsage.scala)


Get it
------

Maven Dependency:

```xml
<dependency>
    <groupId>com.bryghts.kissjson</groupId>
    <artifactId>kissjson_2.10</artifactId>
    <version>0.0.1-M2</version>
</dependency>
```

Sbt Dependency:

```scala
libraryDependencies += "com.bryghts.kissjson" % "kissjson_2.10" % "0.0.1-M2"
```

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/marcesquerra/kissjson/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-31897016-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>
