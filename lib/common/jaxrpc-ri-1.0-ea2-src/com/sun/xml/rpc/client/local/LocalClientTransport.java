// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LocalClientTransport.java

package com.sun.xml.rpc.client.local;

import com.sun.xml.rpc.client.ClientTransport;
import com.sun.xml.rpc.client.ClientTransportException;
import com.sun.xml.rpc.soap.message.Handler;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.localization.Localizable;
import java.io.OutputStream;
import javax.xml.soap.SOAPMessage;

public class LocalClientTransport
    implements ClientTransport {

    private Handler _handler;
    private OutputStream _logStream;

    public LocalClientTransport(Handler handler) {
        _handler = handler;
    }

    public LocalClientTransport(Handler handler, OutputStream logStream) {
        _handler = handler;
        _logStream = logStream;
    }

    public void invoke(String endpoint, SOAPMessageContext context) {
        try {
            if(_logStream != null) {
                String s = "\n******************\nRequest\n";
                _logStream.write(s.getBytes());
                context.getMessage().writeTo(_logStream);
            }
            _handler.handle(context);
            context.setFailure(false);
            if(_logStream != null) {
                String s = "\nResponse\n";
                _logStream.write(s.getBytes());
                context.getMessage().writeTo(_logStream);
                s = "\n******************\n\n";
                _logStream.write(s.getBytes());
            }
        }
        catch(Exception e) {
            if(e instanceof Localizable)
                throw new ClientTransportException("local.client.failed", (Localizable)e);
            else
                throw new ClientTransportException("local.client.failed", new LocalizableExceptionAdapter(e));
        }
    }

    public void invokeOneWay(String endpoint, SOAPMessageContext context) {
        try {
            if(_logStream != null) {
                String s = "\n******************\nRequest\n";
                _logStream.write(s.getBytes());
                context.getMessage().writeTo(_logStream);
            }
            _handler.handle(context);
            context.setFailure(false);
        }
        catch(Exception e) {
            if(e instanceof Localizable)
                throw new ClientTransportException("local.client.failed", (Localizable)e);
            else
                throw new ClientTransportException("local.client.failed", new LocalizableExceptionAdapter(e));
        }
    }
}
