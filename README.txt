Implementation of Apache Licensed Neo4j API for Tinkerpop3
==========================================================

This repository contains both, the implementation of the `neo4j-tinkerpop-api`
in `neo4j-tinkerpop-api-impl`

This is work in progress, it depends on neo4j-gremlin being updated to use `neo4j-tinkerpop-api`
which is available here (https://github.com/jexp/incubator-tinkerpop)

To build:

````
# build tinkerpop3 branch with neo4j-gremlin updated

git clone https://github.com/jexp/incubator-tinkerpop
cd incubator-tinkerpop
git checkout neo4j-gremlin
mvn clean install -DskipTests
cd ..

# build neo4j-tinkerpop-api

git clone github.com/neo4j-contrib/neo4j-tinkerpop-api
cd neo4j-tinkerpop-api
mvn clean install
cd ..

# build neo4j-tinkerpop-api-impl

git clone github.com/neo4j-contrib/neo4j-tinkerpop-api-impl
cd neo4j-tinkerpop-api-impl
mvn clean install

# run neo4j-gremlin tests
git clone github.com/neo4j-contrib/neo4j-tinkerpop-api-binding
cd neo4j-tinkerpop-api-binding/neo4j-tinkerpop-api-tests
mvn -o install

...
Tests run: 1903, Failures: 2, Errors: 12, Skipped: 382

````
