package bryghts.tests

import org.specs2.mutable._
import org.specs2.ScalaCheck
import scala.util.Random
import com.bryghts.kissjson.parser2.tools.uchar._

class UCharSpecs extends Specification with ScalaCheck
{





	"The isWhitespace method" should {

//		"detect whitespaces correctly (FULL)" in{
//
//			for(i <- 0 until Int.MaxValue) {
//				new UChar(i).isWhitespace must beEqualTo (Character.isWhitespace(i))
//			}
//
//			for(i <- Int.MinValue until 0) {
//				new UChar(i).isWhitespace must beEqualTo (Character.isWhitespace(i))
//			}
//
//			new UChar(Int.MaxValue).isWhitespace must beEqualTo (Character.isWhitespace(Int.MaxValue))
//
//		}

		"detect whitespaces correctly (Smoke)" in{

			def check(in: UChar) =
				in.isSpace must beEqualTo (Character.isWhitespace(in))

			for(c:UChar <- (0:UChar) to (255:UChar))  // Checking the full Extended ASCII Range
				check(c)

			for(n <- 0 to 1024) // Checking a random sample
				check(Random.nextPrintableChar())

			check(UChar.EOF)
		}

	}

//	"The equality method" should {
//		"work with normal characters" in {
//
//			def doCheck(a: Int, b: UChar) =
//				(a == b) must beTrue
//
//			def check(c: Int) =
//				doCheck(c, c.asInstanceOf[UChar])
//
//			for(c <- 0 to 255)  // Checking the full Extended ASCII Range
//				check(c)
//
//			for(n <- 0 to 1024) // Checking a random sample
//				check(Random.nextInt())
//
//			check(UChar.EOF)
//		}
//	}


}


