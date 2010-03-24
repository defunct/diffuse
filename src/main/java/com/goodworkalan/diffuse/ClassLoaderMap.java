package com.goodworkalan.diffuse;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

public class ClassLoaderMap<V> extends AbstractMap<ClassLoader, V> {
    @Override
    public Set<Map.Entry<ClassLoader, V>> entrySet() {
        return null;
    }
}
