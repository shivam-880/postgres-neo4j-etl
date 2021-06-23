package com.iamsmkr.imdb.persistence.read.repositories.consolidatedcrew

import com.iamsmkr.imdb.persistence.Entities._
import slick.jdbc.PostgresProfile.api._
import com.iamsmkr.imdb.persistence.read.repositories.ColumnTypeImplicits._

class ConsolidatedCrewTableDef(tag: Tag) extends Table[ConsolidatedCrewEntity](tag, "consolidated_crew") {

  def movieId = column[String]("movieid")

  def personId = column[String]("personid")

  def role = column[String]("role")

  def job = column[Option[String]]("job")

  def characters = column[Option[List[String]]]("characters")

  override def * =
    (movieId, personId, role, job, characters).mapTo[ConsolidatedCrewEntity]
}

object ConsolidatedCrewTableDef {
  lazy val consolidatedCrews = TableQuery[ConsolidatedCrewTableDef]
}
