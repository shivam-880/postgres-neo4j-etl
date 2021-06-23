package com.iamsmkr.imdb.persistence.read.repositories.movies

import com.iamsmkr.imdb.persistence.Entities._
import com.iamsmkr.imdb.persistence.read.repositories.movies.MoviesTableDef._
import slick.basic.DatabasePublisher
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.{ResultSetConcurrency, ResultSetType}

trait MoviesDao {
  def moviesPublisher: DatabasePublisher[MovieEntity]
}

class MoviesDaoImpl(db: Database) extends MoviesDao {
  def moviesPublisher: DatabasePublisher[MovieEntity] =
    db.stream(
      movies
        .result
        .withStatementParameters(
          rsType = ResultSetType.ForwardOnly,
          rsConcurrency = ResultSetConcurrency.ReadOnly,
          fetchSize = 10000)
        .transactionally
    )
}

object MoviesDaoImpl {
  def apply(db: Database): MoviesDao = new MoviesDaoImpl(db)
}
