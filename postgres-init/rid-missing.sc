#!/usr/local/bin/amm

import $ivy.`com.nrinaudo::kantan.csv:0.6.1`
import $ivy.`com.nrinaudo::kantan.csv-generic:0.6.1`

import kantan.csv._
import kantan.csv.generic._
import kantan.csv.ops._
import scala.io.Source
import java.io.File

case class TitleCrew(tconst: String, directors: String, writers: String)

case class TitlePrincipal(tconst: String, ordering: Int, nconst: String, category: String, job: String, characters: String)

case class TitleRating(tconst: String, averageRating: String, numVotes: String)

case class NameBasic(nconst: String, primaryName: String, birthYear: String, deathYear: String, primaryProfession: String, knownForTitles: String)

lazy val names: Set[String] = {
  println("  Fetching names...")
  val source = Source.fromFile(new File("only_names.fk"))
  try source.getLines.toSet finally source.close
}

lazy val titles: Set[String] = {
  println("  Fetching titles...")
  val source = Source.fromFile(new File("only_titles.fk"))
  try source.getLines.toSet finally source.close
}

lazy val principals: Set[String] = {
  println("  Fetching principals...")
  val source = Source.fromFile(new File("only_principals.fk"))
  try source.getLines.toSet finally source.close
}

def ridMissing[T: HeaderDecoder : HeaderEncoder](input: File, output: File, header: Seq[String])(predicate: T => Boolean): Unit = {
  val reader = input.asCsvReader[T](rfc.withHeader.withCellSeparator('\t'))
  val cleaned = reader.filterResult(predicate).toIterator.collect {
    case Right(value) => value
  }

  output.writeCsv[T](cleaned, rfc.withHeader(header: _*).withCellSeparator('\t'))
  println(s"  Finished cleaning [${input.getName}]")
}

@main
def ridMissingCrew(): Unit = {
  val crew = new File("title.crew.tsv")
  val output = new File("title.crew.cleaned")
  val header = Seq("tconst", "directors", "writers")

  ridMissing[TitleCrew](crew, output, header) { crew =>
    titles.contains(crew.tconst)
  }
}

@main
def ridMissingPrincipals(): Unit = {
  val principals = new File("title.principals.tsv")
  val output = new File("title.principals.cleaned")
  val header = Seq("tconst", "ordering", "nconst", "category", "job", "characters")

  ridMissing[TitlePrincipal](principals, output, header) { principal =>
    names.contains(principal.nconst) && titles.contains(principal.tconst)
  }
}

@main
def ridMissingRatings(): Unit = {
  val ratings = new File("title.ratings.tsv")
  val output = new File("title.ratings.cleaned")
  val header = Seq("tconst", "averageRating", "numVotes")

  ridMissing[TitleRating](ratings, output, header) { rating =>
    titles.contains(rating.tconst)
  }
}

@main
def ridUnnecessaryNames(): Unit = {
  val names = new File("name.basics.tsv")
  val output = new File("name.basics.cleaned")
  val header = Seq("nconst", "primaryName", "birthYear", "deathYear", "primaryProfession", "knownForTitles")

  ridMissing[NameBasic](names, output, header) { name =>
    principals.contains(name.nconst)
  }
} 
