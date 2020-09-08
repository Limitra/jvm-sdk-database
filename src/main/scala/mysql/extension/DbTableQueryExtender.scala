package com.limitra.sdk.database.mysql

import slick.jdbc.MySQLProfile.api._
import slick.jdbc.MySQLProfile.backend.DatabaseDef
import slick.lifted.CanBeQueryCondition

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.experimental.macros
import scala.reflect.ClassTag

protected abstract class DbTableQueryExtender[E <: BaseBox, T <: BaseBoxTable[E]](db: DatabaseDef, query: DbBoxQuery[E, T]) {
  // Implicit extension methods for DBIO
  implicit class ActionExtender[R: ClassTag](action: DBIOAction[R, NoStream, Nothing]) extends DbActionExtender[R](db, action)

  // Generates customized query action for auto increment reset operation
  private def _autoIncRes(incStart: Long): DBIOAction[Int, NoStream, Nothing] = {
    val tableName = this.query.TableName
    return sqlu"""ALTER TABLE `#$tableName` AUTO_INCREMENT = #$incStart;"""
  }

  // Generates customized query action for insert operation
  private def _insert(entity: E, incStart: Long) = {
    val insert = (this.query returning this.query.map(_.ID)) += entity
    for {
      autoInc <- this._autoIncRes(incStart)
      insert <- insert
    } yield insert
  }

  // Runs customized insert action sync
  def insert(entity: E, incStart: Long = 1): Long = {
    this._insert(entity, incStart).save
  }

  // Runs customized insert action async
  def insertAsync(entity: E, incStart: Long = 1) = {
    this._insert(entity, incStart).saveAsync
  }

  // Generates customized query action for bulk insert operation
  private def _bulkInsert(entities: Seq[E], incStart: Long) = {
    val bulkInsert = (this.query returning this.query.map(_.ID)) ++= entities

    for {
      autoInc <- this._autoIncRes(incStart)
      insert <- bulkInsert
    } yield insert
  }

  // Runs customized bulk insert action sync
  def bulkInsert(entities: Seq[E], incStart: Long = 1): Seq[Long] = {
    this._bulkInsert(entities, incStart).save
  }

  // Runs customized bulk insert action async
  def bulkInsertAsync(entities: Seq[E], incStart: Long = 1) = {
    this._bulkInsert(entities, incStart).saveAsync
  }

  // Generates customized query action for update operation
  private def _update(entity: E) = {
    this.query.filter(_.ID === entity.ID).update(entity)
  }

  // Runs customized update action sync
  def update(entity: E): Int = {
    this._update(entity).save
  }

  // Runs customized update action async
  def updateAsync(entity: E) = {
    this._update(entity).saveAsync
  }

  // Generates customized query action for delete by id operation
  private def _delete(id: Long) = {
    this.query.filter(_.ID === id).delete
  }

  // Runs customized delete by id action sync
  def delete(id: Long): Int = {
    this._delete(id).save(0)
  }

  // Runs customized delete by id action sync
  def deleteAsync(id: Long) = {
    this._delete(id).saveAsync(0)
  }

  // Generates customized query action for delete by expression operation
  private def _delete[Q <: Rep[_]](expr: T => Q)(implicit wt: CanBeQueryCondition[Q]) = {
    this.query.filter(expr).delete
  }

  // Runs customized delete by expression action sync
  def delete[Q <: Rep[_]](expr: T => Q)(implicit wt: CanBeQueryCondition[Q]) = {
    this._delete(expr).save(0)
  }

  // Runs customized delete by expression action sync
  def deleteAsync[Q <: Rep[_]](expr: T => Q)(implicit wt: CanBeQueryCondition[Q]) = {
    this._delete(expr).saveAsync(0)
  }

  // Generates customized query action for delete all operation
  private def _deleteAll = {
    this.query.delete
  }

  // Runs customized delete all action sync
  def deleteAll: Int = {
    this._deleteAll.save(0)
  }

  // Runs customized delete all action sync
  def deleteAllAsync(callback: (Int) => Unit = null) = {
    this._deleteAll.saveAsync(0)
  }
}
