package com.goodworkalan.diffuse;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A widget.
 *
 * @author Alan Gutierrez
 */
public class Widget {
    /** The nested widget. */
    public Widget widget;
    
    /** The other widget. */
    private Widget other;
    
    /** The text for testing string diffusion. */
    public String string;
    
    /** The type for testing class diffusion. */
    public Class<?> type;
    
    /** The list for testing list diffusion. */
    public List<String> list = new ArrayList<String>();

    /** The map for testing map diffusion. */
    public Map<String, Object> map = new HashMap<String, Object>();
    
    /** A file for testing to string diffusion. */
    public File file;

    /** A date for testing to time diffusion. */
    public Date date;

    /** A getter for the other <code>Widget</code> for testing bean properties. */
    public Widget getOther() {
        return other;
    }
    
    /** A getter an integer for testing bean properties. */
    public int getInteger() {
        return 1;
    }

    /** A setter for the other <code>Widget</code> for testing bean properties. */
    public void setOther(Widget other) {
        this.other = other;
    }
    
    /** Do nothing setter. */
    public void setNothing(String nothing) {
    }
}
