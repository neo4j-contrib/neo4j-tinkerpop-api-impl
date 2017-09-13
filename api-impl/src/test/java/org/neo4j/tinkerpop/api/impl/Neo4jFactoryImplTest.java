/**
 * Copyright (C) 2015 Neo Technology
 *
 * This file is part of neo4j-tinkerpop-binding <http://neo4j.com>.
 *
 * Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.neo4j.tinkerpop.api.impl;

import org.junit.Test;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.HighlyAvailableGraphDatabaseFactory;
import org.neo4j.kernel.ha.HighlyAvailableGraphDatabase;
import org.neo4j.tinkerpop.api.Neo4jGraphAPI;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

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
    public void testCreateHighlyAvailableGraphDatabase() throws Exception {
        Path file = Files.createTempDirectory("tp-test-db");
        String path = file.toAbsolutePath().toString();
        Map<String, String> config = new HashMap<>();
        config.put("ha.server_id","1");
        config.put("ha.initial_hosts","localhost:5001");
        Neo4jGraphAPIImpl db = (Neo4jGraphAPIImpl) new Neo4jFactoryImpl().newGraphDatabase(path, config);
        assertEquals(true, db.getGraphDatabase() instanceof HighlyAvailableGraphDatabase);
        db.shutdown();
    }
}
