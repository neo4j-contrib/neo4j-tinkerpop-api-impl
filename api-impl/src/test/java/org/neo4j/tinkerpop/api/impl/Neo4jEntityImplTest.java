package org.neo4j.tinkerpop.api.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.PropertyContainer;

public class Neo4jEntityImplTest {
    List<String> calls;
    List<List<Object>> callArgs;
    PropertyConverter propertyConverter;

    @Before
    public void setUp() throws Exception {
        calls = new ArrayList<>();
        callArgs = new ArrayList<>();
        propertyConverter = new PropertyConverter.ListArray();
    }

    @Test
    public void setProperty() {
        PropertyContainer mock = mockPropertyContainer(null, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock, propertyConverter);

        entity.setProperty("prop", "val");

        assertEquals(singletonList("setProperty"), calls);
        assertEquals(singletonList(asList("prop", "val")), callArgs);
    }

    @Test
    public void setNullProperty() {
        PropertyContainer mock = mockPropertyContainer(null, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock, propertyConverter);

        entity.setProperty("prop", null);

        assertEquals(singletonList("setProperty"), calls);
        assertEquals(singletonList(asList("prop", null)), callArgs);
    }

    @Test
    public void setListProperty() {
        PropertyContainer mock = mockPropertyContainer(null, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock, propertyConverter);

        ArrayList<Object> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        entity.setProperty("prop", list);

        assertEquals(singletonList("setProperty"), calls);
        assertEquals(1, callArgs.size());
        assertArrayEquals(new String[]{"a", "b", "c"}, second(callArgs.get(0)));
    }

    @Test
    public void setArrayProperty() {
        PropertyContainer mock = mockPropertyContainer(null, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock, propertyConverter);

        entity.setProperty("prop", new String[]{"a", "b", "c"});

        assertEquals(singletonList("setProperty"), calls);
        assertEquals(1, callArgs.size());
        assertArrayEquals(new String[]{"a", "b", "c"}, second(callArgs.get(0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setHeterogeneousListProperty() {
        PropertyContainer mock = mockPropertyContainer(null, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock, propertyConverter);

        entity.setProperty("prop", asList(1, "a", true));
    }

    @Test
    public void setEmptyListProperty() {
        PropertyContainer mock = mockPropertyContainer(null, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock, propertyConverter);

        entity.setProperty("prop", new ArrayList<>());

        assertEquals(singletonList("removeProperty"), calls);
        assertEquals(singletonList(singletonList("prop")), callArgs);
    }

    @Test
    public void getListProperty() {
        int[] response = {1, 2, 3};
        PropertyContainer mock = mockPropertyContainer(response, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock, propertyConverter);

        Object result = entity.getProperty("prop");

        assertEquals(singletonList("getProperty"), calls);
        assertEquals(singletonList(singletonList("prop")), callArgs);
        assertEquals(asList(1, 2, 3), result);
    }

    @Test
    public void getDefaultListProperty() {
        int[] response = {1, 2, 3};
        PropertyContainer mock = mockPropertyContainer(response, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock, propertyConverter);

        Object result = entity.getProperty("prop", asList(4, 5, 6));

        assertEquals(singletonList("getProperty"), calls);
        assertEquals(singletonList(asList("prop", asList(4, 5, 6))), callArgs);
        assertEquals(asList(1, 2, 3), result);
    }

    @Test
    public void getDefaultArrayProperty() {
        int[] defaultValue = {1, 2, 3};
        PropertyContainer mock = mockPropertyContainer(defaultValue, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock, propertyConverter);

        Object result = entity.getProperty("prop", defaultValue);

        assertEquals(singletonList("getProperty"), calls);
        assertEquals(singletonList(asList("prop", defaultValue)), callArgs);
        assertSame(result, defaultValue);
    }

    private PropertyContainer mockPropertyContainer(Object response, List<String> calls, List<List<Object>> callArgs) {
        return (PropertyContainer) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{PropertyContainer.class},
            (proxy, method, args) -> {
                calls.add(method.getName());
                callArgs.add(asList(args));
                return response;
            });
    }

    @SuppressWarnings("unchecked")
    private <T> T second(List<Object> callArgs) {
        return (T) callArgs.get(1);
    }
}