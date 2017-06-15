import sbt._, Keys._
import com.typesafe.sbt.packager.docker._
import com.typesafe.sbt.packager.docker.Dockerfile._

name := "common-words"

version := "0.0.1"

scalaVersion := "2.11.8"

sparkVersion := "2.1.0"

sparkComponents ++= Seq("sql")


libraryDependencies ++= Seq(
  "net.ruippeixotog" %% "scala-scraper" % "2.0.0-RC2"
)


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "BUILD" => MergeStrategy.discard
  case _ => MergeStrategy.deduplicate
}

// Remove all jar mappings in universal and append the fat jar
mappings in Universal := {
  val universalMappings = (mappings in Universal).value
  val fatJar = (assembly in Compile).value
  val filtered = universalMappings.filter {
    case (file, name) => !name.endsWith(".jar")
  }
  filtered :+ (fatJar -> ("lib/" + fatJar.getName))
}

enablePlugins(DockerPlugin, JavaAppPackaging)

dockerRepository := Some("afree")
dockerCommands := Seq(
  Cmd("FROM", "epahomov/docker-spark:lightweighted"),
  Cmd("COPY", "opt/docker/lib/*.jar", "/common-words-assembly-0.0.1.jar"),
  ExecCmd("ENTRYPOINT", "/spark/bin/spark-submit",
    "--class", "com.afree.Top10WordsOnPages",
    "/common-words-assembly-0.0.1.jar")
)


