package org.neo4j.tinkerpop.api.impl;

import org.junit.Test;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.tinkerpop.api.*;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author mh
 * @since 01.03.16
 */
public class BasicAPITest {

    public static final String QUERY = "MATCH path = (d:Person {name:{name}})-[r:LOVES]->(a) RETURN id(d) as id, r, a, path";
    public static final int RUNS = 10000;

    @Test
    public void testCreateGraph() throws Exception {
        FileUtils.deleteRecursively(new File("target/test.db"));
        Neo4jGraphAPI db = new Neo4jFactoryImpl().newGraphDatabase("target/test.db", null);
        for (int i=0;i<10;i++) createGraph(db,false);

        long start = System.currentTimeMillis();
        for (int i = 0; i< RUNS; i++) createGraph(db,false);
        long end = System.currentTimeMillis();
        System.out.printf("Creating and reading sample graph %d times took %d ms, %.2f ms/run.%n",RUNS,(end-start),(float)(end-start)/RUNS);
        db.shutdown();
    }

    protected void createGraph(Neo4jGraphAPI db, boolean success) {
        try (Neo4jTx tx = db.tx()) {
            Neo4jNode dan = db.createNode("Person");
            dan.setProperty("name", "Dan");
            dan.setProperty("age", 42);

            Neo4jNode ann = db.createNode("Person");
            ann.setProperty("name", "Ann");
            ann.setProperty("age", 38);
            ann.setProperty("female", true);

            Neo4jRelationship rel = dan.connectTo(ann, "LOVES");
            rel.setProperty("since", 2010);

            assertEquals(toSet(dan, ann), toSet(db.allNodes()));
            assertEquals(toSet(rel), toSet(db.allRelationships()));
            assertEquals(toSet(dan, ann), toSet(db.findNodes("Person")));
            assertEquals(toSet(dan), toSet(db.findNodes("Person", "name", "Dan")));
            assertEquals(toSet(ann), toSet(db.findNodes("Person", "age", 38)));
            assertEquals(toSet(ann), toSet(db.findNodes("Person", "female", true)));
            Iterator<Map<String, Object>> result = db.execute(QUERY, Collections.<String, Object>singletonMap("name", "Dan"));
            assertTrue(result.hasNext());
            Map<String, Object> row = result.next();
            assertFalse(result.hasNext());

            assertEquals(dan.getId(), row.get("id"));
            assertEquals(ann, row.get("a"));
            assertEquals(rel, row.get("r"));
            Iterable<Neo4jEntity> path = (Iterable<Neo4jEntity>) row.get("path");


            Iterator<Neo4jEntity> it = path.iterator();
            assertTrue(it.hasNext());
            assertEquals(dan, it.next());
            assertEquals(rel, it.next());
            assertEquals(ann, it.next());
            assertFalse(it.hasNext());
            if (success) tx.success();
        }
    }

    private static <T> Collection<T> toSet(Iterable<T> values) {
        LinkedHashSet<T> set = new LinkedHashSet<>();
        for (T value : values) {
            set.add(value);
        }
        return set;
    }

    private static <T> Collection<T> toSet(T...values) {
        LinkedHashSet<T> set = new LinkedHashSet<>(values.length);
        Collections.addAll(set, values);
        return set;
    }


}
