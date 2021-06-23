package com.iamsmkr.imdb.persistence.read.repositories.alsoknownfor

import com.iamsmkr.imdb.persistence.Entities._
import com.iamsmkr.imdb.persistence.read.repositories.alsoknownfor.AlsoKnownForTableDef._
import slick.basic.DatabasePublisher
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.{ResultSetConcurrency, ResultSetType}

trait AlsoKnownForDao {
  def alsoKnownForsPublisher: DatabasePublisher[AlsoKnownForEntity]
}

class AlsoKnownForDaoImpl(db: Database) extends AlsoKnownForDao {
  def alsoKnownForsPublisher: DatabasePublisher[AlsoKnownForEntity] =
    db.stream(
      alsoKnownFors
        .result
        .withStatementParameters(
          rsType = ResultSetType.ForwardOnly,
          rsConcurrency = ResultSetConcurrency.ReadOnly,
          fetchSize = 10000)
        .transactionally
    )
}

object AlsoKnownForDaoImpl {
  def apply(db: Database): AlsoKnownForDao = new AlsoKnownForDaoImpl(db)
}
