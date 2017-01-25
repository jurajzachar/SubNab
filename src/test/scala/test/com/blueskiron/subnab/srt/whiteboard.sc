package com.blueskiron.subnab
import scala.util.{Success, Failure}

object whiteboard {
  import com.blueskiron.subnab.srt._
	/*
  val testFileContents = scala.io.Source.fromURL(getClass.getResource("/Error.srt"))
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
                                                  //|       version: unreleased
                                                  //|       (c) Blue Skiron
                                                  //|     "
}