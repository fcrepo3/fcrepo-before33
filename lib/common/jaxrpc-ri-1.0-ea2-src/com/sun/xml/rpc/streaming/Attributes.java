// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Attributes.java

package com.sun.xml.rpc.streaming;

import javax.xml.rpc.namespace.QName;

public interface Attributes {

    public abstract int getLength();

    public abstract boolean isNamespaceDeclaration(int i);

    public abstract QName getName(int i);

    public abstract String getURI(int i);

    public abstract String getLocalName(int i);

    public abstract String getPrefix(int i);

    public abstract String getValue(int i);

    public abstract int getIndex(QName qname);

    public abstract int getIndex(String s, String s1);

    public abstract int getIndex(String s);

    public abstract String getValue(QName qname);

    public abstract String getValue(String s, String s1);

    public abstract String getValue(String s);
}
