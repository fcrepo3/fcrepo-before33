// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Debug.java

package com.sun.xml.rpc.util;

import java.io.PrintStream;

public final class Debug {

    protected static final boolean _enabled = true;

    public Debug() {
    }

    public static void println(String s) {
        System.out.println(s);
    }

    public static boolean enabled() {
        return true;
    }
}
