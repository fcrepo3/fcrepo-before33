// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Localizable.java

package com.sun.xml.rpc.util.localization;


public interface Localizable {

    public abstract String getKey();

    public abstract Object[] getArguments();

    public abstract String getResourceBundleName();
}
