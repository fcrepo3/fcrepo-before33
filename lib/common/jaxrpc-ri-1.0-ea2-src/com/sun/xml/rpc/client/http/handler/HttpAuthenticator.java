// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HttpAuthenticator.java

package com.sun.xml.rpc.client.http.handler;

import java.net.URL;

public interface HttpAuthenticator {

    public abstract boolean schemeSupported(String s);

    public abstract String authString(URL url, String s, String s1);
}
