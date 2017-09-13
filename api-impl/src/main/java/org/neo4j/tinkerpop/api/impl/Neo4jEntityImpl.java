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

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.tinkerpop.api.Neo4jEntity;

/**
 * @author mh
 * @since 25.03.15
 */
public class Neo4jEntityImpl<T extends PropertyContainer> implements Neo4jEntity {

    protected final T entity;

    public Neo4jEntityImpl(T entity) {
        this.entity = entity;
    }

    @Override
    public long getId() {
        return entity instanceof Node ? ((Node) entity).getId() : ((Relationship) entity).getId();
    }

    @Override
    public Iterable<String> getKeys() {
        return entity.getPropertyKeys();
    }

    @Override
    public Object getProperty(String name) {
        return entity.getProperty(name);
    }

    @Override
    public Object getProperty(String name, Object defaultValue) {
        return entity.getProperty(name, defaultValue);
    }

    @Override
    public void setProperty(String name, Object value) {
        entity.setProperty(name, value);
    }

    @Override
    public Object removeProperty(String name) {
        return entity.removeProperty(name);
    }

    @Override
    public boolean hasProperty(String name) {
        return entity.hasProperty(name);
    }

    @Override
    public void delete() {
        if (entity instanceof Node) ((Node) entity).delete();
        else ((Relationship) entity).delete();
    }

    @Override
    public String toString() {
        return this.entity.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return getClass().isInstance(obj) && ((Neo4jEntity)obj).getId() == getId();
    }

    @Override
    public int hashCode() {
        return (int) getId();
    }

    public T getEntity() {
        return entity;
    }
}
