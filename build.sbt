name := "database"

version := "0.0.1"
scalaVersion := "2.12.8"

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.17",

  "com.typesafe.slick" %% "slick" % "3.3.0",
  "com.limitra.sdk" %% "core" % "0.0.1"
  //"com.limitra.sdk" %% "core" % "0.0.1-alpha-5" from "file:///hdd/dev/limitra/limitra-sdk/jvm/jvm-core/target/scala-2.12/core-assembly-0.0.1-alpha-5.jar"
)
