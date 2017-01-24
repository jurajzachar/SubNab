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
  
  val insertion = 6                               //> insertion  : Int = 6
  val sortedList = {insertion :: List(1,2,3,4,5)}.sorted
                                                  //> sortedList  : List[Int] = List(1, 2, 3, 4, 5, 6)
  
  sortedList.foldLeft((List[Int]()))((list,elem) => {
   list match {
   	 case Nil => elem :: list
   	 case x :: xs => if(x < insertion) elem :: list else elem + 1 :: list
  }
  }).reverse                                      //> res0: List[Int] = List(1, 2, 3, 4, 5, 6)


sortedList.filter(_ != 1)                         //> res1: List[Int] = List(2, 3, 4, 5, 6)


val input = "00:00:00,0"                          //> input  : String = 00:00:00,0
Time.parse(input)                                 //> res2: scala.util.Try[com.blueskiron.subnab.srt.Time] = Success(00:00:00,0)
}