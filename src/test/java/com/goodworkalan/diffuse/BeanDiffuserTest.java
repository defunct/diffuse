package com.goodworkalan.diffuse;

import org.testng.annotations.Test;

/**
 * Unit tests for the {@link BeanDiffuser} class.
 *
 * @author Alan Gutierrez
 */
public class BeanDiffuserTest {
    /** Test failed introspection. */
    @Test(expectedExceptions = DiffuseException.class)
    public void introspection() {
        new DiffuseExceptionCatcher(new Runnable() {
            public void run() {
                BeanDiffuser.introspect(String.class, Number.class);
            }
        }, "BeanDiffuser/getBeanInfo", "Unable to get bean information for class [java.lang.String].").run();
    }
}
