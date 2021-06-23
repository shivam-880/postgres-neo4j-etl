import sbt._

object Dependencies {
  private lazy val slickVersion = "3.2.0"
  private lazy val slickHikariCpVersion = "3.2.0"
  private lazy val postgresqlVersion = "42.2.14"
  private lazy val neo4jVersion = "4.3.1"
  private lazy val akkaStreamVersion = "2.5.8"
  private lazy val scalaLoggingVersion = "3.9.2"
  private lazy val logbackClassicVersion = "1.3.0-alpha5"

  lazy val slick = "com.typesafe.slick" %% "slick" % slickVersion
  lazy val slickHikariCp = "com.typesafe.slick" %% "slick-hikaricp" % slickHikariCpVersion
  lazy val postgresql = "org.postgresql" % "postgresql" % postgresqlVersion
  lazy val neo4j = "org.neo4j.driver" % "neo4j-java-driver" % neo4jVersion
  lazy val akkaStreams = "com.typesafe.akka" %% "akka-stream" % akkaStreamVersion
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % logbackClassicVersion
}
