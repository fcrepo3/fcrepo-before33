// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   CDATA.java

package com.sun.xml.rpc.util.xml;


public final class CDATA {

    private String _text;

    public CDATA(String text) {
        _text = text;
    }

    public String getText() {
        return _text;
    }

    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof CDATA)) {
            return false;
        } else {
            CDATA cdata = (CDATA)obj;
            return _text.equals(cdata._text);
        }
    }

    public int hashCode() {
        return _text.hashCode();
    }
}
