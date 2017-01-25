package com.blueskiron.subnab

import com.blueskiron.subnab.srt.SubNabApp

/**
 * @author Juraj Zachar
 *
 */
object Main extends App {
  lazy val ui = new SubNabApp
  ui.visible = true
}