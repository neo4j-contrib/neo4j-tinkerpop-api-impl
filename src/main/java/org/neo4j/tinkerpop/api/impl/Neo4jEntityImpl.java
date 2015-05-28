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

import org.neo4j.graphdb.*;
import org.neo4j.tinkerpop.api.Neo4jEntity;

import java.util.List;

/**
 * @author mh
 * @since 25.03.15
 */
public class Neo4jEntityImpl<T extends PropertyContainer> implements Neo4jEntity {

    protected final T entity;

    public Neo4jEntityImpl(T entity) {
        this.entity = entity;
    }

    public long getId() { return entity instanceof Node ? ((Node) entity).getId() : ((Relationship) entity).getId(); }
    public Iterable<String> getKeys() { return entity.getPropertyKeys(); }
    public Object getProperty(String name) { return entity.getProperty(name); }
    public Object getProperty(String name, Object defaultValue) { return entity.getProperty(name, defaultValue); }
    public void setProperty(String name, Object value)  { entity.setProperty(name, value); }
    public Object removeProperty(String name)  { return entity.removeProperty(name); }
    public boolean hasProperty(String name)  { return entity.hasProperty(name); }
    public void delete()  { if (entity instanceof Node) ((Node)entity).delete(); else ((Relationship)entity).delete(); }
}
