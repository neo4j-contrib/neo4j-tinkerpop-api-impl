/**
 * Copyright (C) 2015 Neo Technology
 *
 * This file is part of neo4j-tinkerpop-binding <http://neo4j.com>.
 *
 * structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with neo4j-tinkerpop-binding.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.tinkerpop.api.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.HighlyAvailableGraphDatabaseFactory;
import org.neo4j.tinkerpop.api.Neo4jFactory;
import org.neo4j.tinkerpop.api.Neo4jGraphAPI;

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

            boolean convertListsToArrays = false;
            if (config != null) {
                builder = builder.setConfig(config);
                convertListsToArrays = Boolean.valueOf(config.get("convertListsToArrays"));
            }

            PropertyConverter propertyConverter = convertListsToArrays ?
                new PropertyConverter.ListArray() :
                new PropertyConverter.None();

            return new Neo4jGraphAPIImpl(builder.newGraphDatabase(), propertyConverter);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error handling path " + path, e);
        }
    }

    protected GraphDatabaseFactory createGraphDatabaseFactory(Map<String, String> config) {
        if (config != null && config.containsKey("ha.server_id")) return new HighlyAvailableGraphDatabaseFactory();
        return new GraphDatabaseFactory();
    }
}
