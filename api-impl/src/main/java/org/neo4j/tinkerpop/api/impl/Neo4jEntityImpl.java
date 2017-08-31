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
