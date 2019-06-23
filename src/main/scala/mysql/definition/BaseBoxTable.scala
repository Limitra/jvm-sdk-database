package com.limitra.sdk.database.mysql

import slick.jdbc.MySQLProfile.api._
import slick.lifted.{Rep, Tag}

import scala.language.experimental.macros
import scala.reflect.ClassTag

// Base table definition for db entity types
/** Table name options:
    1- Default table name: Table definition class name. Ex: For -> class ProductTable -> Default table name: "Product"
    2- Customized table names. Ex: "Product", "Category", "ProductCategory" etc.
  */
abstract class BaseBoxTable[E <: BaseBox: ClassTag](tag: Tag, table: String = "")
  extends Table[E](tag, if(table.isEmpty) classManifest[E].erasure.getName.split('.').last.replace("Table", "") else table) {
  def ID: Rep[Long] = column[Long]("ID", O.PrimaryKey, O.AutoInc)
}

