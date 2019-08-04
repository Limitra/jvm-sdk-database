package com.limitra.sdk.database.mysql

import slick.jdbc.MySQLProfile.api._

import scala.language.experimental.macros

/**
  * That enables us to use the DbBoxQuery class as an TableQuery.
  * In this way we can add new features without disabling any features.
 */
protected abstract class DbBoxQuery[E <: BaseBox, T <: BaseBoxTable[E]](query: TableQuery[T], cons: Tag => T) extends TableQuery[T](cons) {
  val TableName = this.baseTableRow.tableName
  val Query = query
}
