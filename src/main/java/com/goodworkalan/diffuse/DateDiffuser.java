package com.goodworkalan.diffuse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;



public class DateDiffuser implements ObjectDiffuser {
    public static ObjectDiffuser INSTANCE = new DateDiffuser(); 
        
    private static ThreadLocal<DateFormat> formats = new ThreadLocal<DateFormat>();
    
    public Object convert(Diffuser diffuse, Object object, StringBuilder path, Set<String> includes) {
        DateFormat format = formats.get();
        if (format == null) {
            format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            formats.set(format);
        }
        return format.format((Date) object);
    }

    public boolean isContainer() {
        return false;
    }
}
