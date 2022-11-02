ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "Homework2RESTful"
  )

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)

libraryDependencies += "org.json" % "json" % "20220924"
libraryDependencies += "com.google.code.gson" % "gson" % "2.10"
libraryDependencies += "com.typesafe" % "config" % "1.4.2"