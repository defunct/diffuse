package com.goodworkalan.diffuse;

import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import com.goodworkalan.danger.ContextualDanger;

/**
 * Used contextual messages since there are only a few errors and those have to
 * do with reflection, so they are generally unrecoverable. There is no connection
 * to the network or writing to file, so most errors can 
 *
 * @author Alan Gutierrez
 */
@SuppressWarnings("serial")
public class DiffuseException extends ContextualDanger {
    /** A cache of resource bundles. */
    private final static ConcurrentHashMap<String, ResourceBundle> bundles = new ConcurrentHashMap<String, ResourceBundle>();

    public DiffuseException(Class<?> context, String code, Object...arguments) {
        super(bundles, context, code, null);
    }
    
    public DiffuseException(Class<?> context, String code, Throwable cause, Object...arguments) {
        super(bundles, context, code, cause);
    }
}
