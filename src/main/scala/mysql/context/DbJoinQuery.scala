package com.limitra.sdk.database.mysql

import slick.lifted.{BaseJoinQuery, CanBeQueryCondition, Query, Rep}

/**
  * We can link the DbJoinQuery class as an extension to the BaseJoinQuery class
  * So that we can derive the extensions of the joined queries.
  * Warn: The visible function here is not to disrupt the upper camel case.
  */
protected abstract class DbJoinQuery[+E1, +E2, U1, U2, C[_], +B1, +B2](query: BaseJoinQuery[E1, E2, U1, U2, C, B1, B2]) {
  // Generates mapped query for joined queries
  def On[T <: Rep[_]](pred: (B1, B2) => T)(implicit wt: CanBeQueryCondition[T]): Query[(E1, E2), (U1, U2), C] = {
    this.query.on(pred)
  }
}
