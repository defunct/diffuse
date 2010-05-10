package com.goodworkalan.diffuse;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;
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
    
    /** Test diffusion of objects. */
    @Test
    public void bean() {
        Widget widget = new Widget();
        
        widget.widget = new Widget();
        widget.setOther(new Widget());
        widget.string = "a";
        widget.type = String.class;
        widget.list.add("a");
        widget.list.add(null);
        widget.map.put("a", "b");
        widget.map.put("c", null);
        widget.map.put("e", Collections.emptyMap());
        widget.file = new File("a");
        widget.date = new Date();
        Diffuser diffuser = new Diffuser();
        Map<?, ?> map = (Map<?, ?>) diffuser.diffuse(widget, "*");
        assertEquals(map.get("string"), "a");
        map = (Map<?, ?>) diffuser.diffuse(widget);
        assertEquals(map.get("string"), "a");
        assertNull(map.get("widget"));
        assertNull(map.get("other"));
        widget.type = new Object() { }.getClass();
        map = (Map<?, ?>) diffuser.diffuse(widget, "widget");
        assertNotNull(map.get("widget"));
        assertNull(map.get("other"));
        map = (Map<?, ?>) diffuser.diffuse(widget, "other");
        assertNotNull(map.get("other"));
        assertNull(map.get("widget"));
        map = (Map<?, ?>) diffuser.diffuse(widget, "map");
        map = (Map<?, ?>) diffuser.diffuse(widget, "map", "map.e");
        assertEquals(diffuser.diffuse(1), new Integer(1));
        assertEquals(diffuser.diffuse(new File("a")), "a");
        assertNull(diffuser.diffuse(null));
        diffuser.toString(StringBuilder.class);
        assertEquals(diffuser.diffuse(new StringBuilder().append('a')), "a");
    }
}
