// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HttpURLConnection.java

package com.sun.xml.rpc.client.http.handler;

import java.io.InputStream;

class EmptyInputStream extends InputStream {

    EmptyInputStream() {
    }

    public int available() {
        return 0;
    }

    public int read() {
        return -1;
    }
}
