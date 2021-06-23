FROM neo4j:latest

WORKDIR /usr/src/neo4j-init

COPY . .

ENTRYPOINT ["./create-indexes-constraints.sh"]
