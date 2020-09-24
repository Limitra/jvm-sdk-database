package com.limitra.sdk.database.mysql

import slick.ast.Node
import slick.lifted._

import scala.language.experimental.macros

/**
  * Table initializer's object applier for usage of extended queries.
  * Example: DbBox[T#TableElementType, T] =>
  *       ExampleTable extends BaseBoxTable,
  *       Example extends BaseBox =>
  *       DbBox[Example, ExampleTable]
  */
object DbBox {
  def apply[E <: BaseBox, T <: BaseBoxTable[E]]: DbBox[E, T] = macro _apply[E, T]

  def apply[E <: BaseBox, T <: BaseBoxTable[E]](cons: Tag => T): DbBox[E, T] = new DbBox[E, T](cons)

  def _apply[E <: BaseBox, T <: BaseBoxTable[E]](c: Context)(implicit t: c.WeakTypeTag[T], e: c.WeakTypeTag[E]): c.Expr[DbBox[E, T]] = {
    val cons = c.Expr[Tag => T](Function(
      List(ValDef(Modifiers(Flag.PARAM), TermName("tag"), Ident(typeOf[Tag].typeSymbol), EmptyTree)),
      Apply(
        Select(New(TypeTree(t.tpe)), termNames.CONSTRUCTOR),
        List(Ident(TermName("tag")))
      )
    ))
    reify { DbBox.apply[E, T](cons.splice) }
  }
}

/**
  * Table initializer class for usage extended queries.
  */
class DbBox[E <: BaseBox, T <: BaseBoxTable[E]](cons: Tag => T) extends DbBoxQuery[E, T](new TableQuery[T](cons), cons) {
  lazy override val shaped = {
    val baseTable = cons(new BaseTag { base =>
      def taggedAs(path: Node): AbstractTable[_] = cons(new RefTag(path) {
        def taggedAs(path: Node) = base.taggedAs(path)
      })
    })
    ShapedValue(baseTable, RepShape[FlatShapeLevel, T, T#TableElementType])
  }

  lazy override val toNode = shaped.toNode
}
