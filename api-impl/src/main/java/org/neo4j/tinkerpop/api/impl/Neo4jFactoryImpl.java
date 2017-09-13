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

import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.HighlyAvailableGraphDatabaseFactory;
import org.neo4j.tinkerpop.api.Neo4jFactory;
import org.neo4j.tinkerpop.api.Neo4jGraphAPI;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * @author mh
 * @since 25.03.15
 */
public class Neo4jFactoryImpl implements Neo4jFactory {

    @Override
    public Neo4jGraphAPI newGraphDatabase(String path, Map<String, String> config) {
        try {
            if (path.startsWith("file:")) {
                path = new URL(path).getPath();
            }
            GraphDatabaseBuilder builder = createGraphDatabaseFactory(config).newEmbeddedDatabaseBuilder(new File(path));
            if (config != null) builder = builder.setConfig(config);
            return new Neo4jGraphAPIImpl(builder.newGraphDatabase());
        } catch(MalformedURLException e) {
            throw new RuntimeException("Error handling path "+path,e);
        }
    }

    protected GraphDatabaseFactory createGraphDatabaseFactory(Map<String, String> config) {
        if (config != null && config.containsKey("ha.server_id")) return new HighlyAvailableGraphDatabaseFactory();
        return new GraphDatabaseFactory();
    }
}
