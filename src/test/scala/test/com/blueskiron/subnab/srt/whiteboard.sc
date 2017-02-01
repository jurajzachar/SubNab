package com.blueskiron.subnab
import scala.util.{Success, Failure}

object whiteboard {
  import com.blueskiron.subnab.srt._
	/*   */
  //val testFileContents = scala.io.Source.fromURL(getClass.getResource("/non-iso-extended-ascii.srt"))
  val testFileContents = scala.io.Source.fromFile("/Users/juri/github_repos/SubNab/src/test/resources/non-iso-extended-ascii.srt", "IBM01149").getLines().mkString
                                                  //> testFileContents  : String = 
                                                  //| Ñë?_,/øÑÈ÷>Þ/_ÁËèÑÂÁÊÑÍË.ÑÊ,:?�
                                                  //| �ëø?¦Á>Á¦ÃÁÀÁÊ÷ÄÑÁø%/>ZÈÑ
                                                  //| ëÈ?¦Ò_øÊÁÀÎ/_Ñ/,?>Á�
                                                  //| �ÈÊ÷%>ð:÷ËÈÍøÄ/ãÑÂ?>/>Ë,Á¦ÊÁøÍÂ%Ñ,ð�
                                                  //| �&ÊÑ>÷ª/_/,?ÅÁË�
                                                  //| �?À?ÂÊÁ¦Î4%Á
                                                  //| øÊÁÀÎ÷]Á>ÙÄÇY%Á>?ÎÀÁ%ÁÅ÷ÄÑÁèÁÁ>/�
                                                  //| �ÍÀ/Ê
                                                  //| ?À+/¦ÎðªªÁ¦Ê/ÀðãÑÂ?>/>ÍËøÊÁ¦/Î?_>/¦Ç%Âª
                                                  //| Á¦³ÄÈð
                                                  //| H?ËÈÙ_¦Á�
                                                  //| �H?øÊ?ËÒ_�
                                                  //| �&ÊÁY?È?Í]>ÁÄÇÄ³
                                                  //| â?%/È?Ë³Y/Ë¸ËÈ/Ê?ÎÁ,Á¦:
                                                  //| ÂÊ/>Á/øÊÑ>÷ª/_¦Í/,?Ëð_Â?%�
                                                  //| �_ÑÁÊÍ�
                                                  //| �î?ÃÑÂ?>/>Ë,Á¦,Í%È³ÊÁÀ/Ê
                                                  //| ?Î/¸:ÂÊ/2:>/_Á>÷øÊÒ_ÁÊÑÁ�
                                                  //| � ,?,È?_ÍøÊÑª%Ñ�
                                                  //| �
                                                  //| Output exceeds cutoff limit.
  
  /*
  Parser.parse(testFileContents) match {
  	case Success(list) => list.head
  	case Failure(t) => println(t)
  }
*/
  
  /*
  val insertion = 6
  val sortedList = {insertion :: List(1,2,3,4,5)}.sorted
  
  sortedList.foldLeft((List[Int]()))((list,elem) => {
   list match {
   	 case Nil => elem :: list
   	 case x :: xs => if(x < insertion) elem :: list else elem + 1 :: list
  }
  }).reverse


sortedList.filter(_ != 1)


val input = "00:00:00,0"
Time.parse(input)

val url = getClass.getResource("/icons/subnab_256.png")

//val iconImage = new javax.swing.ImageIcon(getClass.getResource("icons/subnab_256.png"))

val line = "  val metaVersion = \"${version}\""

val regex = "val\\smetaVersion\\s=\\s(.*)".r

regex.findAllIn(line).matchData foreach { m => println(m.group(1)) }
regex.replaceFirstIn(line, "val metaVersion = \"v1.0.0\"")
*/
                                                  
                                                 Meta.toString
                                                  //> res0: String = " 
                                                  //|       SubNab - SRT Subtitles Editor         
                                                  //|       version: development (unreleased)      
                                                  //|       (c) Blue Skiron
                                                  //|     "
}