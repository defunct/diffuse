package com.goodworkalan.diffuse;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MapConverter implements Converter {
    public final static MapConverter INSTANCE = new MapConverter();

    public Object convert(Diffuse diffuse, Object object, StringBuilder path, Set<String> includes) {
        return Collections.unmodifiableMap(modifiable(diffuse, object, path, includes));
    }
    
    public Map<String, Object> modifiable(Diffuse diffuse, Object object, StringBuilder path, Set<String> includes) {
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
                Converter converter = diffuse.getConverter(value.getClass());
                if (!converter.isContainer() || includes.isEmpty() || includes.contains(path.toString())) {
                    path.append(".");
                    copy.put(name, converter.convert(diffuse, value, path, includes));
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
