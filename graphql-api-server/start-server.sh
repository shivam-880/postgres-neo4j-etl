#!/bin/bash

log_info() {
  printf '%s %s\n' "$(date -u +"%Y-%m-%d %H:%M:%S:%3N%z") INFO  start-server.sh: $1"
  return
}

log_info "Waiting for Neo4j..." 

./wait-for-it.sh --quiet --timeout=60 ${NEO4J_HOSTNAME}:${NEO4J_HTTP_PORT}

isServerAvailable=`echo $?`

if [[ $isServerAvailable ]]
then
  log_info "Neo4j is available: Starting graphql api server..." 

  /usr/local/bin/npm start ./index.js
else
  log_info "Neo4j is unreachable: No graphql api server started."
fi
