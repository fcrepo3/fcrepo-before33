// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HttpClientTransportFactory.java

package com.sun.xml.rpc.client.http;

import com.sun.xml.rpc.client.ClientTransport;
import com.sun.xml.rpc.client.ClientTransportFactory;
import java.io.OutputStream;

// Referenced classes of package com.sun.xml.rpc.client.http:
//            HttpClientTransport

public class HttpClientTransportFactory
    implements ClientTransportFactory {

    private OutputStream _logStream;

    public HttpClientTransportFactory() {
        this(null);
    }

    public HttpClientTransportFactory(OutputStream logStream) {
        _logStream = logStream;
    }

    public ClientTransport create() {
        return new HttpClientTransport(_logStream);
    }
}
