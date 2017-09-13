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
import org.neo4j.tinkerpop.api.Neo4jDirection;
import org.neo4j.tinkerpop.api.Neo4jNode;
import org.neo4j.tinkerpop.api.Neo4jRelationship;

import java.util.Set;

import static org.neo4j.graphdb.RelationshipType.withName;
import static org.neo4j.tinkerpop.api.impl.Util.wrap;

/**
 * @author mh
 * @since 25.03.15
 */
public class Neo4jNodeImpl extends Neo4jEntityImpl<Node> implements Neo4jNode {
    public Neo4jNodeImpl(Node node) {
        super(node);
    }

    @Override
    public Set<String> labels() {
        return Util.toLabels(entity.getLabels());
    }

    @Override
    public boolean hasLabel(String label) {
        return entity.hasLabel(Label.label(label));
    }

    @Override
    public void addLabel(String label) {
        entity.addLabel(Label.label(label));
    }

    @Override
    public void removeLabel(String label) {
        entity.removeLabel(Label.label(label));
    }

    @Override
    public int degree(Neo4jDirection direction, String type) {
        return type == null ?
                (direction != null ?
                        entity.getDegree(Direction.valueOf(direction.name())) :
                        entity.getDegree()) :
                (direction != null ?
                        entity.getDegree(withName(type), Direction.valueOf(direction.name())) :
                        entity.getDegree(withName(type)));
    }

    @Override
    public Iterable<Neo4jRelationship> relationships(Neo4jDirection direction, String... types) {
        Iterable<Relationship> relationships = types.length == 0 ?
                (direction != null ?
                        entity.getRelationships(Direction.valueOf(direction.name())) :
                        entity.getRelationships()) :
                (direction != null ?
                        entity.getRelationships(Direction.valueOf(direction.name()), Util.types(types)) :
                        entity.getRelationships(Util.types(types)));

        return new IterableWrapper<Neo4jRelationship, Relationship>(relationships) {
            @Override
            protected Neo4jRelationship underlyingObjectToObject(Relationship relationship) {
                return new Neo4jRelationshipImpl(relationship);
            }
        };
    }

    @Override
    public Neo4jRelationship connectTo(Neo4jNode node, String type) {
        return wrap(entity.createRelationshipTo(((Neo4jNodeImpl) node).entity, RelationshipType.withName(type)));
    }
}
