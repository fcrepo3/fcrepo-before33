// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HttpURLConnection.java

package com.sun.xml.rpc.client.http.handler;

import java.net.Authenticator;
import java.net.InetAddress;
import java.security.PrivilegedAction;

class HttpURLConnection$1
    implements PrivilegedAction {

    private final InetAddress val$addr; /* synthetic field */
    private final int val$port; /* synthetic field */
    private final String val$protocol; /* synthetic field */
    private final String val$prompt; /* synthetic field */
    private final String val$scheme; /* synthetic field */

    HttpURLConnection$1(InetAddress val$addr, int val$port, String val$protocol, String val$prompt, String val$scheme) {
        this.val$addr = val$addr;
        this.val$port = val$port;
        this.val$protocol = val$protocol;
        this.val$prompt = val$prompt;
        this.val$scheme = val$scheme;
    }

    public Object run() {
        return Authenticator.requestPasswordAuthentication(val$addr, val$port, val$protocol, val$prompt, val$scheme);
    }
}
