// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StringUtils.java

package com.sun.xml.rpc.processor.util;


public class StringUtils {

    public StringUtils() {
    }

    public static String decapitalize(String name) {
        if(name == null || name.length() == 0)
            return name;
        if(name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))) {
            return name;
        } else {
            char chars[] = name.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            return new String(chars);
        }
    }

    public static String capitalize(String name) {
        if(name == null || name.length() == 0) {
            return name;
        } else {
            char chars[] = name.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);
        }
    }
}
