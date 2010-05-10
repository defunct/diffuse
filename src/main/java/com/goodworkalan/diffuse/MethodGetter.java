package com.goodworkalan.diffuse;

import com.goodworkalan.reflective.Method;
import com.goodworkalan.reflective.ReflectiveException;

/**
 * A proeprty getter that reads a getter method.
 *
 * @author Alan Gutierrez
 */
class MethodGetter implements Getter {
    /** The getter method. */
    private final Method method;

    /** The property name. */
    private final String name;
    
    /**
     * Create a new method getter with the given name using the given no
     * argument getter method.
     * 
     * @param method
     *            The getter method.
     * @param name
     *            The property name.
     */
    public MethodGetter(Method method, String name) {
        this.method = method;
        this.name = name;
    }
    
    /**
     * Get the value using the getter method from the given object.
     * 
     * @param object The object.
     * @return The value obtained from the getter method.
     */
    public Object get(Object object) {
        try {
            return method.invoke(object);
        } catch (ReflectiveException e) {
            throw new DiffuseException(BeanDiffuser.class, "methodGet", getName(), method.getNative().getName(), object.getClass());
        }
    }

    /**
     * Get the property name.
     * 
     * @return The property name.
     */
    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return method.getNative().getReturnType();
    }
}
