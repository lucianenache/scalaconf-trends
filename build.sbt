import Dependencies._

lazy val root = (project in file("."))
  .settings(
    inThisBuild(
      List(
        organization := "com.example",
        scalaVersion := "2.12.2",
        version := "0.1.0-SNAPSHOT"
      )),
    name := "scalaconf-trends",
    libraryDependencies += twitter4s,
    libraryDependencies += scalaTest % Test,
    libraryDependencies += scalaScraper
  )
  .dependsOn(sharedModel, twitterService)

lazy val sharedModel = (project in file("shared-model"))

lazy val twitterService = (project in file("twitter-service"))
