package audaque.com.pbting.cache.util;

import java.util.ArrayList;
import java.util.List;

/**
 * add the StringUtils class
 * @author pbting
 *
 */
public class StringUtils {
    
    private StringUtils() {
    }
	
    public static final List split(String str, char delimiter) {
        // return no groups if we have an empty string
        if ((str == null) || "".equals(str)) {
            return new ArrayList();
        }

        ArrayList parts = new ArrayList();
        int currentIndex;
        int previousIndex = 0;

        while ((currentIndex = str.indexOf(delimiter, previousIndex)) > 0) {
            String part = str.substring(previousIndex, currentIndex).trim();
            parts.add(part);
            previousIndex = currentIndex + 1;
        }

        parts.add(str.substring(previousIndex, str.length()).trim());

        return parts;
    }
    
   
    public static final boolean hasLength(String s) {
    	return (s != null) && (s.length() > 0);
    }
    
  
    public static final boolean isEmpty(String s) {
        return (s == null) || (s.trim().length() == 0);
    }
}