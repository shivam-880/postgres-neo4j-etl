package com.iamsmkr.imdb.persistence.read.repositories.alsoknownfor

import com.iamsmkr.imdb.persistence.Entities._
import slick.jdbc.PostgresProfile.api._

class AlsoKnownForTableDef(tag: Tag) extends Table[AlsoKnownForEntity](tag, "also_known_for") {

  def movieId = column[String]("movieid")

  def personId = column[String]("personid")

  override def * = (movieId, personId).mapTo[AlsoKnownForEntity]
}

object AlsoKnownForTableDef {
  lazy val alsoKnownFors = TableQuery[AlsoKnownForTableDef]
}
