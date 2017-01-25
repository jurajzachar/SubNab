import sbtassembly.AssemblyPlugin.defaultShellScript

lazy val subnab = (project in file(".")).
settings(
  inThisBuild(List(
    name := "SubNab",
    organization := "com.blueskiron",
    scalaVersion := "2.11.8",
    version      := "0.1.0-SNAPSHOT",
  	libraryDependencies ++= Seq(
    	"org.scalatest" %% "scalatest" % "2.2.6",
    	"org.slf4j" % "slf4j-api" % "1.7.20",
    	"ch.qos.logback" % "logback-classic" % "1.1.7",
    	"com.chuusai" %% "shapeless" % "2.3.2",
    	"io.reactivex" %% "rxscala" % "0.26.5",
    	"org.scala-lang.modules" %% "scala-swing" % "2.0.0-M2",
    	"de.sciss" %% "swingplus" % "0.2.2"))
  	))

//assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultShellScript))
assemblyJarName in assembly := s"${name.value}-${version.value}-fat.jar"
