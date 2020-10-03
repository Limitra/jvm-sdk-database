name := "database"

version := "0.0.2"
scalaVersion := "2.12.8"

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.17",
  "com.typesafe.slick" %% "slick" % "3.3.0",
  "com.limitra.sdk" %% "core" % "0.0.2"
)
