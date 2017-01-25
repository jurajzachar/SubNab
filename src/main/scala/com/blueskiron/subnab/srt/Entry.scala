package com.blueskiron.subnab.srt

/**
 * @author Juraj Zachar
 *
 */
case class Entry(seq: Int, begin: Time, end: Time, caption: String) {

  def shiftTime(millis: Int): Entry = this.copy(begin = begin.shift(millis), end = end.shift(millis))

  override def toString: String = s"$seq\n$begin --> $end\n$caption\n\n"
}