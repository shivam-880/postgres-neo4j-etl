package com.iamsmkr.imdb.persistence.read.repositories.alsoknownfor

import com.iamsmkr.imdb.etlMinimal
import com.iamsmkr.imdb.persistence.Entities._
import com.iamsmkr.imdb.persistence.read.repositories.alsoknownfor.AlsoKnownForTableDef.tblName
import slick.jdbc.PostgresProfile.api._

class AlsoKnownForTableDef(tag: Tag) extends Table[AlsoKnownForEntity](tag, tblName) {

  def movieId = column[String]("movieid")

  def personId = column[String]("personid")

  override def * = (movieId, personId).mapTo[AlsoKnownForEntity]
}

object AlsoKnownForTableDef {
  private final lazy val tblName = if (etlMinimal) "also_known_for_minimal" else "also_known_for"
  lazy val alsoKnownFors = TableQuery[AlsoKnownForTableDef]
}
