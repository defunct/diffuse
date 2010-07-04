package com.goodworkalan.diffuse;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.goodworkalan.reflective.getter.Getter;
import com.goodworkalan.reflective.getter.Getters;

/**
 * Diffuses any object into a <code>java.util.Map</code> where fields and Java
 * Bean properties are the entries in the map. This is the default diffuser for
 * <code>java.lang.Object</code> making it the default diffuser for classes that
 * do not have an object diffuser mapped to their class, super classes or
 * interfaces.
 * 
 * @author Alan Gutierrez
 */
class BeanDiffuser implements ObjectDiffuser {
    /** The singleton instance of the bean diffuser. */
    public final static ObjectDiffuser INSTANCE = new BeanDiffuser();

    /**
     * Freeze the given object, copying all arrays and Java collections classes,
     * turning all the classes specified in the list classes into frozen beans.
     * 
     * @param diffuser
     *            The object diffuser provider.
     * @param object
     *            The object to diffuse.
     * @param path
     *            The path of the object in the object graph.
     * @param includes
     *            The set of paths to include in the diffused object graph or an
     *            empty set to include all paths.
     * @return The object converted into a map of object fields and properties.
     */
    public Object diffuse(Diffuser diffuser, Object object, StringBuilder path, Set<String> includes) {
        return  Collections.unmodifiableMap(modifiable(diffuser, object, path, includes));
    }

    /**
     * Convert the given object into <code>java.util.Map</code> that can be
     * modified, unlike the unmodifiable map generated by the
     * <code>diffuse</code> method. This method is called by
     * <code>diffuse</code> which then wraps the returned map in an unmodifiable
     * decorator implementation. Subclasses of <code>BeanDiffuser</code> can
     * call or override this method to obtain a modifiable map so that they can
     * add or remove elements from the map before it is returned to the user.
     * 
     * @param diffuser
     *            The object diffuser provider.
     * @param object
     *            The object to diffuse.
     * @param path
     *            The path of the object in the object graph.
     * @param includes
     *            The set of paths to include in the diffused object graph or an
     *            empty set to include all paths.
     * @return An unmodifiable map with an diffused object entry for each field
     *         or property of the object, if the field or property was included
     *         according to the set of includes.
     */
    protected Map<String, Object> modifiable(Diffuser diffuser, Object object, StringBuilder path, Set<String> includes) {
        Class<?> beanClass = object.getClass();
        int index = path.length();
        Map<String, Object> diffused = new LinkedHashMap<String, Object>();
        for (Getter getter : Getters.getGetters(beanClass).values()) {
            String name = getter.getName();
            path.append(name);
            ObjectDiffuser converter = diffuser.getDiffuser(getter.getType());
            if (!converter.isContainer() || includes.isEmpty() || includes.contains(path.toString())) {
                Object value;
                try {
                    value = getter.get(object);
                } catch (Exception e) {
                    checkRuntimeException(e);
                    throw new IllegalArgumentException(String.format(
                            "\n\tUnable to set bean property.\n" +
                            "\t\tClass: [%s]\n\t\tProperty: [%s], Type[%s]", getter.getMember().getDeclaringClass(), getter.getName(), getter.getType()), e);
                }
                if (value == null) {
                    diffused.put(name, value);
                } else {
                    path.append(".");
                    diffused.put(name, converter.diffuse(diffuser, value, path, includes));
                }
            }
            path.setLength(index);
        }
        return diffused;
    }

    /**
     * Throw the given exception if it is a <code>RuntimeException</code>. This
     * method extracted for isolation in testing.
     * 
     * @param e
     *            The exception.
     * @exception RuntimeException
     *                If the given exception is a <code>RuntimeException</code>.
     */
    static void checkRuntimeException(Exception e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
    }
    
    /**
     * Return true indicating that this is a diffuser for containers of other
     * objects and not a scalar.
     * 
     * @return True to indicate that this is a container diffuser.
     */
    public boolean isContainer() {
        return true;
    }
}
