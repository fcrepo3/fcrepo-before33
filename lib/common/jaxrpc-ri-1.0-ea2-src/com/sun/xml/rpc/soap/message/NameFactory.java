// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   NameFactory.java

package com.sun.xml.rpc.soap.message;

import javax.xml.soap.Name;

public interface NameFactory {

    public abstract Name createName(String s, String s1, String s2);

    public abstract Name createName(String s);
}
