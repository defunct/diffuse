package com.goodworkalan.diffuse;

import java.util.Set;


public class ClassConverter implements Converter {
    public final static Converter INSTANCE = new ClassConverter();

    public Object convert(Object object, StringBuilder path, Set<String> includes) {
        return ((Class<?>) object).getCanonicalName();
    }
    
    public boolean isContainer() {
        return false;
    }
}
