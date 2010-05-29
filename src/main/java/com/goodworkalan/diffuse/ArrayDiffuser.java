package com.goodworkalan.diffuse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * An object diffuser that converts an array in to an unmodifiable
 * <code>java.util.List</code>.
 * <p>
 * This is the only instance where Diffuse turns a type into a more complicated
 * class. Diffuse is meant to produce a tree of maps, lists and scalars, where
 * scalars are primitives or strings. Converting arrays to lists is part of
 * achieving this consistency.
 * 
 * @author Alan Gutierrez
 */
public class ArrayDiffuser implements ObjectDiffuser {
    /** The singleton instance of the array diffuser. */
    public final static ObjectDiffuser INSTANCE = new ArrayDiffuser();

    /**
     * Convert the given <code>array</code> into an unmodifiable
     * <code>java.util.List</code> using the given root <code>diffuser</code> to
     * diffuse the list elements.
     * <p>
     * Before using the root diffuser to diffuse an element, the array diffuser
     * will append an asterisk to act as a wild card character in the path
     * maintained in the given <code>path</code> reference as the object
     * diffusers recursively descend an object graph. Users will specify
     * container objects to include in diffusion by specifying a wild card to
     * indicate match all array elements.
     * <p>
     * The set of includes is not consulted, since include paths cannot specify
     * specific array elements. Only the wildcard is accepted and all elements
     * are added to the list created.
     * 
     * @param diffuser
     *            The object diffuser provider.
     * @param array
     *            The array to diffuse.
     * @param path
     *            The path of the object in the object graph.
     * @param includes
     *            The set of paths to include in the diffused object graph or an
     *            empty set to include all paths.
     * @return An unmodifiable collection containing the diffused objects of the
     *         array.
     */
    public Object diffuse(Diffuser diffuser, Object array, StringBuilder path, Set<String> includes) {
        Object[] original = (Object[]) array;
        List<Object> copy = new ArrayList<Object>();
        path.append("*.");
        int index = path.length();
        for (int i = 0, stop = original.length; i < stop; i++) {
            Object value = original[i];
            if (value == null) {
                copy.add(value);
            } else {
                copy.add(diffuser.getDiffuser(value.getClass()).diffuse(diffuser, value, path, includes));
                path.setLength(index);
            }
        }
        return Collections.unmodifiableList(copy);
    }

    /**
     * Return true indicating that this diffuser converts an object that is a
     * container for other objects.
     * 
     * @return True to indicate that this is not a container diffuser.
     */
    public boolean isContainer() {
        return true;
    }
}
