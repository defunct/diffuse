package com.goodworkalan.diffuse;

import java.util.Set;

/**
 * A no-op object diffuser that simply returns the object given.
 * <p>
 * This object diffuser should never be used to convert anything but the
 * primitive types, their object counterparts, and String.
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
     * @param diffuser
     *            The root diffuser.
     * @param object
     *            The object to diffuse.
     * @param path
     *            The object path in the object graph.
     * @param includes
     *            The set of included paths.
     */
    public Object diffuse(Diffuser diffuser, Object object, StringBuilder path, Set<String> includes) {
        return object;
    }

    /**
     * Return true indicating that this is an object diffuser for a scalar
     * object.
     * 
     * @return True to indicate that this is a scalar object diffuser.
     */
    public boolean isContainer() {
        return false;
    }
}
