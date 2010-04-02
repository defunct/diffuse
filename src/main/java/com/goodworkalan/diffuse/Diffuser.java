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


public class Diffuser {
    private final ConcurrentMap<Class<?>, ObjectDiffuser> cache = new ConcurrentHashMap<Class<?>, ObjectDiffuser>();

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
            boolean encache = false;
            Class<?> iterator = type;
            for (;;) {
                ObjectDiffuser converter = diffusers.get(iterator);
                if (converter == null) {
                    encache = true;
                    converter = interfaceConverter(iterator.getInterfaces());
                }
                if (converter != null) {
                    if (encache) {
                        cache.put(iterator, converter);
                    }
                    return converter;
                }
                encache = true;
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

    // FIXME Add to Lighthouse: rename flatten to diffuse.
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
        return getConverter(object.getClass()).diffuse(this, object, new StringBuilder(), recurse ? Collections.<String>emptySet() : Collections.singleton("\0"));
    }

    public Object diffuse(Object object, String...includes) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).diffuse(this, object, new StringBuilder(), new HashSet<String>(Arrays.asList(includes)));
    }
}
