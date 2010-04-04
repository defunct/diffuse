package com.goodworkalan.diffuse;

import java.util.Set;



/**
 * A no-op converter that simply returns the object given.
 * 
 * @author Alan Gutierrez
 */
public class NullDiffuser implements ObjectDiffuser {
    /** The singleton instance of the array converter. */
    public final static ObjectDiffuser INSTANCE = new NullDiffuser();

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
    public Object diffuse(Diffuser diffuse, Object object, StringBuilder path, Set<String> includes) {
        return object;
    }

    /**
     * Return true indicating that this is a diffuser for a scalar object. This
     * converter should never be used to convert anything but the primitive
     * types, their object counterparts, and String.
     * 
     * @return True to indicate that this is a scalar diffuser.
     */
    public boolean isContainer() {
        return false;
    }
}
