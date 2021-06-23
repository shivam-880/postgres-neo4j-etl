const { Neo4jGraphQL } = require("@neo4j/graphql");
const { ApolloServer } = require("apollo-server");
const neo4j = require("neo4j-driver");
const fs = require("fs");
const dotenv = require("dotenv");
const path = require("path");

dotenv.config();

const typeDefs = fs
  .readFileSync(path.join(__dirname, "schema.graphql"))
  .toString("utf-8");

const neoSchema = new Neo4jGraphQL({
  typeDefs
});

const driver = neo4j.driver(
  `bolt://${process.env.NEO4J_HOSTNAME}:${process.env.NEO4J_BOLT_PORT}`
   //, neo4j.auth.basic(process.env.NEO4J_USER, process.env.NEO4J_PASSWORD)
);

const server = new ApolloServer({
  context: { driver,  },
  schema: neoSchema.schema,
  introspection: true,
  playground: true,
});

server.listen().then(({ url }) => {
  console.log(`GraphQL server ready at ${url}`);
});
