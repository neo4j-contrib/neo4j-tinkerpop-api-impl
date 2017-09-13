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

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.helpers.collection.IteratorWrapper;
import org.neo4j.kernel.impl.core.NodeManager;
import org.neo4j.kernel.impl.core.GraphProperties;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.tinkerpop.api.Neo4jGraphAPI;
import org.neo4j.tinkerpop.api.Neo4jNode;
import org.neo4j.tinkerpop.api.Neo4jRelationship;
import org.neo4j.tinkerpop.api.Neo4jTx;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Neo4jGraphAPIImpl implements Neo4jGraphAPI {
    private final GraphDatabaseService db;
    private final GraphProperties graphProps;

    public Neo4jGraphAPIImpl(GraphDatabaseService db) {
        this.db = db;
        graphProps = ((GraphDatabaseAPI) this.db).getDependencyResolver().resolveDependency(NodeManager.class).newGraphProperties();
    }

    @Override
    public Neo4jNode createNode(String... labels) {
        return Util.wrap((labels.length == 0) ? db.createNode() : db.createNode(Util.toLabels(labels)));
    }

    @Override
    public Neo4jNode getNodeById(long id) {
        return Util.wrap(db.getNodeById(id));
    }

    @Override
    public Neo4jRelationshipImpl getRelationshipById(long id) {
        return Util.wrap(db.getRelationshipById(id));
    }

    @Override
    public void shutdown() {
        this.db.shutdown();
    }

    @Override
    public Iterable<Neo4jNode> allNodes() {
        return Util.wrapNodes(db.getAllNodes());
    }

    @Override
    public Iterable<Neo4jRelationship> allRelationships() {
        return Util.wrapRels(db.getAllRelationships());
    }

    @Override
    public Iterable<Neo4jNode> findNodes(String label) {
        return Util.wrapNodes(db.findNodes(Label.label(label)));
    }

    @Override
    public Iterable<Neo4jNode> findNodes(String label, String property, Object value) {
        return Util.wrapNodes(db.findNodes(Label.label(label), property, value));
    }

    @Override
    public Neo4jTx tx() {
        return new Neo4jTxImpl(db.beginTx());
    }

    @Override
    public Iterator<Map<String, Object>> execute(String query, Map<String, Object> params) {
        Map<String, Object> nullSafeParams = params == null ? Collections.<String, Object>emptyMap() : params;
        return new IteratorWrapper<Map<String, Object>, Map<String, Object>>(db.execute(query, nullSafeParams)) {
            @Override
            protected Map<String, Object> underlyingObjectToObject(Map<String, Object> row) {
                Map<String, Object> result = new LinkedHashMap<>(row.size());
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    result.put(entry.getKey(), Util.wrapObject(entry.getValue()));
                }
                return result;
            }

            ;
        };
    }

    @Override
    public boolean hasSchemaIndex(String label, String property) {
        Iterable<IndexDefinition> indexes = db.schema().getIndexes(Label.label(label));
        for (IndexDefinition index : indexes) {
            for (String prop : index.getPropertyKeys()) {
                if (prop.equals(property)) return true;
            }
        }
        return false;
    }

    @Override
    public Iterable<String> getKeys() {
        return graphProps.getPropertyKeys();
    }

    @Override
    public Object getProperty(String key) {
        return graphProps.getProperty(key);
    }

    @Override
    public boolean hasProperty(String key) {
        return graphProps.hasProperty(key);
    }

    @Override
    public Object removeProperty(String key) {
        return graphProps.removeProperty(key);
    }

    @Override
    public void setProperty(String key, Object value) {
        graphProps.setProperty(key, value);
    }

    @Override
    public String toString() {
        return db.toString();
    }

    public GraphDatabaseService getGraphDatabase() {
        return db;
    }
}
