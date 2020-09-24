package com.limitra.sdk.database.mysql

import com.limitra.sdk.core._
import slick.jdbc.MySQLProfile.api._
import slick.jdbc.MySQLProfile.backend.DatabaseDef
import slick.lifted.{CanBeQueryCondition, Query, Rep}

import scala.language.experimental.macros
import scala.reflect.ClassTag

protected abstract class DbQueryExtender[+T, E](db: DatabaseDef, query: Query[T, E, Seq]) {
  // Implicit extension methods for DBIO
  implicit class ActionExtender[R](action: DBIOAction[R, NoStream, Nothing]) extends DbActionExtender[R](db, action)

  // Generates result of query for Seq[E]
  def toSeq: Seq[E] = {
    this.query.result.save
  }

  // Generates result of query for E
  def first: E = {
    this.firstOption.getOrElse(null.asInstanceOf[E])
  }

  // Generates result of query for Option[E]
  def firstOption: Option[E] = {
    this.query.result.headOption.save
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
