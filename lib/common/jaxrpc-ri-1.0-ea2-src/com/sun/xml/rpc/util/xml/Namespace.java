// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Namespace.java

package com.sun.xml.rpc.util.xml;


public final class Namespace {

    private String _prefix;
    private String _uri;

    private Namespace(String prefix, String uri) {
        _prefix = prefix;
        _uri = uri;
    }

    public String getPrefix() {
        return _prefix;
    }

    public String getURI() {
        return _uri;
    }

    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof Namespace)) {
            return false;
        } else {
            Namespace namespace = (Namespace)obj;
            return _prefix.equals(namespace._prefix) && _uri.equals(namespace._uri);
        }
    }

    public int hashCode() {
        return _prefix.hashCode() ^ _uri.hashCode();
    }

    public static Namespace getNamespace(String prefix, String uri) {
        if(prefix == null)
            prefix = "";
        if(uri == null)
            uri = "";
        return new Namespace(prefix, uri);
    }
}
