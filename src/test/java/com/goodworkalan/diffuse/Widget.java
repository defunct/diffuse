package com.goodworkalan.diffuse;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO Document.
public class Widget {
    // TODO Document.
    public Widget widget;
    
    // TODO Document.
    private Widget other;
    
    // TODO Document.
    public String string;
    
    // TODO Document.
    public Class<?> type;
    
    // TODO Document.
    public List<String> list = new ArrayList<String>();

    // TODO Document.
    public Map<String, Object> map = new HashMap<String, Object>();
    
    // TODO Document.
    public File file;

    // TODO Document.
    public Date date;

    // TODO Document.
    public Widget getOther() {
        return other;
    }
    
    // TODO Document.
    public int getInteger() {
        return 1;
    }

    // TODO Document.
    public void setOther(Widget other) {
        this.other = other;
    }
    
    // TODO Document.
    public void setNothing(String nothing) {
    }
}
