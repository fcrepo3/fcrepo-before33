// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Handler.java

package com.sun.xml.rpc.client.http.handler;

import java.io.IOException;
import java.net.*;

// Referenced classes of package com.sun.xml.rpc.client.http.handler:
//            HttpURLConnection

public class Handler extends URLStreamHandler {

    protected String proxy;
    protected int proxyPort;

    protected int getDefaultPort() {
        return 80;
    }

    public Handler() {
        proxy = null;
        proxyPort = -1;
    }

    public Handler(String proxy, int port) {
        this.proxy = proxy;
        proxyPort = port;
    }

    protected URLConnection openConnection(URL u) throws IOException {
        return new HttpURLConnection(u, this);
    }
}
