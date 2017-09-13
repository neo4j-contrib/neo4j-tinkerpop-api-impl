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

import org.neo4j.graphdb.*;
import org.neo4j.helpers.collection.IterableWrapper;
import org.neo4j.tinkerpop.api.Neo4jNode;
import org.neo4j.tinkerpop.api.Neo4jRelationship;

import java.util.*;

import static org.neo4j.graphdb.RelationshipType.withName;

class Util {
    static Label[] toLabels(String... labels) {
        Label[] result = new Label[labels.length];
        for (int i = 0; i < labels.length; i++) {
            result[i]= Label.label(labels[i]);
        }
        return result;
    }

    static Set<String> toLabels(Iterable<Label> labels) {
        Set<String> result = new TreeSet<>();
        for (Label label : labels) {
            result.add(label.name());
        }
        return result;
    }

    static RelationshipType[] types(String...types) {
        RelationshipType[] result = new RelationshipType[types.length];
        for (int i = 0; i < types.length; i++) {
            result[i] = withName(types[i]);
        }
        return result;
    }

    static Neo4jNode wrap(Node node) {
        return new Neo4jNodeImpl(node);
    }

    static Neo4jRelationshipImpl wrap(Relationship rel) {
        return new Neo4jRelationshipImpl(rel);
    }

    static Iterable<Neo4jNode> wrapNodes(final Iterable<Node> nodes) {
        return new IterableWrapper<Neo4jNode, Node>(nodes) {
            @Override
            protected Neo4jNode underlyingObjectToObject(Node node) {
                return wrap(node);
            }
        };
    }
    static Iterable<Neo4jNode> wrapNodes(final ResourceIterator<Node> nodes) {
        return new IterableWrapper<Neo4jNode, Node>(new SingleIteratorWrapper(nodes)) {
            @Override
            protected Neo4jNode underlyingObjectToObject(Node node) {
                return wrap(node);
            }
        };
    }
    static Iterable<Neo4jRelationship> wrapRels(final Iterable<Relationship> rels) {
        return new IterableWrapper<Neo4jRelationship, Relationship>(rels) {
            @Override
            protected Neo4jRelationship underlyingObjectToObject(Relationship rel) {
                return wrap(rel);
            }
        };
    }

    static Object wrapObject(Object value) {
        if (value == null) return null;
        if (value instanceof Node) return wrap((Node) value);
        if (value instanceof Relationship) return wrap((Relationship) value);
        if (value instanceof Iterable) {
            List<Object> result = new ArrayList<>();
            for (Object o : (Iterable)value) {
                result.add(wrapObject(o));
            }
            return result;
        }
        return value;
    }

    private static class SingleIteratorWrapper implements Iterable<Node> {
        private final ResourceIterator<Node> nodes;

        public SingleIteratorWrapper(ResourceIterator<Node> nodes) {
            this.nodes = nodes;
        }

        @Override
        public Iterator<Node> iterator() {
            return nodes;
        }
    }
}
