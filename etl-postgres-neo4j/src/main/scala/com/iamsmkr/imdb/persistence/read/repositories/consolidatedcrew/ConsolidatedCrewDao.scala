package com.iamsmkr.imdb.persistence.read.repositories.consolidatedcrew

import com.iamsmkr.imdb.persistence.Entities._
import com.iamsmkr.imdb.persistence.read.repositories.consolidatedcrew.ConsolidatedCrewTableDef._
import slick.basic.DatabasePublisher
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.{ResultSetConcurrency, ResultSetType}

trait ConsolidatedCrewDao {
  def consolidatedCrewsPublisher: DatabasePublisher[ConsolidatedCrewEntity]
}

class ConsolidatedCrewDaoImpl(db: Database) extends ConsolidatedCrewDao {
  def consolidatedCrewsPublisher: DatabasePublisher[ConsolidatedCrewEntity] =
    db.stream(
      consolidatedCrews
        .result
        .withStatementParameters(
          rsType = ResultSetType.ForwardOnly,
          rsConcurrency = ResultSetConcurrency.ReadOnly,
          fetchSize = 10000)
        .transactionally
    )
}

object ConsolidatedCrewDaoImpl {
  def apply(db: Database): ConsolidatedCrewDao = new ConsolidatedCrewDaoImpl(db)
}
