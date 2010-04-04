package com.goodworkalan.diffuse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;


class CollectionConverter implements ObjectDiffuser {
    public final static ObjectDiffuser INSTANCE = new CollectionConverter();
    
    public Object diffuse(Diffuser diffuse, Object object, StringBuilder path, Set<String> includes) {
        path.append("*.");
        int index = path.length();
        Collection<?> original = (Collection<?>) object;
        List<Object> copy = new ArrayList<Object>();
        for (Object item : original) {
            if (item == null) {
                copy.add(item);
            } else {
                copy.add(diffuse.getConverter(item.getClass()).diffuse(diffuse, item, path, includes));
                path.setLength(index);
            }
        }
        return Collections.unmodifiableList(copy);
    }
    
    public boolean isContainer() {
        return true;
    }
}
