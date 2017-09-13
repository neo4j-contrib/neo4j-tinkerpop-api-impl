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
