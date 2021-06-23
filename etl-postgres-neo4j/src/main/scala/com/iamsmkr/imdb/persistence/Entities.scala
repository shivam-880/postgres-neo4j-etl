package com.iamsmkr.imdb.persistence

object Entities {

  case class PersonEntity(personId: String, name: String, birthYear: Option[Int], deathYear: Option[Int], professions: Option[List[String]])

  case class AlsoKnownForEntity(movieId: String, personId: String)

  case class ConsolidatedCrewEntity(movieId: String, personId: String, role: String, job: Option[String], characters: Option[List[String]])

  case class MovieEntity(movieId: String, title: String, isAdult: Boolean, releasedYear: Option[Int], runtimeInMinutes: Option[Int],
                         genres: Option[List[String]], avgRating: Option[Double], numVotes: Option[Int])

}
