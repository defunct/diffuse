package com.goodworkalan.diffuse;

import org.testng.annotations.Test;

/**
 * Unit tests for the {@link BeanDiffuser} class.
 *
 * @author Alan Gutierrez
 */
public class BeanDiffuserTest {
    /** Test reflection exception on field get. */
    @Test(expectedExceptions = DiffuseException.class)
    public void failedGet() throws SecurityException, NoSuchFieldException {
        new DiffuseExceptionCatcher(new Runnable() {
            public void run() {
                new Diffuser().diffuse(new BeanOfEvil());
            }
        }, "BeanDiffuser/getter", "Unable to get the field [evil] of type [java.lang.String] from an object of class [com.goodworkalan.diffuse.BeanOfEvil].").run();
    }

}
