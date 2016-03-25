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
