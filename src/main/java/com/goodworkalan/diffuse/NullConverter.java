package com.goodworkalan.diffuse;

import java.util.Set;



/**
 * A no-op converter that simply returns the object given.
 * 
 * @author Alan Gutierrez
 */
public class NullConverter implements Converter {
    /** The singleton instance of the array converter. */
    public final static Converter INSTANCE = new NullConverter();

    /**
     * Simply return the given object, the path and set of included paths are
     * ignored.
     * 
     * @param object
     *            The array to convert.
     * @param path
     *            The object path in the object graph.
     * @param includes
     *            The set of included paths.
     */
    public Object convert(Diffuse diffuse, Object object, StringBuilder path, Set<String> includes) {
        return object;
    }

    /**
     * Return false indicating that this is not a converter for containers of
     * other objects. This converter should never be used to convert anything
     * but the primitive types, their object counterparts, and String.
     * 
     * @return True to indicate that this is a container converter.
     */
    public boolean isContainer() {
        return false;
    }
}
