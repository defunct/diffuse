package com.goodworkalan.diffuse;

import java.util.Set;

public class ToStringDiffuser implements ObjectDiffuser {
    public final static ObjectDiffuser INSTANCE = new ToStringDiffuser();

    public Object diffuse(Diffuser diffuse, Object object, StringBuilder path, Set<String> includes) {
        return object.toString();
    }
    
    public boolean isScalar() {
        return true;
    }
}
