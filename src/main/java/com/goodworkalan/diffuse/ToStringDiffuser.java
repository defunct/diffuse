package com.goodworkalan.diffuse;

import java.util.Set;



public class ToStringDiffuser implements ObjectDiffuser {
    public final static ObjectDiffuser INSTANCE = new ToStringDiffuser();

    public Object convert(Diffuser diffuse, Object object, StringBuilder path, Set<String> includes) {
        return object.toString();
    }
    
    public boolean isContainer() {
        return false;
    }
}
