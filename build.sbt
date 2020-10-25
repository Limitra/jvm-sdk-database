name := "database"

version := "0.0.4"
scalaVersion := "2.12.9"

resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.22",
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "com.limitra.sdk" %% "core" % "0.0.4"
)
