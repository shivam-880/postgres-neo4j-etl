package com.iamsmkr.imdb.persistence.read.repositories.consolidatedcrew

import com.iamsmkr.imdb.etlMinimal
import com.iamsmkr.imdb.persistence.Entities._
import slick.jdbc.PostgresProfile.api._
import com.iamsmkr.imdb.persistence.read.repositories.ColumnTypeImplicits._
import com.iamsmkr.imdb.persistence.read.repositories.consolidatedcrew.ConsolidatedCrewTableDef.tblName

class ConsolidatedCrewTableDef(tag: Tag) extends Table[ConsolidatedCrewEntity](tag, tblName) {

  def movieId = column[String]("movieid")

  def personId = column[String]("personid")

  def role = column[String]("role")

  def job = column[Option[String]]("job")

  def characters = column[Option[List[String]]]("characters")

  override def * =
    (movieId, personId, role, job, characters).mapTo[ConsolidatedCrewEntity]
}

object ConsolidatedCrewTableDef {
  private final lazy val tblName = if (etlMinimal) "consolidated_crew_minimal" else "consolidated_crew"
  lazy val consolidatedCrews = TableQuery[ConsolidatedCrewTableDef]
}
