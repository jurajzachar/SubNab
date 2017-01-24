package com.blueskiron.subnab.srt

import scala.util.Try
import scala.util.Success
import scala.util.Failure
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * @author Juraj Zachar
 *
 */
object Time {

  val fixedLength = 12
  val format = "HH:mm:ss,SSS"
  val dateTimeFormatter = DateTimeFormatter.ofPattern(format)

  private val milliToNanoMultiplier = 1000000

  def apply(localTime: LocalTime): Time =
    Time(localTime.getHour, localTime.getMinute, localTime.getSecond, localTime.getNano / Time.milliToNanoMultiplier)

  def parse(input: String): Try[Time] = {
    val token = {
      if (input.length() < 12) {
        val padding = 12 - input.length()
        input + ("0" * padding)
      } else {
        input
      }
    }
    try {
      val localTime = LocalTime.parse(token, dateTimeFormatter)
      Success(Time(localTime))
    } catch {
      case e: Exception => Failure(e)
    }
  }

}

case class Time(hour: Int, minute: Int, second: Int, millis: Int) {

  val localTime = LocalTime.of(hour, minute, second, millis * Time.milliToNanoMultiplier)

  override def toString: String = {
    val hourPadding = s"${if (hour < 10) "0" + hour else hour}"
    val minutePadding = s"${if (minute < 10) "0" + minute else minute}"
    val secondPadding = s"${if (second < 10) "0" + second else second}"
    val msPadding = s"${if (millis < 100) "0" + millis else if (millis < 10) "0" + millis else millis}"
    s"$hourPadding:$minutePadding:$secondPadding,$millis"
  }

}