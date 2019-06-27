package org.neo4j.tinkerpop.api.impl;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.HighlyAvailableGraphDatabaseFactory;
import org.neo4j.kernel.ha.HighlyAvailableGraphDatabase;
import org.neo4j.tinkerpop.api.Neo4jGraphAPI;
import org.neo4j.tinkerpop.api.Neo4jNode;
import org.neo4j.tinkerpop.api.Neo4jRelationship;
import org.neo4j.tinkerpop.api.Neo4jTx;

/**
 * @author mh
 * @since 21.11.15
 */
public class Neo4jFactoryImplTest {

    @Test
    public void testCreateGraphDatabaseFactory() throws Exception {
        GraphDatabaseFactory factory = new Neo4jFactoryImpl().createGraphDatabaseFactory(Collections.singletonMap("ha.server_id", "1"));
        assertEquals(true, factory instanceof HighlyAvailableGraphDatabaseFactory);
    }

    @Test
    public void testDefaultPropertyConverter() throws Exception {
        Path file = Files.createTempDirectory("tp-test-db");
        String path = file.toAbsolutePath().toString();
        Neo4jGraphAPI db = new Neo4jFactoryImpl().newGraphDatabase(path, new HashMap<>());
        try (Neo4jTx tx = db.tx()) {
            Neo4jNode node1 = db.createNode("node1");
            Neo4jNode node2 = db.createNode("node2");
            Neo4jRelationship rel = node1.connectTo(node2, "knows");
            assertTrue(((Neo4jNodeImpl) node1).propertyConverter instanceof PropertyConverter.None);
            assertTrue(((Neo4jNodeImpl) node2).propertyConverter instanceof PropertyConverter.None);
            assertTrue(((Neo4jRelationshipImpl) rel).propertyConverter instanceof PropertyConverter.None);
            assertTrue(((Neo4jNodeImpl) rel.start()).propertyConverter instanceof PropertyConverter.None);
            assertTrue(((Neo4jNodeImpl) rel.end()).propertyConverter instanceof PropertyConverter.None);
        }
    }

    @Test
    public void testCreatePropertyConverter() throws Exception {
        Path file = Files.createTempDirectory("tp-test-db");
        String path = file.toAbsolutePath().toString();
        Map<String, String> config = singletonMap("convertListsToArrays", "true");
        Neo4jGraphAPI db = new Neo4jFactoryImpl().newGraphDatabase(path, config);
        try (Neo4jTx tx = db.tx()) {
            Neo4jNode node1 = db.createNode("node1");
            Neo4jNode node2 = db.createNode("node2");
            Neo4jRelationship rel = node1.connectTo(node2, "knows");
            assertTrue(((Neo4jNodeImpl) node1).propertyConverter instanceof PropertyConverter.ListArray);
            assertTrue(((Neo4jNodeImpl) node2).propertyConverter instanceof PropertyConverter.ListArray);
            assertTrue(((Neo4jRelationshipImpl) rel).propertyConverter instanceof PropertyConverter.ListArray);
            assertTrue(((Neo4jNodeImpl) rel.start()).propertyConverter instanceof PropertyConverter.ListArray);
            assertTrue(((Neo4jNodeImpl) rel.end()).propertyConverter instanceof PropertyConverter.ListArray);
        }
    }

    @Test
    public void testCreateHighlyAvailableGraphDatabase() throws Exception {
        Path file = Files.createTempDirectory("tp-test-db");
        String path = file.toAbsolutePath().toString();
        Map<String, String> config = new HashMap<>();
        config.put("ha.server_id", "1");
        config.put("ha.initial_hosts", "localhost:5001");
        Neo4jGraphAPIImpl db = (Neo4jGraphAPIImpl) new Neo4jFactoryImpl().newGraphDatabase(path, config);
        assertEquals(true, db.getGraphDatabase() instanceof HighlyAvailableGraphDatabase);
        db.shutdown();
    }
}
