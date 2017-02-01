package test.com.blueskiron.subnab.srt

import org.scalatest._
import com.blueskiron.subnab.srt._
import scala.util.{ Failure, Success }

//TODO
class SRTParserSpec extends FlatSpec with Matchers {
  "The SRTParser" should "read subtitles file into sections" in new SRTSubtitlesFixture {
    Parser.parse(buffSource.getLines) match {
      case Failure(t)    => fail(t)
      case Success(list) => //profit
    }
  }

  trait SRTSubtitlesFixture {
    import scala.io.Source
    lazy val buffSource = Source.fromURL(getClass.getResource("/Portuguese.srt"))
  }
}
