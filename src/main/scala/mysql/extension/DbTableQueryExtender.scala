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
    return sqlu"""ALTER TABLE #$tableName AUTO_INCREMENT = #$incStart;"""
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
  def Insert(entity: E, incStart: Long = 1): Long = {
    this._insert(entity, incStart).Save
  }

  // Runs customized insert action async
  def InsertAsync(entity: E, incStart: Long = 1) = {
    this._insert(entity, incStart).SaveAsync
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
  def BulkInsert(entities: Seq[E], incStart: Long = 1): Seq[Long] = {
    this._bulkInsert(entities, incStart).Save
  }

  // Runs customized bulk insert action async
  def BulkInsertAsync(entities: Seq[E], incStart: Long = 1) = {
    this._bulkInsert(entities, incStart).SaveAsync
  }

  // Generates customized query action for update operation
  private def _update(entity: E) = {
    this.query.filter(_.ID === entity.ID).update(entity)
  }

  // Runs customized update action sync
  def Update(entity: E): Int = {
    this._update(entity).Save
  }

  // Runs customized update action async
  def UpdateAsync(entity: E) = {
    this._update(entity).SaveAsync
  }

  // Generates customized query action for delete by id operation
  private def _delete(id: Long) = {
    this.query.filter(_.ID === id).delete
  }

  // Runs customized delete by id action sync
  def Delete(id: Long): Int = {
    this._delete(id).Save(0)
  }

  // Runs customized delete by id action sync
  def DeleteAsync(id: Long) = {
    this._delete(id).SaveAsync(0)
  }

  // Generates customized query action for delete by expression operation
  private def _delete[Q <: Rep[_]](expr: T => Q)(implicit wt: CanBeQueryCondition[Q]) = {
    this.query.filter(expr).delete
  }

  // Runs customized delete by expression action sync
  def Delete[Q <: Rep[_]](expr: T => Q)(implicit wt: CanBeQueryCondition[Q]) = {
    this._delete(expr).Save(0)
  }

  // Runs customized delete by expression action sync
  def DeleteAsync[Q <: Rep[_]](expr: T => Q)(implicit wt: CanBeQueryCondition[Q]) = {
    this._delete(expr).SaveAsync(0)
  }

  // Generates customized query action for delete all operation
  private def _deleteAll = {
    this.query.delete
  }

  // Runs customized delete all action sync
  def DeleteAll: Int = {
    this._deleteAll.Save(0)
  }

  // Runs customized delete all action sync
  def DeleteAllAsync(callback: (Int) => Unit = null) = {
    this._deleteAll.SaveAsync(0)
  }
}
