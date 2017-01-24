package test.com.blueskiron.subnab.srt

import org.scalatest._
import com.blueskiron.subnab.srt._

class SRTParserSpec extends FlatSpec with Matchers {
  "The SRTParser" should "read subtitles file into sections" in new SRTSubtitlesFixture {
    Parser.parse(buffSource).foreach(println(_))
  }

  trait SRTSubtitlesFixture {
    import scala.io.Source
    lazy val buffSource = Source.fromURL(getClass.getResource("/subtitles.srt"))
  }
}
