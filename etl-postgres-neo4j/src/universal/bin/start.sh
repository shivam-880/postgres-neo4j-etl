#!/bin/bash

BIN_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
HOME_DIR="$(dirname "$BIN_DIR")"
CONF_DIR="${HOME_DIR}/conf"
LIB_DIR="${HOME_DIR}/lib"

if [ ! -d ${CONF_DIR} ] || [ ! -d ${LIB_DIR} ]; then
  echo "`date`: Mandatory directory check failed"
  exit 0
fi

java -server -cp "${LIB_DIR}/*:${CONF_DIR}/*" \
  -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
  -Dpostgres_neo4j_etl.home="${HOME_DIR}" \
  -Dpostgres_neo4j_etl.conf="${CONF_DIR}" \
  -Dpostgres_neo4j_etl.minimal="${ETL_MINIMAL}" \
  -Dlogback.configurationFile="${CONF_DIR}/logback.xml" \
   com.iamsmkr.imdb.PostgresNeo4jEtl 2>&1

postgres_neo4j_etl_pid=$!

echo
echo "PostgresNeo4jEtl service started with PID [$postgres_neo4j_etl_pid] at [`date`]"
