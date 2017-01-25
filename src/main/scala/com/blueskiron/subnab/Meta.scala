package com.blueskiron.subnab
import java.util.Properties
import scala.io.Source
import scala.util.{ Try, Success, Failure }
import java.io.InputStream

/**
 * @author Juraj Zachar
 *
 */
object Meta {

  private val propsName = "subnab.properties"
  private lazy val defaultProps = {
    val props = new Properties()
    props.setProperty("name", "SubNab - SRT Subtitles Editor")
    props.setProperty("version", "development (unreleased)")
    props.setProperty("organization", "Blue Skiron")
    props.setProperty("copyright", "(c) Blue Skiron")
    props
  }

  private lazy val props = {
    try {
      val is: InputStream = getClass.getResource("/subnab.properties").openStream
      val p = new Properties()
      p.load(is)
      val merged = new Properties()
      merged.putAll(defaultProps)
      merged.putAll(p)
      merged
    } catch {
      case e: Exception => defaultProps
    }
  }

  override def toString = {
    s""" 
      ${props.get("name")}         
      version: ${props.get("version")}      
      ${props.get("copyright")}
    """
  }
}