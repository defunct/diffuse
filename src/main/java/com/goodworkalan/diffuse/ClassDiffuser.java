package com.goodworkalan.diffuse;

import java.util.Set;

/**
 * Converts an <code>Class</code> to the canonical class name. This
 * implementation is in lieu of using <code>Class.toString</code>, which
 * prepends the string value with a superfluous "class " or "interface "
 * depending.
 * 
 * @author Alan Gutierrez
 */
public class ClassDiffuser implements ObjectDiffuser {
    /** The singleton instance of the class diffuser. */
    public final static ObjectDiffuser INSTANCE = new ClassDiffuser();

    /**
     * Diffuse the class given in object by returning its canonical name or name
     * if the canonical name is null.
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
     * @return The canonical class name or the class name.
     */
    public Object diffuse(Diffuser diffuser, Object object, StringBuilder path, Set<String> includes) {
        String className = ((Class<?>) object).getCanonicalName();
        if (className == null) {
            return ((Class<?>) object).getName();
        }
        return className;
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
