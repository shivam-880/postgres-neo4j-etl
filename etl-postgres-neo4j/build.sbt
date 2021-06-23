import Dependencies._
import com.typesafe.sbt.packager.docker.Cmd

name := "etl-postgres-neo4j"

version := "0.1"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(slick, scalaLogging, logbackClassic, slickHikariCp, postgresql, neo4j, akkaStreams)

dockerCommands += Cmd("ENTRYPOINT", "./bin/start.sh")

enablePlugins(JavaAppPackaging, DockerPlugin)
