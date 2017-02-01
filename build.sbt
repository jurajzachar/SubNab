import sbtassembly.AssemblyPlugin.defaultShellScript
name := "SubNab"
organization := "com.blueskiron"
scalaVersion := "2.11.8"
version      := "0.1.2-SNAPSHOT"

libraryDependencies ++= Seq(
    	"org.scalatest" %% "scalatest" % "2.2.6",
    	"org.slf4j" % "slf4j-api" % "1.7.20",
    	"ch.qos.logback" % "logback-classic" % "1.1.7",
    	"com.chuusai" %% "shapeless" % "2.3.2",
    	"io.reactivex" %% "rxscala" % "0.26.5",
    	"org.scala-lang.modules" %% "scala-swing" % "2.0.0-M2",
    	"de.sciss" %% "swingplus" % "0.2.2")

//generate runtime version info
resourceGenerators in Compile += Def.task {
  val file = (resourceManaged in Compile).value / "subnab.properties"
  val contents = "version=%s".format(version.value)
  IO.write(file, contents)
  Seq(file)
}.taskValue

enablePlugins(JDKPackagerPlugin)
assemblyJarName in assembly := s"${name.value}-${version.value}-fat.jar"
packageDescription := "SubNab - A simple SRT Subtitles Editor"
maintainer := "Blue Skiron"
lazy val iconGlob = sys.props("os.name").toLowerCase match {
  case os if os.contains("mac") ⇒ "*.icns"
  case os if os.contains("win") ⇒ "*.ico"
  case _ ⇒ "*.png"
}
jdkAppIcon :=  (sourceDirectory.value ** iconGlob).getPaths.headOption.map(file)
jdkPackagerType := "installer"
jdkPackagerJVMArgs := Seq("-Xmx1g")
jdkPackagerProperties := Map("app.name" -> name.value, "app.version" -> s"v$version.value")
jdkPackagerAppArgs := Seq(maintainer.value, packageSummary.value, packageDescription.value)
