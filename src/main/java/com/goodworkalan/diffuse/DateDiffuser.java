package com.goodworkalan.diffuse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;



// TODO Document.
public class DateDiffuser implements ObjectDiffuser {
    // TODO Document.
    public static ObjectDiffuser INSTANCE = new DateDiffuser(); 
        
    // TODO Document.
    private static ThreadLocal<DateFormat> formats = new ThreadLocal<DateFormat>();
    
    // TODO Document.
    public Object diffuse(Diffuser diffuse, Object object, StringBuilder path, Set<String> includes) {
        DateFormat format = formats.get();
        if (format == null) {
            format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            formats.set(format);
        }
        return format.format((Date) object);
    }

    // TODO Document.
    public boolean isContainer() {
        return false;
    }
}
