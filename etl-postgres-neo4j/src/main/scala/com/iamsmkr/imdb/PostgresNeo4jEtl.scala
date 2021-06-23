package com.iamsmkr.imdb

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.iamsmkr.imdb.persistence.Entities._
import com.iamsmkr.imdb.persistence.read.repositories.alsoknownfor._
import com.iamsmkr.imdb.persistence.read.repositories.consolidatedcrew._
import com.iamsmkr.imdb.persistence.read.repositories.movies._
import com.iamsmkr.imdb.persistence.read.repositories.people._
import com.iamsmkr.imdb.persistence.write.repositories.graph.Cyphers.Mutations.Create._
import com.typesafe.scalalogging.LazyLogging
import org.neo4j.driver.{Driver, GraphDatabase}
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationInt}
import scala.util.{Failure, Success}

class PostgresNeo4jEtl extends LazyLogging {

  import PostgresNeo4jEtl._

  lazy val etlPeople: Future[Done] =
    Source.fromPublisher(peopleDao.peoplePublisher)
      .via(Flow[PersonEntity].mapAsync(8) { person =>
        val cypher = createPersonCypher(person)

        val session = driver.asyncSession()

        val f = session.writeTransactionAsync(tx =>
          tx.runAsync(cypher)
            .thenCompose(cursor => cursor.consumeAsync())
        ).whenComplete { (resultSummary, error) =>
          if (error != null) error.printStackTrace()
          else logger.debug(logMsg.format("etlPeople", resultSummary.counters().nodesCreated(), cypher))
          session.closeAsync()
        }

        scala.compat.java8.FutureConverters.toScala(f)
      })
      .runWith(Sink.ignore)

  lazy val etlMovies: Future[Done] =
    Source.fromPublisher(moviesDao.moviesPublisher)
      .via(Flow[MovieEntity].mapAsync(8) { movie =>
        val cypher = createMovieCypher(movie)

        val session = driver.asyncSession()

        val f = session.writeTransactionAsync(tx =>
          tx.runAsync(cypher)
            .thenCompose(cursor => cursor.consumeAsync())
        ).whenComplete { (resultSummary, error) =>
          if (error != null) error.printStackTrace()
          else logger.debug(logMsg.format("etlMovies", resultSummary.counters().nodesCreated(), cypher))
          session.closeAsync()
        }

        scala.compat.java8.FutureConverters.toScala(f)
      })
      .runWith(Sink.ignore)

  lazy val etlRoleRelationships: Future[Done] =
    Source.fromPublisher(consolidatedCrewDao.consolidatedCrewsPublisher)
      .via(Flow[ConsolidatedCrewEntity].mapAsync(8) { role =>
        val cypher = createRoleRelationshipQuery(role)

        val session = driver.asyncSession()

        val f = session.writeTransactionAsync(tx =>
          tx.runAsync(cypher)
            .thenCompose(cursor => cursor.consumeAsync())
        ).whenComplete { (resultSummary, error) =>
          if (error != null) error.printStackTrace()
          else logger.debug(logMsg.format("etlRoleRelationships", resultSummary.counters().nodesCreated(), cypher))
          session.closeAsync()
        }

        scala.compat.java8.FutureConverters.toScala(f)
      })
      .runWith(Sink.ignore)

  lazy val etlAlsoKnownForRelationships: Future[Done] =
    Source.fromPublisher(alsoKnownForDao.alsoKnownForsPublisher)
      .via(Flow[AlsoKnownForEntity].mapAsync(8) { alsoKnownFor =>
        val cypher = createAlsoKnownForRelationshipQuery(alsoKnownFor)

        val session = driver.asyncSession()

        val f = session.writeTransactionAsync(tx =>
          tx.runAsync(cypher)
            .thenCompose(cursor => cursor.consumeAsync())
        ).whenComplete { (resultSummary, error) =>
          if (error != null) error.printStackTrace()
          else logger.debug(logMsg.format("etlAlsoKnownForRelationships", resultSummary.counters().nodesCreated(), cypher))
          session.closeAsync()
        }

        scala.compat.java8.FutureConverters.toScala(f)
      })
      .runWith(Sink.ignore)

  logger.info("ETL Started...")

  (for {
    _ <- Future.sequence(Seq(etlPeople, etlMovies))
    _ <- Future.sequence(Seq(etlRoleRelationships, etlAlsoKnownForRelationships))
  } yield {
    logger.info("ETL Completed.")
  }).onComplete {
    case Success(_) => System.exit(0)
    case Failure(e) => e.printStackTrace()
      System.exit(0)
  }

  Await.result(system.whenTerminated, Duration.Inf)
}

object PostgresNeo4jEtl {
  val db: JdbcBackend.Database = Database.forConfig("postgres", appConf)
  val driver: Driver = GraphDatabase.driver(neo4jUri)

  val peopleDao: PeopleDao = PeopleDaoImpl(db)
  val moviesDao: MoviesDao = MoviesDaoImpl(db)
  val consolidatedCrewDao: ConsolidatedCrewDao = ConsolidatedCrewDaoImpl(db)
  val alsoKnownForDao: AlsoKnownForDao = AlsoKnownForDaoImpl(db)

  implicit val system: ActorSystem = ActorSystem("PostgresNeo4jEtl")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  sys.addShutdownHook {
    driver.close()
    db.close()
    Await.result(system.terminate(), 1.second)
  }

  def main(args: Array[String]): Unit = new PostgresNeo4jEtl()
}
