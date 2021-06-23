package com.iamsmkr.imdb.persistence.read.repositories.people

import com.iamsmkr.imdb.persistence.Entities.PersonEntity
import slick.jdbc.PostgresProfile.api._
import com.iamsmkr.imdb.persistence.read.repositories.ColumnTypeImplicits._

class PeopleTableDef(tag: Tag) extends Table[PersonEntity](tag, "people") {

  def personId = column[String]("personid")

  def name = column[String]("name")

  def birthYear = column[Option[Int]]("birthyear")

  def deathYear = column[Option[Int]]("deathyear")

  def professions = column[Option[List[String]]]("professions")

  override def * = (personId, name, birthYear, deathYear, professions).mapTo[PersonEntity]
}

object PeopleTableDef {
  lazy val people = TableQuery[PeopleTableDef]
}
