name := "database"

version := "0.0.11"
scalaVersion := "2.13.5"

resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.23",
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "com.limitra.sdk" %% "core" % "0.0.7"
)

logLevel := Level.Error
