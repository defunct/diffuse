package com.goodworkalan.diffuse;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentClassAssociation<V>
    extends ConcurrentHashMap<Class<?>, V>
    implements ClassAsssociation<V> {
    /** Default serial version id. */
    private static final long serialVersionUID = 1L;
}
