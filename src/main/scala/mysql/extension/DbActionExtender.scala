package com.limitra.sdk.database.mysql

import slick.jdbc.MySQLProfile.api._
import slick.jdbc.MySQLProfile.backend.DatabaseDef

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.experimental.macros

abstract class DbActionExtender[R](db: DatabaseDef, action: DBIOAction[R, NoStream, Nothing]) {
  // Action executer extension method for sync request.
  // This extension returns action value directly.
  def Save(): R = {
    try {
      Await.result(db.run(action), Duration.Inf)
    } catch {
      case e: Exception => {
        println(e.getMessage)
        throw new RuntimeException(e.getMessage)
      }
    }
  }

  def Save(default: R): R = {
    try {
      Await.result(db.run(action), Duration.Inf)
    } catch {
      case e: Exception => {
        println(e.getMessage)
        return default
      }
    }
  }

  // Query executer extension method for async request.
  // This extension doesnt returns query value with callback.
  def SaveAsync(): Future[R] = {
    try {
      return db.run(action)
    } catch {
      case e: Exception => {
          println(e.getMessage)
          throw new RuntimeException(e.getMessage)
      }
    }
  }

  def SaveAsync(default: R): Future[R] = {
    try {
      return db.run(action)
    } catch {
      case e: Exception => {
        println(e.getMessage)
        return Future.successful(default)
      }
    }
  }
}
