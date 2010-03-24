package com.goodworkalan.diffuse;

public interface ClassAsssociation<V> {
    public V get(Object key);
    
    public V put(Class<?> type, V value);
}
