package com.goodworkalan.diffuse;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.goodworkalan.utility.ClassAssociation;

/**
 * The root diffuser used to map classes to object diffusers and initiate the
 * diffusion of object graphs. The output will be an object tree composed of
 * maps, lists and scalars, where scalars are primitives or strings. The maps
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
    private final ClassAssociation<ObjectDiffuser> associations = new ClassAssociation<ObjectDiffuser>();
 
    /**
     * Create a diffuser with reasonable defaults for the most common types. The
     * default object diffuser, if no other diffusers matches is the
     * <code>BeanDiffusers</code>.
     */
    public Diffuser() {
        associations.derived(Byte.class, NullDiffuser.INSTANCE);
        associations.derived(Boolean.class, NullDiffuser.INSTANCE);
        associations.derived(Short.class, NullDiffuser.INSTANCE);
        associations.derived(Character.class, NullDiffuser.INSTANCE);
        associations.derived(Integer.class, NullDiffuser.INSTANCE);
        associations.derived(Long.class, NullDiffuser.INSTANCE);
        associations.derived(Float.class, NullDiffuser.INSTANCE);
        associations.derived(Double.class, NullDiffuser.INSTANCE);
        associations.derived(String.class, NullDiffuser.INSTANCE);
        associations.derived(Object.class, BeanDiffuser.INSTANCE);
        associations.derived(Map.class, MapDiffuser.INSTANCE);
        associations.derived(Collection.class, CollectionConverter.INSTANCE);
        associations.derived(File.class, ToStringDiffuser.INSTANCE);
        associations.derived(URL.class, ToStringDiffuser.INSTANCE);
        associations.derived(URI.class, ToStringDiffuser.INSTANCE);
        associations.derived(Class.class, ClassDiffuser.INSTANCE);
        associations.derived(CharSequence.class, ToStringDiffuser.INSTANCE);
        associations.derived(StringWriter.class, ToStringDiffuser.INSTANCE);
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
        associations.derived(type, converter);
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
        return associations.get(type);
    }

    /**
     * Perform a recursive diffusion of the given <code>object</code> that
     * includes only the container objects that match one of the paths in the
     * set of paths given in <code>includes</code>.
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

    /**
     * Perform a shallow diffusion of the given <code>object</code>, which
     * creates a diffused object that does not include and members that are
     * containers of other objects.
     * <p>
     * FIXME Should default be to just recurse? If so than this makes sense,
     * because the vararg would be the empty set.
     * <p>
     * FIXME Maybe rename shallow.
     * 
     * @param object
     *            The object to diffuse.
     * @return A representation of the object that is either a map, list or
     *         scalar, where a scalar is a primitive or string.
     */
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
        return getConverter(object.getClass()).diffuse(this, object, new StringBuilder(), recurse ? Collections.<String> emptySet() : Collections.singleton("\0"));
    }

    public Object diffuse(Object object, String... includes) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).diffuse(this, object, new StringBuilder(), new HashSet<String>(Arrays.asList(includes)));
    }
}
