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

    @Before
    public void setUp() throws Exception {
        calls = new ArrayList<>();
        callArgs = new ArrayList<>();
    }

    @Test
    public void setProperty() {
        PropertyContainer mock = mockPropertyContainer(null, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock);

        entity.setProperty("prop", "val");

        assertEquals(singletonList("setProperty"), calls);
        assertEquals(singletonList(asList("prop", "val")), callArgs);
    }

    @Test
    public void setNullProperty() {
        PropertyContainer mock = mockPropertyContainer(null, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock);

        entity.setProperty("prop", null);

        assertEquals(singletonList("setProperty"), calls);
        assertEquals(singletonList(asList("prop", null)), callArgs);
    }

    @Test
    public void setListProperty() {
        PropertyContainer mock = mockPropertyContainer(null, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock);

        ArrayList<Object> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        entity.setProperty("prop", list);

        assertEquals(singletonList("setProperty"), calls);
        assertEquals(1, callArgs.size());
        assertArrayEquals(new String[]{"a", "b", "c"}, second(callArgs.get(0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setHeterogeneousListProperty() {
        PropertyContainer mock = mockPropertyContainer(null, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock);

        entity.setProperty("prop", asList(1, "a", true));
    }

    @Test
    public void setEmptyListProperty() {
        PropertyContainer mock = mockPropertyContainer(null, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock);

        entity.setProperty("prop", new ArrayList<>());

        assertEquals(singletonList("removeProperty"), calls);
        assertEquals(singletonList(singletonList("prop")), callArgs);
    }

    @Test
    public void setListsPropertyTypes() {
        PropertyContainer mock = mockPropertyContainer(null, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock);

        entity.setProperty("prop0", asList("a", "b", "c"));
        entity.setProperty("prop1", asList((byte) 1, (byte) 2, (byte) 3));
        entity.setProperty("prop2", asList(1L, 2L, 3L));
        entity.setProperty("prop3", asList(1, 2, 3));
        entity.setProperty("prop4", asList(1.0, 2.0, 3.0));
        entity.setProperty("prop5", asList(1.0f, 2.0f, 3.0f));
        entity.setProperty("prop6", asList(true, false, true));
        entity.setProperty("prop7", asList('a', 'b', 'c'));
        entity.setProperty("prop8", asList((short) 1, (short) 2, (short) 3));

        assertEquals(9, callArgs.size());
        assertArrayEquals(new String[]{"a", "b", "c"}, second(callArgs.get(0)));
        assertArrayEquals(new Byte[]{1, 2, 3}, second(callArgs.get(1)));
        assertArrayEquals(new Long[]{1L, 2L, 3L}, second(callArgs.get(2)));
        assertArrayEquals(new Integer[]{1, 2, 3}, second(callArgs.get(3)));
        assertArrayEquals(new Double[]{1.0, 2.0, 3.0}, second(callArgs.get(4)));
        assertArrayEquals(new Float[]{1.0f, 2.0f, 3.0f}, second(callArgs.get(5)));
        assertArrayEquals(new Boolean[]{true, false, true}, second(callArgs.get(6)));
        assertArrayEquals(new Character[]{'a', 'b', 'c'}, second(callArgs.get(7)));
        assertArrayEquals(new Short[]{1, 2, 3}, second(callArgs.get(8)));
    }


    @Test
    public void getListProperty() {
        int[] response = {1, 2, 3};
        PropertyContainer mock = mockPropertyContainer(response, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock);

        Object result = entity.getProperty("prop");

        assertEquals(singletonList("getProperty"), calls);
        assertEquals(singletonList(singletonList("prop")), callArgs);
        assertEquals(asList(1, 2, 3), result);
    }

    @Test
    public void getDefaultListProperty() {
        int[] response = {1, 2, 3};
        PropertyContainer mock = mockPropertyContainer(response, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock);

        Object result = entity.getProperty("prop", asList(4, 5, 6));

        assertEquals(singletonList("getProperty"), calls);
        assertEquals(singletonList(asList("prop", asList(4, 5, 6))), callArgs);
        assertEquals(asList(1, 2, 3), result);
    }

    @Test
    public void getDefaultArrayProperty() {
        int[] defaultValue = {1, 2, 3};
        PropertyContainer mock = mockPropertyContainer(defaultValue, calls, callArgs);
        Neo4jEntityImpl<PropertyContainer> entity = new Neo4jEntityImpl<>(mock);

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