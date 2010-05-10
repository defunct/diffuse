package com.goodworkalan.diffuse;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Widget {
    public Widget widget;
    
    private Widget other;
    
    public String string;
    
    public Class<?> type;
    
    public List<String> list = new ArrayList<String>();

    public Map<String, Object> map = new HashMap<String, Object>();
    
    public File file;

    public Date date;

    public Widget getOther() {
        return other;
    }
    
    public int getInteger() {
        return 1;
    }

    public void setOther(Widget other) {
        this.other = other;
    }
    
    public void setNothing(String nothing) {
    }
}
