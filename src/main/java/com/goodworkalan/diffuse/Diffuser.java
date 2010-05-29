package com.goodworkalan.diffuse;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.goodworkalan.utility.ClassAssociation;

/**
 * The root diffuser used to map classes to object diffusers and initiate the
 * diffusion of object graphs. The output will be an object tree composed solely
 * of maps, lists and scalars, where scalars are primitives or strings. The maps
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
    /** The mapping of classes to their object diffusers. */
    private final ClassAssociation<ObjectDiffuser> associations;
 
    /**
     * Create a diffuser with reasonable defaults for the most common types. The
     * default object diffuser, if no other diffusers matches is the
     * <code>BeanDiffusers</code>.
     */
    public Diffuser() {
        associations = new ClassAssociation<ObjectDiffuser>();
        associations.assignable(Byte.class, NullDiffuser.INSTANCE);
        associations.assignable(Boolean.class, NullDiffuser.INSTANCE);
        associations.assignable(Short.class, NullDiffuser.INSTANCE);
        associations.assignable(Character.class, NullDiffuser.INSTANCE);
        associations.assignable(Integer.class, NullDiffuser.INSTANCE);
        associations.assignable(Long.class, NullDiffuser.INSTANCE);
        associations.assignable(Float.class, NullDiffuser.INSTANCE);
        associations.assignable(Double.class, NullDiffuser.INSTANCE);
        associations.assignable(String.class, NullDiffuser.INSTANCE);
        associations.assignable(Object.class, BeanDiffuser.INSTANCE);
        associations.assignable(Map.class, MapDiffuser.INSTANCE);
        associations.assignable(Collection.class, CollectionDiffuser.INSTANCE);
        associations.assignable(File.class, ToStringDiffuser.INSTANCE);
        associations.assignable(URL.class, ToStringDiffuser.INSTANCE);
        associations.assignable(URI.class, ToStringDiffuser.INSTANCE);
        associations.assignable(Class.class, ClassDiffuser.INSTANCE);
        associations.assignable(CharSequence.class, ToStringDiffuser.INSTANCE);
        associations.assignable(StringWriter.class, ToStringDiffuser.INSTANCE);
        associations.assignable(Date.class, DateDiffuser.INSTANCE);
    }
    
    public Diffuser(Diffuser diffuser) {
        associations = new ClassAssociation<ObjectDiffuser>(diffuser.associations);
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
     * @param diffuser
     *            The object diffuser.
     */
    public void setConverter(Class<?> type, ObjectDiffuser diffuser) {
        associations.assignable(type, diffuser);
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
    public ObjectDiffuser getDiffuser(Class<?> type) {
        if (type.isArray()) {
            return ArrayDiffuser.INSTANCE;
        }
        if (type.isPrimitive()) {
            return NullDiffuser.INSTANCE;
        }
        return associations.get(type);
    }

    /**
     * Diffuse the given object creating a diffused object graph that includes
     * only the child objects that match one of the given include object paths.
     * If no includes are provided, a shallow copy is performed. If any of the
     * include paths are the special path "*", then recursive copy is performed
     * that includes all of the paths.
     * <p>
     * Currently, this method only converts object trees. Actually object graphs
     * will result in endless recursion.
     * 
     * @param object
     *            The object to diffuse.
     * @return A diffused object graph that contains only maps, lists or
     *         scalars, where a scalar is a primitive or string.
     */
    public Object diffuse(Object object, String... includes) {
        if (object == null) {
            return null;
        }
        Set<String> paths = new HashSet<String>(Arrays.asList(includes));
        if (paths.isEmpty()) {
            paths.add("\0");
        }
        if (paths.contains("*")) {
            paths.clear();
        }
        return getDiffuser(object.getClass()).diffuse(this, object, new StringBuilder(), paths);
    }
}
