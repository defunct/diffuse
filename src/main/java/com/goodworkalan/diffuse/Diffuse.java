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


public class Diffuse {
    private final ClassAsssociation<Converter> cache;
    
    private final ClassAsssociation<Converter> defaults;
    
    public Diffuse() {
        this(new ConcurrentClassAssociation<Converter>(), new ConcurrentClassAssociation<Converter>());
    }

    public Diffuse(ClassAsssociation<Converter> cache, ClassAsssociation<Converter> defaults) {
        this.cache = cache;
        this.defaults = setDefaultConverters(defaults);
    }
    
    private static ClassAsssociation<Converter> setDefaultConverters(ClassAsssociation<Converter> defaults) {
        defaults.put(Byte.class, NullConverter.INSTANCE);
        defaults.put(Boolean.class, NullConverter.INSTANCE);
        defaults.put(Short.class, NullConverter.INSTANCE);
        defaults.put(Character.class, NullConverter.INSTANCE);
        defaults.put(Integer.class, NullConverter.INSTANCE);
        defaults.put(Long.class, NullConverter.INSTANCE);
        defaults.put(Float.class, NullConverter.INSTANCE);
        defaults.put(Double.class, NullConverter.INSTANCE);
        defaults.put(String.class, NullConverter.INSTANCE);
        defaults.put(Object.class, BeanConverter.INSTANCE);
        defaults.put(Map.class, MapConverter.INSTANCE);
        defaults.put(Collection.class, CollectionConverter.INSTANCE);
        defaults.put(File.class, ToStringConverter.INSTANCE);
        defaults.put(URL.class, ToStringConverter.INSTANCE);
        defaults.put(URI.class, ToStringConverter.INSTANCE);
        defaults.put(Class.class, ClassConverter.INSTANCE);
        defaults.put(CharSequence.class, ToStringConverter.INSTANCE);
        defaults.put(StringWriter.class, ToStringConverter.INSTANCE);
        defaults.put(Date.class, DateConverter.INSTANCE);
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
    public void setConverter(Class<?> type, Converter converter) {
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
        setConverter(toStringClass, ToStringConverter.INSTANCE);
    }

    /**
     * Get the object converter for the given object type.
     * 
     * @param type
     *            The object type.
     * @return The object converter.
     */
    public Converter getConverter(Class<?> type) {
        if (type.isArray()) {
            return ArrayConverter.INSTANCE;
        }
        if (type.isPrimitive()) {
            return NullConverter.INSTANCE;
        }
        Converter converter = cache.get(type);
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
        return getConverter(object.getClass()).convert(this, object, new StringBuilder(), includes);
    }

    public Object flatten(Object object) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).convert(this, object, new StringBuilder(), Collections.singleton("\0"));
    }

    public Object flatten(Object object, boolean recurse) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).convert(this, object, new StringBuilder(), recurse ? Collections.<String>emptySet() : Collections.singleton("\0"));
    }

    public Object flatten(Object object, String...includes) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).convert(this, object, new StringBuilder(), new HashSet<String>(Arrays.asList(includes)));
    }
}
