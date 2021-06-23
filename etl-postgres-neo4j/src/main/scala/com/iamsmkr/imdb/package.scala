package com.iamsmkr

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}

import scala.sys.SystemProperties

package object imdb {
  private lazy val sp = new SystemProperties()

  def getVMParam(vmParam: String): String = {
    val r = sp(vmParam)
    if (r != null && r.trim.nonEmpty) r.trim else throw new RuntimeException(s"-D$vmParam was not provided.")
  }

  private lazy val confDir = getVMParam("postgres_neo4j_etl.conf")
  private lazy val appConfFile = confDir + File.separator + "application.conf"

  final lazy val appConf: Config = ConfigFactory.parseFile(new File(appConfFile))
  final lazy val neo4j: Config = appConf.getConfig("neo4j")
  final lazy val neo4jInterface: String = neo4j.getString("serverName")
  final lazy val neo4jPort: String = neo4j.getString("portNumber")
  final lazy val neo4jUri: String = s"bolt://$neo4jInterface:$neo4jPort"

  final lazy val etlMinimal: Boolean = getVMParam("postgres_neo4j_etl.minimal").toBoolean

  final lazy val logMsg = s"%s: %s nodes created. Cypher query = %s"
}
