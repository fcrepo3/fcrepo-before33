// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LocalClientTransportFactory.java

package com.sun.xml.rpc.client.local;

import com.sun.xml.rpc.client.ClientTransport;
import com.sun.xml.rpc.client.ClientTransportFactory;
import com.sun.xml.rpc.soap.message.Handler;
import java.io.OutputStream;

// Referenced classes of package com.sun.xml.rpc.client.local:
//            LocalClientTransport

public class LocalClientTransportFactory
    implements ClientTransportFactory {

    private Handler _handler;
    private OutputStream _logStream;

    public LocalClientTransportFactory(Handler handler) {
        _handler = handler;
        _logStream = null;
    }

    public LocalClientTransportFactory(Handler handler, OutputStream logStream) {
        _handler = handler;
        _logStream = logStream;
    }

    public ClientTransport create() {
        return new LocalClientTransport(_handler, _logStream);
    }
}
