package com.goodworkalan.diffuse;

import java.util.Set;

// FIXME Should this be diffuser? If so, what do we call diffuse?
public interface Converter {
    public Object convert(Diffuse diffuse, Object object, StringBuilder path, Set<String> includes);
    
    public boolean isContainer();
}