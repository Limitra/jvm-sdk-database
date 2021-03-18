name := "database"

version := "0.0.9"
scalaVersion := "2.13.5"

resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.23",
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "com.limitra.sdk" %% "core" % "0.0.6"
)
