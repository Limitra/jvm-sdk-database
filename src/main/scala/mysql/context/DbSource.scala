package com.limitra.sdk.database.mysql

import slick.jdbc.MySQLProfile

import scala.concurrent.duration._

/**
  * DbSource is the abstract class representing our database.
  * Reads and applies the database settings as a single customized object from a configuration file.
  * On the other hand, it includes derived extension methods.
  */
abstract class DbSource(configName: String = "") extends MySQLProfile.API {
  // Set config object for database configuration
  private val _config = Config(configName)

  val DatabaseName = _config.String("DatabaseName")

  val MinThreads = _config.Int("MinThreads", 20) // Default 20
  val MaxThreads = _config.Int("MaxThreads", 20) // Default 20
  val QueueSize = _config.Int("QueueSize", 1000) // Default 1000
  val MaxConnections = _config.Int("MaxConnections", 20) // Default 20

  val RegisterMbeans = _config.Boolean("RegisterMbeans", false) // Default false
  val UseSSL = _config.Boolean("UseSSL", false) // Default false
  val AllowPublicKeyRetrieval = _config.Boolean("AllowPublicKeyRetrieval", true) // Default true
  val AutoReconnect = _config.Boolean("AutoReconnect", true) // Default true
  val UseUnicode = _config.Boolean("UseUnicode", true) // Default true
  val FailOverReadOnly = _config.Boolean("FailOverReadOnly", false) // Default false

  val MaxReconnects = _config.Int("MaxReconnects", 10) // Default 10
  val CharacterEncoding = _config.String("CharacterEncoding", "UTF-8") // Default UTF-8
  val ServerTimezone = _config.String("ServerTimezone", "UTC") // Default UTC
  val Charset = _config.String("Charset", "utf8mb4") // Default utf8mb4
  val KeepAliveTime = _config.Duration("KeepAliveTime", 1.minute) // Default 1 minute

  // Create a database instance with customized configuration
  lazy val Db: MySQLProfile.backend.DatabaseDef = MySQLProfile.backend.Database.forURL("jdbc:mysql://" + _config.String("Server") + "/" + DatabaseName +
    "?useUnicode=" + UseUnicode + "&charset=" + Charset + "&serverTimezone=" + ServerTimezone + "&characterEncoding=" + CharacterEncoding +
    "&autoReconnect=" + AutoReconnect + "&failOverReadOnly=" + FailOverReadOnly + "&maxReconnects=" + MaxReconnects +
    "&allowPublicKeyRetrieval=" + AllowPublicKeyRetrieval + "&useSSL=" + UseSSL,
    driver = "com.mysql.cj.jdbc.Driver",
    user = _config.String("Username"),
    password = _config.String("Password"),
    executor = AsyncExecutor(DatabaseName + "_threads", MinThreads, MaxThreads, QueueSize, MaxConnections, KeepAliveTime, RegisterMbeans))

  // Implicit extension methods for Query
  implicit class QueryExtender[+T, E](query: Query[T, E, Seq]) extends DbQueryExtender[T, E](Db, query)

  // Implicit extension methods for Table Query
  implicit class TableQueryExtender[E <: BaseBox, T <: BaseBoxTable[E]](query: DbBoxQuery[E, T]) extends DbTableQueryExtender[E, T](Db, query)

  // Implicit extension methods for DBIO
  implicit class ActionExtender[R](action: DBIOAction[R, NoStream, Nothing]) extends DbActionExtender[R](Db, action)
}
