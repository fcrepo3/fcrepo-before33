// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HttpURLConnection.java

package com.sun.xml.rpc.client.http.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PrivilegedAction;

class HttpURLConnection$2
    implements PrivilegedAction {

    private final String val$h1; /* synthetic field */
    private final String val$h2; /* synthetic field */
    private final boolean val$result[]; /* synthetic field */

    HttpURLConnection$2(String val$h1, String val$h2, boolean val$result[]) {
        this.val$h1 = val$h1;
        this.val$h2 = val$h2;
        this.val$result = val$result;
    }

    public Object run() {
        try {
            InetAddress a1 = InetAddress.getByName(val$h1);
            InetAddress a2 = InetAddress.getByName(val$h2);
            val$result[0] = a1.equals(a2);
        }
        catch(UnknownHostException unknownhostexception) { }
        catch(SecurityException securityexception) { }
        return null;
    }
}
