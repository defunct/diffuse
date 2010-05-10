package com.goodworkalan.diffuse;

import org.testng.annotations.Test;

import com.goodworkalan.reflective.Method;
import com.goodworkalan.reflective.ReflectiveException;

/**
 * Unit tests for the {@link MethodGetter} class.
 *
 * @author Alan Gutierrez
 */
public class MethodGetterTest {
    /** Test reflection exception on method get.*/
    @Test(expectedExceptions = DiffuseException.class)
    public void failedGet() throws SecurityException, NoSuchMethodException {
        final java.lang.reflect.Method method = Widget.class.getMethod("getOther");
        new DiffuseExceptionCatcher(new Runnable() {
            public void run() {
                new MethodGetter(new Method(method) {
                    @Override
                    public Object invoke(Object obj, Object...args) throws ReflectiveException {
                       try {
                           throw new IllegalArgumentException();
                       } catch (IllegalArgumentException e) {
                           throw new ReflectiveException(ReflectiveException.ILLEGAL_ARGUMENT, e);
                       }
                    } 
                }, "other").get(new Widget());
            }
        }, "BeanDiffuser/methodGet", "Unable to get the field [other] using the getter method [getOther] from an object of class [com.goodworkalan.diffuse.Widget].").run();
    }

}
