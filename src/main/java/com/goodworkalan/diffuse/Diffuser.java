package com.goodworkalan.diffuse;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Diffuser {
    private final ClassAsssociation<ObjectDiffuser> cache;
    
    private final ClassAsssociation<ObjectDiffuser> defaults;
    
    public Diffuser() {
        this(new ConcurrentClassAssociation<ObjectDiffuser>(), new ConcurrentClassAssociation<ObjectDiffuser>());
    }

    public Diffuser(ClassAsssociation<ObjectDiffuser> cache, ClassAsssociation<ObjectDiffuser> defaults) {
        this.cache = cache;
        this.defaults = setDefaultConverters(defaults);
    }
    
    private static ClassAsssociation<ObjectDiffuser> setDefaultConverters(ClassAsssociation<ObjectDiffuser> defaults) {
        defaults.put(Byte.class, NullDiffuser.INSTANCE);
        defaults.put(Boolean.class, NullDiffuser.INSTANCE);
        defaults.put(Short.class, NullDiffuser.INSTANCE);
        defaults.put(Character.class, NullDiffuser.INSTANCE);
        defaults.put(Integer.class, NullDiffuser.INSTANCE);
        defaults.put(Long.class, NullDiffuser.INSTANCE);
        defaults.put(Float.class, NullDiffuser.INSTANCE);
        defaults.put(Double.class, NullDiffuser.INSTANCE);
        defaults.put(String.class, NullDiffuser.INSTANCE);
        defaults.put(Object.class, BeanDiffuser.INSTANCE);
        defaults.put(Map.class, MapDiffuser.INSTANCE);
        defaults.put(Collection.class, CollectionConverter.INSTANCE);
        defaults.put(File.class, ToStringDiffuser.INSTANCE);
        defaults.put(URL.class, ToStringDiffuser.INSTANCE);
        defaults.put(URI.class, ToStringDiffuser.INSTANCE);
        defaults.put(Class.class, ClassDiffuser.INSTANCE);
        defaults.put(CharSequence.class, ToStringDiffuser.INSTANCE);
        defaults.put(StringWriter.class, ToStringDiffuser.INSTANCE);
        defaults.put(Date.class, DateDiffuser.INSTANCE);
        return defaults;
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
        defaults.put(type, converter);
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
        ObjectDiffuser converter = cache.get(type);
        if (converter == null) {
            cache.put(type, converter = defaults.get(type));
        }
        return converter;
    }

    /**
     * Freeze the given object, copying all arrays and Java collections classes,
     * turning all the classes specified in the list classes into frozen beans.
     * 
     * @param object
     *            The object to freeze.
     * @param freeze
     *            The set of classes to freeze when encountered.
     * @return A frozen object.
     */
    public Object flatten(Object object, Set<String> includes) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).diffuse(this, object, new StringBuilder(), includes);
    }

    // FIXME Add to Lighthouse: rename flatten to diffuse.
    public Object flatten(Object object) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).diffuse(this, object, new StringBuilder(), Collections.singleton("\0"));
    }

    public Object flatten(Object object, boolean recurse) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).diffuse(this, object, new StringBuilder(), recurse ? Collections.<String>emptySet() : Collections.singleton("\0"));
    }

    public Object flatten(Object object, String...includes) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).diffuse(this, object, new StringBuilder(), new HashSet<String>(Arrays.asList(includes)));
    }
}
