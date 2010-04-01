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
public class ClassConverter implements Converter {
    /** The singleton instance of the class diffuser. */
    public final static Converter INSTANCE = new ClassConverter();

    public Object convert(Diffuse diffuse, Object object, StringBuilder path, Set<String> includes) {
        return ((Class<?>) object).getCanonicalName();
    }

    /**
     * Return false indicating that this is not a converter for containers of
     * other objects.
     * 
     * @return False to indicate that this is not a container converter.
     */
    public boolean isContainer() {
        return false;
    }
}
