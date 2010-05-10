package com.goodworkalan.diffuse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Converts a collection into a list.
 * 
 * @author Alan Gutierrez
 */
class CollectionDiffuser implements ObjectDiffuser {
    /** The singleton collection diffuser intsance. */
    public final static ObjectDiffuser INSTANCE = new CollectionDiffuser();

    /**
     * Copy the given <code>collection</code> into an unmodifiable
     * <code>java.util.List</code> using the given object diffuser provider,
     * <code>diffuser</code>, to diffuse the list elements.
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
     * @param collection
     *            The collection to diffuse.
     * @param path
     *            The path of the collection in the object graph.
     * @param includes
     *            The set of paths to include in the diffused object graph or an
     *            empty set to include all paths.
     * @return An unmodifiable collection containing the diffused objects of the
     *         array.
     */
    public Object diffuse(Diffuser diffuser, Object collection, StringBuilder path, Set<String> includes) {
        path.append("*.");
        int index = path.length();
        Collection<?> original = (Collection<?>) collection;
        List<Object> copy = new ArrayList<Object>();
        for (Object item : original) {
            if (item == null) {
                copy.add(item);
            } else {
                copy.add(diffuser.getConverter(item.getClass()).diffuse(diffuser, item, path, includes));
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
