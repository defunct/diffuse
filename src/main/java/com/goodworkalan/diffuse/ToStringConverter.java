package com.goodworkalan.diffuse;

import java.util.Set;



public class ToStringConverter implements Converter {
    public final static Converter INSTANCE = new ToStringConverter();

    public Object convert(Object object, StringBuilder path, Set<String> includes) {
        return object.toString();
    }
    
    public boolean isContainer() {
        return false;
    }
}
