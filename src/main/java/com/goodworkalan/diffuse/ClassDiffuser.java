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

    public Object diffuse(Diffuser diffuse, Object object, StringBuilder path, Set<String> includes) {
        return ((Class<?>) object).getCanonicalName();
    }

    /**
     * Return true indicating that this is a diffuser for a scalar object.
     * 
     * @return True to indicate that this is a scalar converter.
     */
    public boolean isContainer() {
        return false;
    }
}
