package com.goodworkalan.diffuse;

import java.util.Set;

/**
 * Convert an object into a string by calling its <code>toString</code> method.
 * 
 * @author Alan Gutierrez
 */
public class ToStringDiffuser implements ObjectDiffuser {
    /** The singleton instance of the to string diffuser. */
    public final static ObjectDiffuser INSTANCE = new ToStringDiffuser();

	/**
	 * Diffuse the given <code>object</code> by calling its
	 * <code>toString</code> method.
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
	 * @return The result of calling <code>toString</code> on the object.
	 */
    public Object diffuse(Diffuser diffuser, Object object, StringBuilder path, Set<String> includes) {
        return object.toString();
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
