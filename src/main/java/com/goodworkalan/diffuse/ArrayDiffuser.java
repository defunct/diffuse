package com.goodworkalan.diffuse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * An object diffuser that converts an array in to a <code>java.util.List</code>
 * <p>
 * This is the only instance where Diffuse turns a primitive type into a more
 * complicated class. Diffuse is meant to produce a tree of maps, lists and
 * scalars, where scalars are primitives or strings. Converting arrays to lists
 * is part of achieving this consistency.
 * 
 * @author Alan Gutierrez
 */
public class ArrayDiffuser implements ObjectDiffuser {
    /** The singleton instance of the array converter. */
    public final static ObjectDiffuser INSTANCE = new ArrayDiffuser();

    /**
     * Convert the given object appending an array wildcard to the given path.
     * The given set of included paths is forwarded to converters that generate
     * hashes.
     * 
     * @param object
     *            The array to convert.
     * @param path
     *            The object path in the object graph.
     * @param includes
     *            The set of included paths.
     */
    public Object convert(Diffuser diffuse, Object object, StringBuilder path, Set<String> includes) {
        Object[] original = (Object[]) object;
        List<Object> copy = new ArrayList<Object>();
        path.append("*.");
        int index = path.length();
        for (int i = 0, stop = original.length; i < stop; i++) {
            Object value = original[i];
            if (value == null) {
                copy.add(value);
            } else {
                copy.add(diffuse.getConverter(value.getClass()).convert(diffuse, value, path, includes));
                path.setLength(index);
            }
        }
        return Collections.unmodifiableList(copy);
    }

    /**
     * Return true indicating that this is a converter for containers of other
     * objects.
     * 
     * @return True to indicate that this is a container converter.
     */
    public boolean isContainer() {
        return true;
    }
}
