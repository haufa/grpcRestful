ThisBuild / version := "0.9.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "RESTFULSERVER"
  )

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-core" % "1.2.1",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.12.328",
  "com.amazonaws" % "aws-lambda-java-events" % "3.11.0",
  "com.google.code.gson" % "gson" % "2.10"

)

libraryDependencies += "org.json" % "json" % "20220924"
libraryDependencies += "com.typesafe" % "config" % "1.4.2"
libraryDependencies += "org.slf4j" % "slf4j-api" % "2.0.3"
libraryDependencies += "ch.qos.logback" % "logback-core" % "1.4.4"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.4"

assembly / assemblyMergeStrategy  := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}


Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)