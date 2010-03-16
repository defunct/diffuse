package com.goodworkalan.diffuse;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class BeanConverter implements Converter {
    public final static Converter INSTANCE = new BeanConverter();

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
    public Object convert(Object object, StringBuilder path, Set<String> includes) {
        return  Collections.unmodifiableMap(modifiable(object, path, includes));
    }
    
    protected Map<String, Object> modifiable(Object object, StringBuilder path, Set<String> includes) {
        Class<?> beanClass = object.getClass();
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(beanClass);
        } catch (IntrospectionException e) {
            throw new DiffuseException(BeanConverter.class, "getBeanInfo", e);
        }
        int index = path.length();
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            Method read = descriptor.getReadMethod();
            if (read != null) {
                String name = descriptor.getName();
                path.append(name);
                Converter converter = Diffuse.getConverter(read.getReturnType());
                if (!converter.isContainer() || includes.isEmpty() || includes.contains(path.toString())) {
                    Object value;
                    try {
                        value = read.invoke(object);
                    } catch (Exception e) {
                        throw new DiffuseException(BeanConverter.class, "read", e);
                    }
                    if (value == null) {
                        properties.put(name, value);
                    } else {
                        path.append(".");
                        properties.put(name, converter.convert(value, path, includes));
                    }
                }
                path.setLength(index);
            }
        }
        return properties;
    }
    
    public boolean isContainer() {
        return true;
    }
}
