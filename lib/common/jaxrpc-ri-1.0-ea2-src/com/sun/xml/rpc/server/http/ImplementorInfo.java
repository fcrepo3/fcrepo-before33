// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ImplementorInfo.java

package com.sun.xml.rpc.server.http;


public class ImplementorInfo {

    private Class _tieClass;
    private Class _servantClass;

    public ImplementorInfo(Class tieClass, Class servantClass) {
        _tieClass = tieClass;
        _servantClass = servantClass;
    }

    public Class getTieClass() {
        return _tieClass;
    }

    public Class getServantClass() {
        return _servantClass;
    }
}
