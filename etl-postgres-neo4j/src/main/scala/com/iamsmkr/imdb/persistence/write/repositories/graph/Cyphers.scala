package com.iamsmkr.imdb.persistence.write.repositories.graph

import com.iamsmkr.imdb.persistence.Entities._

object Cyphers {
  def prettify(roles: List[String]): List[String] =
    roles.map { role =>
      role.split("_").map { word =>
        word.head.toUpper + word.tail
      }.mkString(" ")
    }

  object Mutations {

    object Create {
      def createPersonCypher(person: PersonEntity): String = {
        lazy val personId = person.personId
        lazy val name = person.name
        lazy val professions = prettify(person.professions.get).mkString(",")
        lazy val birthYear = person.birthYear.get
        lazy val deathYear = person.deathYear.get

        s"""
           |MERGE (p:Person {name: "$name"})
           |    ON CREATE SET p.personId="$personId"${
                  if (person.professions.isDefined && person.professions.get.exists(_.length > 0))
                    s""",p.professions=split(coalesce("$professions",""), ",")""" else ""
                }
           |${
              if (person.birthYear.isDefined) {
                s"""
                   |MERGE (y1:Year {year: "$birthYear"})
                   |MERGE (p)-[:BORN_IN]->(y1)
                   |""".stripMargin
              } else ""
            }
           |${
              if (person.deathYear.isDefined) {
                s"""
                   |MERGE (y2:Year {year: $deathYear})
                   |MERGE (p)-[:DIED_IN]->(y2)
                   |""".stripMargin
              } else ""
            }
           |""".stripMargin
      }

      def createMovieCypher(movie: MovieEntity): String = {
        lazy val movieId = movie.movieId
        lazy val title = movie.title
        lazy val isAdult = movie.isAdult
        lazy val releasedYear = movie.releasedYear.get
        lazy val runtimeInMinutes = movie.runtimeInMinutes.get
        lazy val genres = movie.genres.get
        lazy val avgRating = movie.avgRating.get
        lazy val numVotes = movie.numVotes.get

        s"""
           |MERGE (m:Movie {title: "$title"})
           |    ON CREATE SET m.movieId="$movieId",m.adult=$isAdult${
                  if (movie.runtimeInMinutes.isDefined) s",m.runtime=$runtimeInMinutes" else ""
                }${
                  if(movie.avgRating.isDefined) s",m.avgRating=$avgRating" else ""
                }${
                  if(movie.numVotes.isDefined) s",m.numVotes=$numVotes" else ""
                }
           |${
              if (movie.releasedYear.isDefined) {
                s"""
                   |MERGE (y:Year {year: $releasedYear})
                   |MERGE (m)-[:RELEASED_IN]->(y)
                   |""".stripMargin
              } else ""
            }
           |${
              if (movie.genres.isDefined && movie.genres.get.nonEmpty) {
                genres.zipWithIndex.map { case (genre, i) =>
                  s"""
                     |MERGE (g$i:Genre {type: "$genre"})
                     |MERGE (m)-[:IN_GENRE]->(g$i)
                     |""".stripMargin
                }.mkString("\n")
              } else ""
            }
           |""".stripMargin
      }

      def createRoleRelationshipQuery(rel: ConsolidatedCrewEntity): String = {
        lazy val movieId = rel.movieId
        lazy val personId = rel.personId
        lazy val role = s"${rel.role.toUpperCase}_IN"
        lazy val job = prettify(List(rel.job.get)).head
        lazy val characters = rel.characters.get.mkString(",")

        s"""
           |MATCH (p:Person {personId: "$personId"})
           |MATCH (m:Movie {movieId: "$movieId"})
           |MERGE (p)-[c:$role]->(m)
           |${
              val r = List(
                if (rel.job.isDefined) s"""c.job="$job"""" else "",
                if (rel.characters.isDefined && rel.characters.get.exists(_.length > 0))
                  s"""c.characters=split(coalesce("$characters",""), ",")""" else ""
              ).filterNot(_.length==0)
              if (r.nonEmpty) {
                s"""
                  |ON CREATE SET ${r.mkString(",")}
                  |""".stripMargin
              } else ""
            }
           |""".stripMargin
      }

      def createAlsoKnownForRelationshipQuery(rel: AlsoKnownForEntity): String = {
        lazy val movieId = rel.movieId
        lazy val personId = rel.personId

        s"""
           |MATCH (p:Person {personId: "$personId"})
           |MATCH (m:Movie {movieId: "$movieId"})
           |MERGE (p)-[:ALSO_KNOWN_FOR]->(m)
           |""".stripMargin
      }
    }

  }

}
