// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SimpleToBoxedUtil.java

package com.sun.xml.rpc.processor.generator;

import java.util.HashSet;
import java.util.Set;

public final class SimpleToBoxedUtil {

    static Set primitiveSet;

    public SimpleToBoxedUtil() {
    }

    public static String getBoxedExpressionOfType(String s, String c) {
        if(isPrimitive(c)) {
            StringBuffer sb = new StringBuffer();
            sb.append("new ");
            sb.append(getBoxedClassName(c));
            sb.append('(');
            sb.append(s);
            sb.append(')');
            return sb.toString();
        } else {
            return s;
        }
    }

    public static String getUnboxedExpressionOfType(String s, String c) {
        if(isPrimitive(c)) {
            StringBuffer sb = new StringBuffer();
            sb.append('(');
            sb.append(s);
            sb.append(").");
            sb.append(c);
            sb.append("Value()");
            return sb.toString();
        } else {
            return s;
        }
    }

    public static String convertExpressionFromTypeToType(String s, String from, String to) throws Exception {
        if(from.equals(to))
            return s;
        if(!isPrimitive(to) && isPrimitive(from))
            return getBoxedExpressionOfType(s, from);
        if(isPrimitive(to) && isPrimitive(from))
            return getUnboxedExpressionOfType(s, to);
        else
            return s;
    }

    public static String getBoxedClassName(String className) {
        if(isPrimitive(className)) {
            StringBuffer sb = new StringBuffer();
            if(className.equals(Integer.TYPE.getName()))
                sb.append("Integer");
            else
            if(className.equals(Character.TYPE.getName())) {
                sb.append("Character");
            } else {
                sb.append(Character.toUpperCase(className.charAt(0)));
                sb.append(className.substring(1));
            }
            return sb.toString();
        } else {
            return className;
        }
    }

    public static boolean isPrimitive(String className) {
        return primitiveSet.contains(className);
    }

    static  {
        primitiveSet = null;
        primitiveSet = new HashSet();
        primitiveSet.add("boolean");
        primitiveSet.add("byte");
        primitiveSet.add("double");
        primitiveSet.add("float");
        primitiveSet.add("int");
        primitiveSet.add("long");
        primitiveSet.add("short");
    }
}
