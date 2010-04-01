package com.goodworkalan.diffuse;
import static org.testng.Assert.assertSame;

import java.util.Collections;

import org.testng.annotations.Test;

/**
 * Tests for the null converter.
 * 
 * @author Alan Gutierrez
 */
public class NullDiffuserTest {
    /** Test the null converter. */
    @Test
    public void convert() {
        Object o = new Object();
        assertSame(NullDiffuser.INSTANCE.convert(new Diffuser(), o, new StringBuilder(), Collections.<String>emptySet()), o);
    }
}
