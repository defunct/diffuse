package com.goodworkalan.diffuse;

import java.util.HashMap;

import org.testng.annotations.Test;

/**
 * Unit tests for the {@link Diffuser} class.
 *
 * @author Alan Gutierrez
 */
public class DiffuserTest {
    /** Test the diffuse method. */
    @Test
    public void diffuse() {
        HashMap<Object, Object> map = new HashMap<Object, Object>();
        map.put("a", 1);
        map.put("b", new String[] { "a", "b", });
        Diffuser diffuser = new Diffuser();
        diffuser.diffuse(map, "*");
    }
}
