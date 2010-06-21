package com.goodworkalan.diffuse;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.goodworkalan.reflective.ReflectiveException;
import com.goodworkalan.reflective.getter.FieldGetter;
import com.goodworkalan.reflective.getter.Getter;
import com.goodworkalan.reflective.getter.MethodGetter;

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
    
    /** The cache of classes to their list of getters. */
    private final static ConcurrentMap<Class<?>, List<Getter>> GETTERS = new ConcurrentHashMap<Class<?>, List<Getter>>();

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
        List<Getter> getters = GETTERS.get(beanClass);
        if (getters == null) {
            Map<String, Getter> properties = new LinkedHashMap<String, Getter>();
            BeanInfo beanInfo = introspect(beanClass, Object.class);
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                java.lang.reflect.Method read = descriptor.getReadMethod();
                if (read != null) {
                    String name = descriptor.getName();
                    properties.put(name, new MethodGetter(read, name));
                }
            }
            for (java.lang.reflect.Field field : beanClass.getFields()) {
                properties.put(field.getName(), new FieldGetter(field));
            }
            getters = new ArrayList<Getter>(properties.values());
            GETTERS.put(beanClass, getters);
        }
        int index = path.length();
        Map<String, Object> diffused = new LinkedHashMap<String, Object>();
        for (Getter getter : getters) {
            String name = getter.getName();
            path.append(name);
            ObjectDiffuser converter = diffuser.getDiffuser(getter.getType());
            if (!converter.isContainer() || includes.isEmpty() || includes.contains(path.toString())) {
                Object value;
                try {
                    value = getter.get(object);
                } catch (ReflectiveException e) {
                    throw new DiffuseException(BeanDiffuser.class, "getter", getter.getName(), getter.getType(), object.getClass());
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
    
    // TODO Document.
    static final BeanInfo introspect(Class<?> beanClass, Class<?> stopClass) {
        try {
            return Introspector.getBeanInfo(beanClass, stopClass);
        } catch (IntrospectionException e) {
            throw new DiffuseException(BeanDiffuser.class, "getBeanInfo", e, beanClass);
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
