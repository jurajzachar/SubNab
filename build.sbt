import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.blueskiron",
      scalaVersion := "2.11.8",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "SubNab",
    libraryDependencies ++= Seq(
    	scalaTest % Test,
    	"org.slf4j" % "slf4j-api" % "1.7.12",
    	"ch.qos.logback" % "logback-classic" % "1.1.7",
		"com.chuusai" %% "shapeless" % "2.3.2",
		"io.reactivex" %% "rxscala" % "0.26.5",
		"org.scala-lang.modules" %% "scala-swing" % "2.0.0-M2",
		"de.sciss" %% "swingplus" % "0.2.2")
  )
