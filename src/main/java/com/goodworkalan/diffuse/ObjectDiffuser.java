package com.goodworkalan.diffuse;

import java.util.Set;

/**
 * A strategy for converting a particular type of object to a map, list or
 * scalar, where a scalar is a primitive or string.
 * <p>
 * Implementations of this interface will diffuse an object in an object graph,
 * converting it to either a map, list or scalar. The {@link #isContainer}
 * method indicates that the object is a container.
 * 
 * @author Alan Gutierrez
 */
public interface ObjectDiffuser {
    /**
     * Diffuse the given object, converting it to a map, list, or scalar where a
     * scalar is a primitive or string. Any nested objects can be diffused using
     * the given root diffuser.
     * <p>
     * Implementations of this interface that diffuse objects by converting them
     * into into maps should only include non-scalar entries if either the given
     * set of paths to include, includes, is empty or if it contains the path
     * formed by appending the entry key to the given path. If a non-scalar
     * entry is included in a to map diffusion, the to map object diffuser will
     * first obtain an object diffuser to the entry value type, then call the
     * diffuse method of the object diffuser passing it the given path with
     * entry key appended, followed by an appended "." (period). This will
     * maintain the current object path as the diffusion descends an object
     * graph.
     * <p>
     * Diffusion is a recursive process. A diffuser that diffuses and object
     * into a map or list will use the given root diffuser to find the
     * appropriate object diffuser to diffuse the items in the list or the entry
     * values in the map.
     * 
     * @param diffuser
     *            The root diffuser to use to diffuse nested objects.
     * @param object
     *            The object to diffuse.
     * @param path
     *            The current object path in the object graph.
     * @param includes
     *            A set of paths to include in the diffusion.
     * @return The diffused object, converted to a map, list or scalar, where a
     *         scalar is a primitive or a string.
     */
    public Object diffuse(Diffuser diffuser, Object object, StringBuilder path, Set<String> includes);

    public boolean isContainer();
}