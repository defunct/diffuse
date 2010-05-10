package com.goodworkalan.diffuse;

import com.goodworkalan.reflective.Field;
import com.goodworkalan.reflective.ReflectiveException;

/**
 * A getter that reads a field value.
 *
 * @author Alan Gutierrez
 */
class FieldGetter implements Getter {
    /** The field. */
    private final Field field;

    /**
     * Create a field getter from the given field.
     * 
     * @param field
     *            The field.
     */
    public FieldGetter(Field field) {
        this.field = field;
    }

    /**
     * Get the field value from the given object.
     * 
     * @param object
     *            The object.
     * @return The field value.
     */
    public Object get(Object object) {
        try {
            return field.get(object);
        } catch (ReflectiveException e) {
            throw new DiffuseException(BeanDiffuser.class, "fieldGet", e, getName(), object.getClass());
        }
    }

    /**
     * Get the field name.
     * 
     * @return The field name.
     */
    public String getName() {
        return field.getNative().getName();
    }
    
    public Class<?> getType() {
        return field.getNative().getType();
    }
}
