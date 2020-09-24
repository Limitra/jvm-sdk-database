package com.limitra.sdk.database.mysql

import slick.jdbc.MySQLProfile.api._
import slick.jdbc.MySQLProfile.backend.DatabaseDef
import slick.lifted.{CanBeQueryCondition, Query, Rep}

import scala.concurrent.Future
import scala.language.experimental.macros

protected abstract class DbQueryExtender[+T, E](db: DatabaseDef, query: Query[T, E, Seq]) {
  // Implicit extension methods for DBIO
  implicit class ActionExtender[R](action: DBIOAction[R, NoStream, Nothing]) extends DbActionExtender[R](db, action)

  // Generates result of query for Seq[E]
  def toSeq: Seq[E] = {
    this.query.result.save
  }

  // Generates result of query for E
  def first: E = {
    this.query.result.head.save
  }

  // Generates result of query for Option[E]
  def firstOption: Option[E] = {
    this.query.result.headOption.save
  }

  // Generates result of query for E async
  def firstAsync: Future[E] = {
    this.query.result.head.saveAsync
  }

  // Generates result of query for Option[E] async
  def firstOptionAsync: Future[Option[E]] = {
    this.query.result.headOption.saveAsync
  }

  // Generates query of any operation for Rep[Boolean]
  def any[Q <: Rep[_]](expr: T => Q)(implicit wt: CanBeQueryCondition[Q]): Rep[Boolean] = {
    this.query.filter(expr).length > 0
  }

  // Generates query of count operation
  def count: Rep[Int] = {
    this.query.length
  }
}
