package com.goodworkalan.diffuse;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


// FIXME Move all of this to a library called diffuse (make weaker, thinner.)
public class MapConverter implements Converter {
    public final static MapConverter INSTANCE = new MapConverter();

    public Object convert(Object object, StringBuilder path, Set<String> includes) {
        return Collections.unmodifiableMap(modifiable(object, path, includes));
    }
    
    public Map<String, Object> modifiable(Object object, StringBuilder path, Set<String> includes) {
        int index = path.length();
        Map<?, ?> original = (Map<?, ?>) object;
        Map<String, Object> copy = new LinkedHashMap<String, Object>();
        for (Map.Entry<?, ?> entry : original.entrySet()) {
            String name = entry.getKey().toString();
            path.append(name);
            Object value = entry.getValue();
            if (value == null) {
                copy.put(name, value);
            } else {
                Converter converter = Diffuse.getConverter(value.getClass());
                if (!converter.isContainer() || includes.isEmpty() || includes.contains(path.toString())) {
                    path.append(".");
                    copy.put(name, converter.convert(value, path, includes));
                }
            }
            path.setLength(index);
        }
        return copy;
    }
    
    public boolean isContainer() {
        return true;
    }
}
