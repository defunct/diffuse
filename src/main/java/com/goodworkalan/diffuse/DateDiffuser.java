package com.goodworkalan.diffuse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Convert a date into an RFC 822 formatted date string.
 *
 * @author Alan Gutierrez
 */
public class DateDiffuser implements ObjectDiffuser {
    /** The singleton date diffuser instance. */
    public static ObjectDiffuser INSTANCE = new DateDiffuser(); 
        
    /** A cache of compiled date formats. */
    private static ThreadLocal<DateFormat> formats = new ThreadLocal<DateFormat>();
    
    /**
     * Convert the given <code>date</code> into an RFC 822 foramtted date string.
     * 
     * @param diffuse The object diffuser provider.
     * @param object The date to diffuse.
     * @param path
     *            The path of the object in the object graph.
     * @param includes
     *            The set of paths to include in the diffused object graph or an
     *            empty set to include all paths.
     * @return 
     */
    public Object diffuse(Diffuser diffuse, Object object, StringBuilder path, Set<String> includes) {
        DateFormat format = formats.get();
        if (format == null) {
            format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            formats.set(format);
        }
        return format.format((Date) object);
    }

    /**
     * Return false indicating that this is a diffuser for a scalar object.
     * 
     * @return False to indicate that this is a scalar diffuser.
     */
    public boolean isContainer() {
        return false;
    }
}
