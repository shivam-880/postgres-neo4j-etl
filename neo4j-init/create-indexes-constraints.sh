#!/bin/bash

log_info() {
  printf '%s %s\n' "$(date -u +"%Y-%m-%d %H:%M:%S:%3N%z") INFO  create-indexes-constraints.sh: $1"
  return
}

sleep 30s

log_info "Waiting for Neo4j..." 

./wait-for-it.sh --quiet --timeout=60 ${NEO4J_HOSTNAME}:${NEO4J_HTTP_PORT}

isServerAvailable=`echo $?`

if [[ $isServerAvailable ]]
then
  log_info "Neo4j is available: Creating indexes and constraints..." 

  load_cypher() {
    cat $1 | while read line 
    do
      if [ -n "$line" ]; then
        cypher-shell -a bolt://${NEO4J_HOSTNAME}:${NEO4J_BOLT_PORT} "$line"
      fi
    done
  }

  load_cypher "./cyphers/constraints.cypher"

  log_info "Finished creating constraints."

  load_cypher "./cyphers/indexes.cypher"

  log_info "Finished creating indexes."

else 
  log_info "Neo4j is unreachable: No constraints or indexes created."
fi
