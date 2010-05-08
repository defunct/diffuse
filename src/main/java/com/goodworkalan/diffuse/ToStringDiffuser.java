package com.goodworkalan.diffuse;

import java.util.Set;

// TODO Document.
public class ToStringDiffuser implements ObjectDiffuser {
    // TODO Document.
    public final static ObjectDiffuser INSTANCE = new ToStringDiffuser();

    // TODO Document.
    public Object diffuse(Diffuser diffuse, Object object, StringBuilder path, Set<String> includes) {
        return object.toString();
    }
    
    // TODO Document.
    public boolean isContainer() {
        return false;
    }
}
