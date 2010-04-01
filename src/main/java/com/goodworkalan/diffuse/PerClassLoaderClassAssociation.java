package com.goodworkalan.diffuse;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PerClassLoaderClassAssociation<V> implements ClassAsssociation<V>{
    /**
     * The map of maps of object converters, indexed by class loader. The weak
     * hash map associates a map of converters with a class loader. This will
     * allow custom object converters to be garbage collected when a class loader
     * is unloaded and disposed of.
     */
    private final WeakHashMap<ClassLoader, ConcurrentMap<Class<?>, V>> classLoaderConverters = new WeakHashMap<ClassLoader, ConcurrentMap<Class<?>, V>>();
    
    /** The map of the system class loader associated values. */
    private final Map<Class<?>, V> systemValues = new ConcurrentHashMap<Class<?>, V>();


    private Map<Class<?>, V> getClassLoaderConverters(ClassLoader classLoader) {
        synchronized (classLoaderConverters) {
            ConcurrentMap<Class<?>, V> converters = classLoaderConverters.get(classLoader);
            if (converters == null) {
                converters = new ConcurrentHashMap<Class<?>, V>(getConverters(classLoader.getParent()));
                classLoaderConverters.put(classLoader, converters);
            }
            return converters;
        }
    }
    

    private Map<Class<?>, V> getConverters(ClassLoader classLoader) {
        if (classLoader == null) {
            return systemValues;
        }
        return getClassLoaderConverters(classLoader);
    }

    private V interfaceConverter(Map<Class<?>, V> converters, Class<?>[] ifaces) {
        LinkedList<Class<?>> queue = new LinkedList<Class<?>>(Arrays.asList(ifaces));
        while (!queue.isEmpty()) {
            Class<?> iface = queue.removeFirst();
            V converter = converters.get(iface);
            if (converter != null) {
                return converter;
            }
            queue.addAll(Arrays.asList((Class<?>[]) iface.getInterfaces()));
        }
        return null;
    }

    public V put(Class<?> type, V value) {
        return getConverters(type.getClassLoader()).put(type, value);
    }

    /**
     * @exception ClassCastException If the given key is not a <code>Class</code>.
     * @param type The type key.
     * @return The value associated with the given type.
     */
    public V get(Object type) {
        // FIXME Yes. You need to split this up, ClassLoader association, separate
        // from type.
        boolean encache = false;
        Class<?> iterator = (Class<?>) type;
        for (;;) {
            Map<Class<?>, V> converters = getConverters(iterator.getClassLoader());
            V converter = converters.get(iterator);
            if (converter == null) {
                encache = true;
                converter = interfaceConverter(converters, iterator.getInterfaces());
            }
            if (converter != null) {
                if (encache) {
                    converters.put(iterator, converter);
                }
                return converter;
            }
            encache = true;
            iterator = iterator.getSuperclass();
        }
    }

}
