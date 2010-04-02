package com.goodworkalan.diffuse;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Converts an object into a <code>java.util.Map</code> where fields and Java
 * Bean properties are the entries in the map.
 * 
 * @author Alan Gutierrez
 */
class BeanDiffuser implements ObjectDiffuser {
    /** The singleton instance of the bean converter. */
    public final static ObjectDiffuser INSTANCE = new BeanDiffuser();

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
    public Object diffuse(Diffuser diffuse, Object object, StringBuilder path, Set<String> includes) {
        return  Collections.unmodifiableMap(modifiable(diffuse, object, path, includes));
    }
    
    protected Map<String, Object> modifiable(Diffuser diffuse, Object object, StringBuilder path, Set<String> includes) {
        Class<?> beanClass = object.getClass();
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(beanClass);
        } catch (IntrospectionException e) {
            throw new DiffuseException(BeanDiffuser.class, "getBeanInfo", e);
        }
        int index = path.length();
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            Method read = descriptor.getReadMethod();
            if (read != null) {
                String name = descriptor.getName();
                path.append(name);
                ObjectDiffuser converter = diffuse.getConverter(read.getReturnType());
                if (converter.isScalar() || includes.isEmpty() || includes.contains(path.toString())) {
                    Object value;
                    try {
                        value = read.invoke(object);
                    } catch (Exception e) {
                        throw new DiffuseException(BeanDiffuser.class, "read", e);
                    }
                    if (value == null) {
                        properties.put(name, value);
                    } else {
                        path.append(".");
                        properties.put(name, converter.diffuse(diffuse, value, path, includes));
                    }
                }
                path.setLength(index);
            }
        }
        for (Field field : beanClass.getFields()) {
            ObjectDiffuser converter = diffuse.getConverter(field.getType());
            String name = field.getName();
            path.append(name);
            if (converter.isScalar() || includes.isEmpty() || includes.contains(path.toString())) {
                Object value;
                try {
                    value = field.get(object);
                } catch (Exception e) {
                    throw new DiffuseException(BeanDiffuser.class, "get", e);
                }
                if (value == null) {
                    properties.put(name, value);
                } else {
                    path.append(".");
                    properties.put(name, converter.diffuse(diffuse, value, path, includes));
                }
            }
            path.setLength(index);
        }
        return properties;
    }

    /**
     * Return false indicating that this is a diffuser for containers of other
     * objects and not a scalar.
     * 
     * @return False to indicate that this is not a scalar converter.
     */
    public boolean isScalar() {
        return false;
    }
}
