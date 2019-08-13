package com.limitra.sdk.database.mysql

import com.limitra.sdk.core._
import slick.ast.{Library, TypedType}
import slick.jdbc.MySQLProfile.api._
import slick.jdbc.MySQLProfile.backend.DatabaseDef
import slick.lifted.FunctionSymbolExtensionMethods._
import slick.lifted.{CanBeQueryCondition, FlatShapeLevel, OptionLift, Ordered, Query, Rep, Shape}

import scala.language.experimental.macros
import scala.reflect.ClassTag

protected abstract class DbQueryExtender[+T, E](db: DatabaseDef, query: Query[T, E, Seq]) {
  // Implicit extension methods for DBIO
  implicit class ActionExtender[R](action: DBIOAction[R, NoStream, Nothing]) extends DbActionExtender[R](db, action)
  // Generates customized query for filter
  def Filter[Q <: Rep[_]](expr: T => Q)(implicit wt: CanBeQueryCondition[Q]): Query[T, E, Seq] = {
    return this.query.filter(expr)
  }

  // Generates customized query for sort
  def SortBy[F](rule: T => F)(implicit ev: F => Ordered): Query[T, E, Seq] = {
    return this.query.sortBy(rule)
  }

  // Generates mapped query
  def Select[F, G, E](map: T => F)(implicit shape: Shape[_ <: FlatShapeLevel, F, E, G]): Query[G, E, Seq] = {
    return this.query.map(map)
  }

  // Generates join query for relations
  def Join[T2, E2, D[_]](rel: Query[T2, E2, D])= {
    this.query.join(rel)
  }

  // Generates join left query for relations
  def JoinLeft[T2, E2, D[_], O2](rel: Query[T2, _, D])(implicit ol: OptionLift[T2, O2], sh: Shape[FlatShapeLevel, O2, E2, _]) = {
    this.query.joinLeft(rel)
  }

  // Generates join right query for relations
  def JoinRight[T1 >: T, T2, E2, D[_], O1, E1](rel: Query[T2, E2, D])(implicit ol: OptionLift[T1, O1], sh: Shape[FlatShapeLevel, O1, E1, _]) = {
    this.query.joinRight[T1, T2, E2, D, O1, E1](rel)
  }

  // Generates join full query for relations
  def JoinFull[T1 >: T, T2, E2, D[_], O1, E1, O2](rel: Query[T2, _, D])(implicit ol1: OptionLift[T1, O1], sh1: Shape[FlatShapeLevel, O1, E1, _], ol2: OptionLift[T2, O2], sh2: Shape[FlatShapeLevel, O2, E2, _]) = {
    this.query.joinFull[T1, T2, E2, D, O1, E1, O2](rel)
  }

  // Generates result of query for Seq[E]
  def ToSeq: Seq[E] = {
    this.query.result.Save
  }

  // Generates mapped result of query for custom type C
  def ToRef[C](implicit tag: ClassTag[C]): Seq[C] = {
    var objSet: Seq[C] = Seq()

    this.query.result.Save.foreach(entity => {
      val reflected = Reflect.FromTo[C](entity)
      if(reflected.isDefined) {
        objSet = objSet :+ reflected.get
      }
    })
    return objSet
  }

  // Generates result of query for E
  def First: E = {
    this.FirstOption.getOrElse(null.asInstanceOf[E])
  }

  // Generates result of query for Option[E]
  def FirstOption: Option[E] = {
    this.query.result.headOption.Save
  }

  // Generates result value of has operation for Boolean
  def AnyVal[Q <: Rep[_]](expr: T => Q)(implicit wt: CanBeQueryCondition[Q]): Boolean = {
    return (this.query.filter(expr).length > 0).result.Save
  }

  // Generates query of any operation for Rep[Boolean]
  def Any[Q <: Rep[_]](expr: T => Q)(implicit wt: CanBeQueryCondition[Q]): Rep[Boolean] = {
    this.query.filter(expr).length > 0
  }

  // Generates result value of count operation
  def CountVal: Long = {
    return query.length.result.Save
  }

  // Generates query of count operation
  def Count: Rep[Long] = {
    return query.length.asInstanceOf[Long]
  }

  // Generates result value of min operation
  def MinVal[F, G, E](map: T => Rep[F])(implicit tm: TypedType[F], shape: Shape[_ <: FlatShapeLevel, Rep[F], E, G]): F = {
    return Library.Min.column[Option[F]](this.query.map(map).toNode).result.Save.getOrElse(null.asInstanceOf[F])
  }

  // Generates query of min operation
  def Min[F, G, E](map: T => Rep[F])(implicit tm: TypedType[F], shape: Shape[_ <: FlatShapeLevel, Rep[F], E, G]): Rep[F] = {
    return Library.Min.column[F](this.query.map(map).toNode)
  }

  // Generates result value of max operation
  def MaxVal[F, G, E](map: T => Rep[F])(implicit tm: TypedType[F], shape: Shape[_ <: FlatShapeLevel, Rep[F], E, G]): F = {
    return Library.Max.column[Option[F]](this.query.map(map).toNode).result.Save.getOrElse(null.asInstanceOf[F])
  }

  // Generates query of max operation
  def Max[F, G, E](map: T => Rep[F])(implicit tm: TypedType[F], shape: Shape[_ <: FlatShapeLevel, Rep[F], E, G]): Rep[F] = {
    return Library.Max.column[F](this.query.map(map).toNode)
  }

  // Generates result value of avg operation
  def AvgVal[F, G, E](map: T => Rep[F])(implicit tm: TypedType[F], shape: Shape[_ <: FlatShapeLevel, Rep[F], E, G]): F = {
    return Library.Avg.column[Option[F]](this.query.map(map).toNode).result.Save.getOrElse(null.asInstanceOf[F])
  }

  // Generates query of avg operation
  def Avg[F, G, E](map: T => Rep[F])(implicit tm: TypedType[F], shape: Shape[_ <: FlatShapeLevel, Rep[F], E, G]): Rep[F] = {
    return Library.Avg.column[F](this.query.map(map).toNode)
  }

  // Generates result value of sum operation
  def SumVal[F, G, E](map: T => Rep[F])(implicit tm: TypedType[F], shape: Shape[_ <: FlatShapeLevel, Rep[F], E, G]): F = {
    return Library.Sum.column[Option[F]](this.query.map(map).toNode).result.Save.getOrElse(null.asInstanceOf[F])
  }

  // Generates query of sum operation
  def Sum[F, G, E](map: T => Rep[F])(implicit tm: TypedType[F], shape: Shape[_ <: FlatShapeLevel, Rep[F], E, G]): Rep[F] = {
    return Library.Sum.column[F](this.query.map(map).toNode)
  }

  // Create result for queries
  def Execute = {
    this.query.result.Save
  }

  // Create result for async queries
  def ExecuteAsync = {
    this.query.result.SaveAsync
  }
}
