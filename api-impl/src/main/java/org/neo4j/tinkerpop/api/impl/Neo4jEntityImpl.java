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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
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
        Object property = entity.getProperty(name);
        if (property.getClass().isArray()) {
            return asListOfObjects(property);
        } else {
            return property;
        }
    }

    @Override
    public Object getProperty(String name, Object defaultValue) {
        Object property = entity.getProperty(name, defaultValue);
        if (defaultValue != property && property.getClass().isArray()) {
            return asListOfObjects(property);
        } else {
            return property;
        }
    }

    @Override
    public void setProperty(String name, Object value) {
        if (value instanceof Collection) {
            Collection collection = (Collection) value;
            if (collection.isEmpty()) {
                entity.removeProperty(name);
            } else {
                Object array = toArray(collection);
                entity.setProperty(name, array);
            }
        } else {
            entity.setProperty(name, value);
        }
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

    private ArrayList<Object> asListOfObjects(Object array) {
        int length = Array.getLength(array);
        ArrayList<Object> result = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Object e = Array.get(array, i);
            result.add(e);
        }
        return result;
    }

    private Object toArray(Collection collection) {
        try {
            Class<?> type = collection.iterator().next().getClass();
            Object array = Array.newInstance(type, collection.size());
            collection.toArray((Object[]) array);
            return array;
        } catch (ArrayStoreException e) {
            throw new IllegalArgumentException(
                "Unable to convert collection to array. Elements have a different type? Got: " + collection, e);
        }
    }
}
