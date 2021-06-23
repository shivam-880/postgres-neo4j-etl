package com.iamsmkr.imdb.persistence.read.repositories.movies

import com.iamsmkr.imdb.etlMinimal
import com.iamsmkr.imdb.persistence.Entities._
import slick.jdbc.PostgresProfile.api._
import com.iamsmkr.imdb.persistence.read.repositories.ColumnTypeImplicits._
import com.iamsmkr.imdb.persistence.read.repositories.movies.MoviesTableDef.tblName

class MoviesTableDef(tag: Tag) extends Table[MovieEntity](tag, tblName) {

  def movieId = column[String]("movieid")

  def title = column[String]("title")

  def isAdult = column[Boolean]("isadult")

  def releasedYear = column[Option[Int]]("releasedyear")

  def runtimeInMinutes = column[Option[Int]]("runtimeminutes")

  def genres = column[Option[List[String]]]("genres")

  def avgRating = column[Option[Double]]("averagerating")

  def numVotes = column[Option[Int]]("numvotes")

  override def * =
    (movieId, title, isAdult, releasedYear, runtimeInMinutes, genres, avgRating, numVotes).mapTo[MovieEntity]
}

object MoviesTableDef {
  private final lazy val tblName = if (etlMinimal) "movies_minimal" else "movies"
  lazy val movies = TableQuery[MoviesTableDef]
}
