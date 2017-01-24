package com.blueskiron.subnab.srt

/**
 * @author Juraj Zachar
 *
 */
case class Entry(seq: Int, begin: Time, end: Time, caption: String) {
  override def toString: String = s"$seq\n$begin --> $end\n$caption\n\n"
}