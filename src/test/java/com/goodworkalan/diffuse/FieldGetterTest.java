package com.goodworkalan.diffuse;

import org.testng.annotations.Test;

import com.goodworkalan.reflective.Field;
import com.goodworkalan.reflective.ReflectiveException;

/**
 * Unit tests for the {@link FieldGetter} class.
 *
 * @author Alan Gutierrez
 */
public class FieldGetterTest {
    /** Test reflection exception on field get. */
    @Test(expectedExceptions = DiffuseException.class)
    public void failedGet() throws SecurityException, NoSuchFieldException {
        final java.lang.reflect.Field field = Widget.class.getField("widget");
        new DiffuseExceptionCatcher(new Runnable() {
            public void run() {
                new FieldGetter(new Field(field) {
                    @Override
                    public Object get(Object object) throws ReflectiveException {
                       try {
                           throw new IllegalArgumentException();
                       } catch (IllegalArgumentException e) {
                           throw new ReflectiveException(ReflectiveException.ILLEGAL_ARGUMENT, e);
                       }
                    } 
                }).get(new Widget());
            }
        }, "BeanDiffuser/fieldGet", "Unable to get the field [widget] from an object of class [com.goodworkalan.diffuse.Widget].").run();
    }
}
