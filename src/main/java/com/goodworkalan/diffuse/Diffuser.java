package com.goodworkalan.diffuse;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The root diffuser used to map classes to object diffusers and initiate the
 * diffusion of object graphs. The output will be an object tree composed of
 * maps, lists and scalars, where scalars are primitives or strings. The maps
 * and lists will be unmodifiable. Any attempt to add or remove elements from
 * the maps and lists will result in an
 * <code>UnsupportedOperationException</code>.
 * <p>
 * An object in the object graph is converted using an {@link ObjectDiffuser}
 * implementation. The <code>Diffuser</code> keeps a map that associates classes
 * and interfaces with <code>ObjectDiffuser</code> implementations.
 * <p>
 * When an object is diffused, it is returned immediately if it is null. It is
 * converted into a list if it is an array. Otherwise, the
 * <code>ObjectDiffuser</code> map is checked for an object diffuser that
 * matches the type of the object. If none is found, then the
 * <code>ObjectDiffuser</code> map is checked for an <code>ObjectDiffuser</code>
 * that matches any of the interfaces implemented by the type, or an
 * <code>ObjectDiffuser</code> that matches any of their super interfaces. If no
 * <code>ObjectDiffuser</code>, the test is repeated with the super class of the
 * class of the given object and its interfaces and super interfaces. This
 * attempt to match proceeds up the class hierarchy until
 * <code>java.lang.Object</code> is reached and the default
 * <code>ObjectDiffuser</code> for <code>Object</code> is used.
 * <p>
 * In the case of class that implements two interfaces that both have
 * <code>ObjectDiffuser</code> instances mapped to their type in the
 * <code>Diffuser</code> the <code>ObjectDiffuser</code> returned is undefined.
 * In order to choose a specific <code>ObjectDiffuser</code> in this instance,
 * one should be mapped directly to the class itself using the
 * {@link #setConverter(Class, ObjectDiffuser) setConverter} method.
 * <p>
 * The ascent up the class hierarchy is potentially time consuming, so the
 * results of the <code>ObjectDiffuser</code> search are cached. The cache is
 * cleared every time the {@link #setConverter(Class, ObjectDiffuser)
 * setConverter} method is called. Mapping <code>ObjectDiffuser</code>
 * implementations should be done before a <code>Diffuser</code> is used to
 * diffuser an object.
 * <p>
 * Upon creation, map is populated with reasonable defaults for the
 * <code>java.util</code> containers, the primitives and <code>Object</code>
 * derived counterparts, and <code>String</code>. Additionally, some reasonable
 * defaults as associated with some common Java types.
 * 
 * @author Alan Gutierrez
 */
public class Diffuser {
    /**
     * The classes to their object diffusers as resolved by ascending the object
     * hierarchy, looking for an object diffuser that will diffuse a super class
     * or interface. This cache is reset when a new object diffuser is assigned
     * using the {@link #setConverter(Class, ObjectDiffuser) setConverter}
     * method.
     */
    private final ConcurrentMap<Class<?>, ObjectDiffuser> cache = new ConcurrentHashMap<Class<?>, ObjectDiffuser>();

    /** Map of assigned classes to object diffusers. */
    private final ConcurrentMap<Class<?>, ObjectDiffuser> diffusers = new ConcurrentHashMap<Class<?>, ObjectDiffuser>();

    /**
     * Create a diffuser with reasonable defaults for the most common types. The
     * default object diffuser, if no other diffusers matches is the
     * <code>BeanDiffusers</code>.
     */
    public Diffuser() {
        diffusers.put(Byte.class, NullDiffuser.INSTANCE);
        diffusers.put(Boolean.class, NullDiffuser.INSTANCE);
        diffusers.put(Short.class, NullDiffuser.INSTANCE);
        diffusers.put(Character.class, NullDiffuser.INSTANCE);
        diffusers.put(Integer.class, NullDiffuser.INSTANCE);
        diffusers.put(Long.class, NullDiffuser.INSTANCE);
        diffusers.put(Float.class, NullDiffuser.INSTANCE);
        diffusers.put(Double.class, NullDiffuser.INSTANCE);
        diffusers.put(String.class, NullDiffuser.INSTANCE);
        diffusers.put(Object.class, BeanDiffuser.INSTANCE);
        diffusers.put(Map.class, MapDiffuser.INSTANCE);
        diffusers.put(Collection.class, CollectionConverter.INSTANCE);
        diffusers.put(File.class, ToStringDiffuser.INSTANCE);
        diffusers.put(URL.class, ToStringDiffuser.INSTANCE);
        diffusers.put(URI.class, ToStringDiffuser.INSTANCE);
        diffusers.put(Class.class, ClassDiffuser.INSTANCE);
        diffusers.put(CharSequence.class, ToStringDiffuser.INSTANCE);
        diffusers.put(StringWriter.class, ToStringDiffuser.INSTANCE);
    }

    /**
     * Assign the given object converter to the given object type. The converter
     * will be assigned to a map of converters that is associated with the
     * <code>ClassLoader</code> of the given object type. The assignment will be
     * inherited by any subsequently created child class loaders of the
     * associated class loader, but not by existing child class loaders.
     * 
     * @param type
     *            The object type.
     * @param converter
     *            The object converter.
     */
    public void setConverter(Class<?> type, ObjectDiffuser converter) {
        cache.clear();
        diffusers.put(type, converter);
    }

    /**
     * Assign the to string converter to the given object type. The converter
     * will be assigned to a map of converters that is associated with the
     * <code>ClassLoader</code> of the given object type. The assignment will be
     * inherited by any subsequently created child class loaders of the
     * associated class loader, but not by existing child class loaders.
     * 
     * @param type
     *            The object type.
     * @param converter
     *            The object converter.
     */
    public void toString(Class<?> toStringClass) {
        setConverter(toStringClass, ToStringDiffuser.INSTANCE);
    }

    private ObjectDiffuser interfaceConverter(Class<?>[] ifaces) {
        LinkedList<Class<?>> queue = new LinkedList<Class<?>>(Arrays.asList(ifaces));
        while (!queue.isEmpty()) {
            Class<?> iface = queue.removeFirst();
            ObjectDiffuser diffuser = diffusers.get(iface);
            if (diffuser != null) {
                return diffuser;
            }
            queue.addAll(Arrays.asList((Class<?>[]) iface.getInterfaces()));
        }
        return null;
    }

    /**
     * Get the object converter for the given object type.
     * 
     * @param type
     *            The object type.
     * @return The object converter.
     */
    public ObjectDiffuser getConverter(Class<?> type) {
        if (type.isArray()) {
            return ArrayDiffuser.INSTANCE;
        }
        if (type.isPrimitive()) {
            return NullDiffuser.INSTANCE;
        }
        ObjectDiffuser diffuser = cache.get(type);
        if (diffuser == null) {
            Class<?> iterator = type;
            for (;;) {
                ObjectDiffuser converter = diffusers.get(iterator);
                if (converter == null) {
                    converter = interfaceConverter(iterator.getInterfaces());
                }
                if (converter != null) {
                    cache.put(type, converter);
                    return converter;
                }
                iterator = iterator.getSuperclass();
            }
        }
        return diffuser;
    }

    /**
     * Freeze the given object, copying all arrays and Java collections classes,
     * turning all the classes specified in the list classes into frozen beans.
     * <p>
     * FIXME Empty set here needs to be converted to '\0' set.
     * 
     * @param object
     *            The object to freeze.
     * @param freeze
     *            The set of classes to freeze when encountered.
     * @return A frozen object.
     */
    public Object diffuse(Object object, Set<String> includes) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).diffuse(this, object, new StringBuilder(), includes);
    }

    // FIXME Should default be to just recurse? If so than this makes sense,
    // because the vararg would be the empty set.
    public Object diffuse(Object object) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).diffuse(this, object, new StringBuilder(), Collections.singleton("\0"));
    }

    public Object diffuse(Object object, boolean recurse) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).diffuse(this, object, new StringBuilder(), recurse ? Collections.<String> emptySet() : Collections.singleton("\0"));
    }

    public Object diffuse(Object object, String... includes) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).diffuse(this, object, new StringBuilder(), new HashSet<String>(Arrays.asList(includes)));
    }
}
