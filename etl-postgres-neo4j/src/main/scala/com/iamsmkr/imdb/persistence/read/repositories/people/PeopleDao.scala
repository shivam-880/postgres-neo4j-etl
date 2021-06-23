package com.iamsmkr.imdb.persistence.read.repositories.people

import com.iamsmkr.imdb.persistence.Entities.PersonEntity
import com.iamsmkr.imdb.persistence.read.repositories.people.PeopleTableDef._
import slick.basic.DatabasePublisher
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.{ResultSetConcurrency, ResultSetType}

trait PeopleDao {
  def peoplePublisher: DatabasePublisher[PersonEntity]
}

class PeopleDaoImpl(db: Database) extends PeopleDao {
  def peoplePublisher: DatabasePublisher[PersonEntity] =
    db.stream(
      people
        .result
        .withStatementParameters(
          rsType = ResultSetType.ForwardOnly,
          rsConcurrency = ResultSetConcurrency.ReadOnly,
          fetchSize = 10000)
        .transactionally
    )
}

object PeopleDaoImpl {
  def apply(db: Database): PeopleDao = new PeopleDaoImpl(db)
}
