name := "database"

version := "0.0.13"
scalaVersion := "2.13.6"

resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.26",
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "com.limitra.sdk" %% "core" % "0.0.8"
)

logLevel := Level.Error
