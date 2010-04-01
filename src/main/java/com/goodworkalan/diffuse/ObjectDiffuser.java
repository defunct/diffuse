package com.goodworkalan.diffuse;

import java.util.Set;

/**
 * A strategy for converting a particular type of object to a map, list or scalar,
 * where a scalar is a primitive or string.
 * <p>
 * Implementations of this interface will diffuse an object in an object graph,
 * converting it to either a map, list or scalar. The {@link #isContainer} method
 * indicates that the object is a container.
 */
public interface ObjectDiffuser {
    public Object convert(Diffuser diffuse, Object object, StringBuilder path, Set<String> includes);
    
    public boolean isContainer();
}