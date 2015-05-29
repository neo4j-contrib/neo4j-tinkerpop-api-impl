/**
 * Copyright (C) 2015 Neo Technology
 * <p/>
 * This file is part of neo4j-tinkerpop-binding <http://neo4j.com>.
 * <p/>
 * structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with neo4j-tinkerpop-binding.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.tinkerpop.api.impl;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.helpers.collection.IteratorWrapper;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.kernel.impl.core.GraphPropertiesImpl;
import org.neo4j.kernel.impl.core.NodeManager;
import org.neo4j.tinkerpop.api.Neo4jGraphAPI;
import org.neo4j.tinkerpop.api.Neo4jNode;
import org.neo4j.tinkerpop.api.Neo4jRelationship;
import org.neo4j.tinkerpop.api.Neo4jTx;
import org.neo4j.tooling.GlobalGraphOperations;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.neo4j.tinkerpop.api.impl.Util.wrapNodes;

public class Neo4jGraphAPIImpl implements Neo4jGraphAPI {
    private final GraphDatabaseService db;
    private final GraphPropertiesImpl graphProps;

    Neo4jGraphAPIImpl(GraphDatabaseService db) {
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
        return wrapNodes(db.getAllNodes());
    }

    @Override
    public Iterable<Neo4jRelationship> allRelationships() {
        return Util.wrapRels(GlobalGraphOperations.at(db).getAllRelationships());
    }

    @Override
    public Iterable<Neo4jRelationship> findRelationships(String property, Object value) {
        return Util.wrapRels(db.index().getRelationshipAutoIndexer().getAutoIndex().get(property, value));
    }

    @Override
    public Iterable<Neo4jNode> findNodes(String property, Object value) {
        return Util.wrapNodes((Iterable<Node>) db.index().getNodeAutoIndexer().getAutoIndex().get(property, value));
    }

    @Override
    public Iterable<Neo4jNode> findNodes(String label) {
        return wrapNodes(db.findNodes(DynamicLabel.label(label)));
    }

    @Override
    public Iterable<Neo4jNode> findNodes(String label, String property, Object value) {
        return wrapNodes(db.findNodes(DynamicLabel.label(label), property, value));
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
        Iterable<IndexDefinition> indexes = db.schema().getIndexes(DynamicLabel.label(label));
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
}
