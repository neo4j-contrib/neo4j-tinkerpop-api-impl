package org.neo4j.tinkerpop.api.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public interface PropertyConverter {
    default Object onSet(Object property) {
        return property;
    }

    default Object onGet(Object property) {
        return property;
    }

    class None implements PropertyConverter {
    }

    class ListArray implements PropertyConverter {
        @Override
        public Object onSet(Object property) {
            if (property instanceof Collection) {
                return asArray((Collection) property);
            } else {
                return property;
            }
        }

        @Override
        public Object onGet(Object property) {
            if (property.getClass().isArray()) {
                return asListOfObjects(property);
            } else {
                return property;
            }
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

        private Object asArray(Collection collection) {
            try {
                if (collection.isEmpty()) {
                    return new Object[0];
                } else {
                    Class<?> type = collection.iterator().next().getClass();
                    Object array = Array.newInstance(type, collection.size());
                    collection.toArray((Object[]) array);
                    return array;
                }
            } catch (ArrayStoreException e) {
                throw new IllegalArgumentException(
                    "Unable to convert collection to array. Elements have a different type? Got: " + collection, e);
            }
        }
    }
}
