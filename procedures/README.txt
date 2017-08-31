# Support for running Gremlin as Stored Procedure in Neo4j 3.x

```
cp neo4j-tinkerpop-api-impl/procedures/target/neo4j-tinkerpop-api-impl-*-procedure.jar  $NEO4J_HOME/plugins/
$NEO4J_HOME/bin/neo4j restart
$NEO4J_HOME/bin/cypher-shell -u neo4j -p test
neo4j-sh (?)$ cypher call gremlin.run("g.V().hasLabel('Product').has('productName',name)", {name:'Chai'});

+--------------------------------------------------------------------+
| value                                                              |
+--------------------------------------------------------------------+
| Node[343]{productName:"Chai",quantityPerUnit:"10 boxes x 20 bags"} |
+--------------------------------------------------------------------+
1 row

```

// cypher call gremlin.run("g.V().hasLabel('Product').has('productName',name).both().out().path().take(10)", {name:'Chai'});
