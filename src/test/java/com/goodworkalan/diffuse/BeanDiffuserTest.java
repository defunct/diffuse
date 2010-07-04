package com.goodworkalan.diffuse;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * Unit tests for the {@link BeanDiffuser} class.
 *
 * @author Alan Gutierrez
 */
public class BeanDiffuserTest {
    /** Test reflection exception on field get. */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void failedGet() throws SecurityException, NoSuchFieldException {
        try {
            new Diffuser().diffuse(new BeanOfEvil());
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "\n\tUnable to set bean property.\n\t\tClass: [class com.goodworkalan.diffuse.BeanOfEvil]\n\t\tProperty: [evil], Type[class java.lang.String]");
            throw e;
        }
    }
}
