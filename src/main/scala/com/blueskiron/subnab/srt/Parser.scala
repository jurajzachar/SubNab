package com.blueskiron.subnab.srt

import scala.concurrent.duration.Duration
import scala.io.BufferedSource
import shapeless._
import syntax.std.traversable._
import scala.util.Try
import scala.util.Failure
import scala.util.Success

/**
 * @author Juraj Zachar
 *
 */
object Parser {

  case class InvalidSRTContentException(msg: String) extends Exception(msg)

  def parse(iter: Iterator[String], validated: Try[List[String]]): Try[List[String]] = {

    def iterateOrFail: Try[String] = {
      if (!iter.hasNext) Failure(new InvalidSRTContentException(s"File not complete"))
      else {
        //remove legacy BOM character (if any)
        val value = iter.next.trim.replace("\uFEFF", "").replace("\uFFFE", "")
        Success(value)
      }
    }

    def iterateToEmptyLineOrFail(captionLine: String): Try[String] = {
      if (!iter.hasNext && captionLine.isEmpty()) {
        Failure(new InvalidSRTContentException(s"Caption not complete: $captionLine"))
      } else if (!iter.hasNext && !captionLine.isEmpty) {
        Success(captionLine)
      } else {
        val line = iter.next
        val combined = captionLine + line
        //end of caption
        if (line.trim().isEmpty()) Success(combined)
        else iterateToEmptyLineOrFail(combined)
      }
    }

    if (validated.isFailure) {
      Failure(InvalidSRTContentException(s"Cannot parse source: ${validated.failed.map(error => error.getMessage)}"))
    } else if (!iter.isEmpty) {
      val seqId = iterateOrFail
      val time = iterateOrFail
      val caption = iterateToEmptyLineOrFail("")
      (seqId, time, caption) match {
        case (Success(seqIdLine), Success(timeLine), Success(captionLine)) if (seqIdLine.matches("^\\d+$") && timeLine.contains("-->")) =>
          parse(iter, Success(List(seqIdLine, timeLine, captionLine) ++ validated.getOrElse(Nil)))
        case _ => Failure(InvalidSRTContentException(s"Cannot parse file: ${extractFailureMessage(seqId, time, caption)}"))
      }
    } else {
      //end of stack 
      validated.flatMap(list => if (list.isEmpty) Failure(new InvalidSRTContentException(s"Empty File")) else validated)
    }
  }

  def extractFailureMessage(seqId: Try[Any], time: Try[Any], caption: Try[Any]): String = {
    List(seqId, time, caption).filter(_.isFailure).map(_.failed.get.getLocalizedMessage).reverse.mkString(", ")
  }

  def parse(lines: Iterator[String]): Try[List[Entry]] = {
    parse(lines, Success(Nil)).flatMap { validatedLines =>
      val validation = validatedLines.grouped(3).toList.map(chunk => {
        val tripleXList = chunk.toHList[String :: String :: String :: HNil].get.tupled
        val times = tripleXList._2.split(" --> ")
        validate(tripleXList._1, times(0), times(1), tripleXList._3)
      }).reverse.partition(_.isSuccess)
      //check if any failures
      validation._2 match {
        case Nil => Success(validation._1.map(_.get)) //return parsed and validated entries
        case _ => validation._2.head.map(List(_)) //return first detected failure
      }
    }
  }

  def validate(chunk: (String, String, String, String)): Try[Entry] = {
    for {
      seqId <- validateSequenceId(chunk._1)
      times <- validateTime(chunk._2, chunk._3)
      caption <- {
        val text = chunk._4
        if (text.isEmpty()) Failure(InvalidSRTContentException(s"Caption line cannot be empty for sequence: $chunk"))
        else Success(text)
      }
    } yield Entry(seqId, times._1, times._2, caption)
  }

  def validateSequenceId(arg: String): Try[Int] = {
    if (arg.matches("^\\d+$")) Success(Integer.parseInt(arg)) else Failure(InvalidSRTContentException(s"Invalid sequence identifier: $arg"))
  }

  def validateTime(begin: String, end: String): Try[(Time, Time)] = {
    for {
      beginTime <- Time.parse(begin)
      endTime <- Time.parse(end)
    } yield (beginTime, endTime)
  }

}