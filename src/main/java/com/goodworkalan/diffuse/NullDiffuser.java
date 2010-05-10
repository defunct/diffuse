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
     * Simply return the given object since it is already a string or primitive.
     * 
     * 
     * @param diffuser
     *            The object diffuser provider.
     * @param object
     *            The class to diffuse.
     * @param path
     *            The path of the object in the object graph.
     * @param includes
     *            The set of paths to include in the diffused object graph or an
     *            empty set to include all paths.
     * @return The object.
     */
    public Object diffuse(Diffuser diffuser, Object object, StringBuilder path, Set<String> includes) {
        return object;
    }

    /**
     * Return false indicating that this is a diffuser for a scalar object.
     * 
     * @return False to indicate that this is a scalar diffuser.
     */
    public boolean isContainer() {
        return false;
    }
}
