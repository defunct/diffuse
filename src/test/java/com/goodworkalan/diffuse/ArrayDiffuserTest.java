package com.goodworkalan.diffuse;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.testng.annotations.Test;

/**
 * Tests for the array converter.
 *
 * @author Alan Gutierrez
 */
public class ArrayDiffuserTest {
    /** Test array conversion. */
    @Test
    public void convert() {
        StringBuilder path = new StringBuilder();
        path.append("object");
        Set<String> includes = Collections.emptySet();
        Collection<?> collection = (Collection<?>) ArrayDiffuser.INSTANCE.diffuse(new Diffuser(), new String[] { "a", null }, path, includes);
        Iterator<?> iterator = collection.iterator();
        assertEquals(iterator.next(), "a");
        assertNull(iterator.next());
        assertFalse(iterator.hasNext());
    }
}
