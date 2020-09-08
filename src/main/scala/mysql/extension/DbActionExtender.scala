package com.limitra.sdk.database.mysql

import slick.jdbc.MySQLProfile.api._
import slick.jdbc.MySQLProfile.backend.DatabaseDef

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.experimental.macros

abstract class DbActionExtender[R](db: DatabaseDef, action: DBIOAction[R, NoStream, Nothing]) {
  // This extension returns action value directly.
  def save(): R = {
    try {
      Await.result(db.run(action), Duration.Inf)
    } catch {
      case e: Exception => {
        println(e.getMessage)
        throw new RuntimeException(e.getMessage)
      }
    }
  }

  def save(default: R): R = {
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
  def saveAsync(): Future[R] = {
    try {
      return db.run(action)
    } catch {
      case e: Exception => {
          println(e.getMessage)
          throw new RuntimeException(e.getMessage)
      }
    }
  }

  def saveAsync(default: R): Future[R] = {
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
