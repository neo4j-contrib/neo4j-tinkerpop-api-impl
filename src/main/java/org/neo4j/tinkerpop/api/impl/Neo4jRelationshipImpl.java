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

import org.neo4j.graphdb.Relationship;
import org.neo4j.tinkerpop.api.Neo4jNode;
import org.neo4j.tinkerpop.api.Neo4jRelationship;

public class Neo4jRelationshipImpl extends Neo4jEntityImpl<Relationship> implements Neo4jRelationship {

    public Neo4jRelationshipImpl(Relationship rel) {
        super(rel);
    }

    @Override
    public String type() {
        return entity.getType().name();
    }

    @Override
    public Neo4jNode start() {
        return new Neo4jNodeImpl(entity.getStartNode());
    }

    @Override
    public Neo4jNode end() {
        return new Neo4jNodeImpl(entity.getEndNode());
    }

    @Override
    public Neo4jNode other(Neo4jNode node) {
        return new Neo4jNodeImpl(entity.getOtherNode((((Neo4jNodeImpl) node).entity)));
    }
}
