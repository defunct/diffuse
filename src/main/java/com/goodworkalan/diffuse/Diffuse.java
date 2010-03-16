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
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class Diffuse {

    /**
     * The map of maps of object converters, indexed by class loader. The weak
     * hash map associates a map of converters with a class loader. This will
     * allow custom object converters to be garbage collected when a class loader
     * is unloaded and disposed of.
     */
    private static final WeakHashMap<ClassLoader, ConcurrentMap<Class<?>, Converter>> classLoaderConverters = new WeakHashMap<ClassLoader, ConcurrentMap<Class<?>,Converter>>();
    /** The map of the default converters. */
    private static final Map<Class<?>, Converter> defaultConverters = new ConcurrentHashMap<Class<?>, Converter>();
    static {
        defaultConverters.put(Byte.class, NullConverter.INSTANCE);
        defaultConverters.put(Boolean.class, NullConverter.INSTANCE);
        defaultConverters.put(Short.class, NullConverter.INSTANCE);
        defaultConverters.put(Character.class, NullConverter.INSTANCE);
        defaultConverters.put(Integer.class, NullConverter.INSTANCE);
        defaultConverters.put(Long.class, NullConverter.INSTANCE);
        defaultConverters.put(Float.class, NullConverter.INSTANCE);
        defaultConverters.put(Double.class, NullConverter.INSTANCE);
        defaultConverters.put(String.class, NullConverter.INSTANCE);
        defaultConverters.put(Object.class, BeanConverter.INSTANCE);
        defaultConverters.put(Map.class, MapConverter.INSTANCE);
        defaultConverters.put(Collection.class, CollectionConverter.INSTANCE);
        defaultConverters.put(File.class, ToStringConverter.INSTANCE);
        defaultConverters.put(URL.class, ToStringConverter.INSTANCE);
        defaultConverters.put(URI.class, ToStringConverter.INSTANCE);
        defaultConverters.put(Class.class, ClassConverter.INSTANCE);
        defaultConverters.put(CharSequence.class, ToStringConverter.INSTANCE);
        defaultConverters.put(StringWriter.class, ToStringConverter.INSTANCE);
        defaultConverters.put(Date.class, DateConverter.INSTANCE);
    }
    private static Map<Class<?>, Converter> getClassLoaderConverters(ClassLoader classLoader) {
        synchronized (classLoaderConverters) {
            ConcurrentMap<Class<?>, Converter> converters = classLoaderConverters.get(classLoader);
            if (converters == null) {
                converters = new ConcurrentHashMap<Class<?>, Converter>(getConverters(classLoader.getParent()));
                classLoaderConverters.put(classLoader, converters);
            }
            return converters;
        }
    }

    private static Map<Class<?>, Converter> getConverters(ClassLoader classLoader) {
        if (classLoader == null) {
            return defaultConverters;
        }
        return getClassLoaderConverters(classLoader);
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
    public static void setConverter(Class<?> type, Converter converter) {
        getConverters(type.getClassLoader()).put(type, converter);
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
    public static void toString(Class<?> toStringClass) {
        setConverter(toStringClass, ToStringConverter.INSTANCE);
    }

    /**
     * Get the object converter for the given object type.
     * 
     * @param type
     *            The object type.
     * @return The object converter.
     */
    public static Converter getConverter(Class<?> type) {
        if (type.isArray()) {
            return ArrayConverter.INSTANCE;
        }
        if (type.isPrimitive()) {
            return NullConverter.INSTANCE;
        }
        boolean encache = false;
        Class<?> iterator = type;
        Map<Class<?>, Converter> converters = getConverters(type.getClassLoader());
        for (;;) {
            Converter converter = converters.get(iterator);
            if (converter == null) {
                encache = true;
                converter = interfaceConverter(converters, iterator.getInterfaces());
            }
            if (converter != null) {
                if (encache) {
                    converters.put(type, converter);
                }
                return converter;
            }
            encache = true;
            iterator = iterator.getSuperclass();
        }
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
    public static Object flatten(Object object, Set<String> includes) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).convert(object, new StringBuilder(), includes);
    }

    public static Object flatten(Object object) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).convert(object, new StringBuilder(), Collections.singleton("\0"));
    }

    public static Object flatten(Object object, boolean recurse) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).convert(object, new StringBuilder(), recurse ? Collections.<String>emptySet() : Collections.singleton("\0"));
    }

    public static Object flatten(Object object, String...includes) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).convert(object, new StringBuilder(), new HashSet<String>(Arrays.asList(includes)));
    }

    private static Converter interfaceConverter(Map<Class<?>, Converter> converters, Class<?>[] ifaces) {
        LinkedList<Class<?>> queue = new LinkedList<Class<?>>(Arrays.asList(ifaces));
        while (!queue.isEmpty()) {
            Class<?> iface = queue.removeFirst();
            Converter converter = converters.get(iface);
            if (converter != null) {
                return converter;
            }
            queue.addAll(Arrays.asList((Class<?>[]) iface.getInterfaces()));
        }
        return null;
    }

}
