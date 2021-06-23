package com.iamsmkr.imdb.persistence.read.repositories.movies

import com.iamsmkr.imdb.persistence.Entities._
import slick.jdbc.PostgresProfile.api._
import com.iamsmkr.imdb.persistence.read.repositories.ColumnTypeImplicits._

class MoviesTableDef(tag: Tag) extends Table[MovieEntity](tag, "movies") {

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
  lazy val movies = TableQuery[MoviesTableDef]
}
