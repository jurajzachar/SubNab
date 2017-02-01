package com.blueskiron.subnab.srt

import scala.util.Try
import java.nio.charset.Charset

sealed trait AppEvent
case class Source(entries: List[Entry], enc: Encoding, filePath: Option[String], syncedToFs: Boolean) extends AppEvent
case class Load(path: String) extends AppEvent
case class Save(path: Option[String]) extends AppEvent
case class Close(withSysExit: Boolean) extends AppEvent
case class Search(text: String) extends AppEvent
case class Get(seqId: Int, fromTextPosition: Int) extends AppEvent
case class Modify(func: (List[Entry]) => Try[List[Entry]]) extends AppEvent
case class SearchMatch(seqId: Int, from: Int, position: Int, lenght: Int) extends AppEvent
case class Found(matches: List[SearchMatch]) extends AppEvent
case object NextSearchMatch extends AppEvent
case class Encoding(code: String) extends AppEvent
object Encoding {
  val autoDetectSet = Set(
    Charset.defaultCharset().name(),
    "UTF-8",
    "UTF-16LE",
    "UTF-16BE",
    "windows-1252",
    "ISO-8859-1",
    "ISO-8859-3",
    "ISO-8859-15",
    "x-MacRoman",
    "IBM437", //DOS
    "windows-1256", //windows arabic
    "ISO-8859-6", //iso arabic
    "windows-1257", //windows baltic
    "ISO-8859-4", //iso baltic
    "ISO-8859-14", //iso celtic
    "windows-1250", //central european
    "ISO-8859-2", //central european
    "windows-1251", //windows cyrillic
    "IBM866", //cp cyrillic
    "KOI8-R", // cyrillic koi-r
    "KOI8-U", // cyrillic koi-u
    "ISO-8859-13", //estonian
    "windows-1253", //windows greek
    "ISO-8859-7", //iso greek
    "windows-1255", //windows hebrew
    "ISO-8859-8", //iso hebrew
    "ISO-8859-10", //nordic
    "ISO-8859-16", //romanian
    "windows-1254", //windows turkish
    "ISO-8859-9", //iso turkish
    "windows-1254", //windows vietnamese
    "GBK", //simplified chinese
    "GB18030", //simplified chinese PRC standard",
    "Big5", //traditional chinese
    "Big5-HKSCS", //traditional chinese with HK extension
    "SHIFT_JIS", //Shift-JIS japanese
    "x-IBM943", //IBM OS/2 Japanese, superset of Cp932
    "EUC-JP", //EUC japanese
    "EUC-KR" //EUC korean
  )
}
