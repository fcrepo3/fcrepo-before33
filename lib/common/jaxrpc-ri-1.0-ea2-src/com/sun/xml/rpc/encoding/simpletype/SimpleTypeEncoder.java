// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SimpleTypeEncoder.java

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

public interface SimpleTypeEncoder {

    public abstract String objectToString(Object obj, XMLWriter xmlwriter) throws Exception;

    public abstract Object stringToObject(String s, XMLReader xmlreader) throws Exception;

    public abstract void writeAdditionalNamespaceDeclarations(Object obj, XMLWriter xmlwriter) throws Exception;
}
