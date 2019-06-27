package org.neo4j.tinkerpop.api.impl;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

public class PropertyConverterTest {
    PropertyConverter.ListArray converter;

    @Before
    public void setUp() throws Exception {
        converter = new PropertyConverter.ListArray();
    }

    @Test
    public void testListConverterSet() {
        assertEquals("val", converter.onSet("val"));
        assertNull(converter.onSet(null));
        assertArrayEquals(new String[]{"a", "b", "c"}, (String[]) converter.onSet(asList("a", "b", "c")));
        assertArrayEquals(new String[]{"a", "b", "c"}, (String[]) converter.onSet(new String[]{"a", "b", "c"}));
        assertArrayEquals(new Object[0], (Object[]) converter.onSet(new ArrayList<>()));
    }

    @Test
    public void testListConverterSetPropertyTypes() {
        assertArrayEquals(new String[]{"a", "b", "c"}, (String[]) converter.onSet(asList("a", "b", "c")));
        assertArrayEquals(new Byte[]{1, 2, 3}, (Byte[]) converter.onSet(asList((byte) 1, (byte) 2, (byte) 3)));
        assertArrayEquals(new Long[]{1L, 2L, 3L}, (Long[]) converter.onSet(asList(1L, 2L, 3L)));
        assertArrayEquals(new Integer[]{1, 2, 3}, (Integer[]) converter.onSet(asList(1, 2, 3)));
        assertArrayEquals(new Double[]{1.0, 2.0, 3.0}, (Double[]) converter.onSet(asList(1.0, 2.0, 3.0)));
        assertArrayEquals(new Float[]{1.0f, 2.0f, 3.0f}, (Float[]) converter.onSet(asList(1.0f, 2.0f, 3.0f)));
        assertArrayEquals(new Boolean[]{true, false, true}, (Boolean[]) converter.onSet(asList(true, false, true)));
        assertArrayEquals(new Character[]{'a', 'b', 'c'}, (Character[]) converter.onSet(asList('a', 'b', 'c')));
        assertArrayEquals(new Short[]{1, 2, 3}, (Short[]) converter.onSet(asList((short) 1, (short) 2, (short) 3)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testListConverterSetHeterogeneousList() {
        converter.onSet(asList(1, "a", true));
    }

    @Test
    public void testListConverterGet() {
        assertEquals("val", converter.onGet("val"));
        assertEquals(asList(1, 2, 3), converter.onGet(new int[]{1, 2, 3}));
    }

}