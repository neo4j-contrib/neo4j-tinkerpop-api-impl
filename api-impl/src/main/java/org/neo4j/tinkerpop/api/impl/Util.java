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

import static org.neo4j.graphdb.RelationshipType.withName;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.helpers.collection.IterableWrapper;
import org.neo4j.tinkerpop.api.Neo4jNode;
import org.neo4j.tinkerpop.api.Neo4jRelationship;

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

    static Neo4jNode wrap(Node node, PropertyConverter propertyConverter) {
        return new Neo4jNodeImpl(node, propertyConverter);
    }

    static Neo4jRelationshipImpl wrap(Relationship rel, PropertyConverter propertyConverter) {
        return new Neo4jRelationshipImpl(rel, propertyConverter);
    }

    static Iterable<Neo4jNode> wrapNodes(final Iterable<Node> nodes, PropertyConverter propertyConverter) {
        return new IterableWrapper<Neo4jNode, Node>(nodes) {
            @Override
            protected Neo4jNode underlyingObjectToObject(Node node) {
                return wrap(node, propertyConverter);
            }
        };
    }
    static Iterable<Neo4jNode> wrapNodes(final ResourceIterator<Node> nodes, PropertyConverter propertyConverter) {
        return new IterableWrapper<Neo4jNode, Node>(new SingleIteratorWrapper(nodes)) {
            @Override
            protected Neo4jNode underlyingObjectToObject(Node node) {
                return wrap(node, propertyConverter);
            }
        };
    }
    static Iterable<Neo4jRelationship> wrapRels(final Iterable<Relationship> rels, PropertyConverter propertyConverter) {
        return new IterableWrapper<Neo4jRelationship, Relationship>(rels) {
            @Override
            protected Neo4jRelationship underlyingObjectToObject(Relationship rel) {
                return wrap(rel, propertyConverter);
            }
        };
    }

    static Object wrapObject(Object value, PropertyConverter propertyConverter) {
        if (value == null) return null;
        if (value instanceof Node) return wrap((Node) value, propertyConverter);
        if (value instanceof Relationship) return wrap((Relationship) value, propertyConverter);
        if (value instanceof Iterable) {
            List<Object> result = new ArrayList<>();
            for (Object o : (Iterable)value) {
                result.add(wrapObject(o, propertyConverter));
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
