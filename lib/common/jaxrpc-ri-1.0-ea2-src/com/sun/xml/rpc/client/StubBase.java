// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StubBase.java

package com.sun.xml.rpc.client;

import com.sun.xml.rpc.client.http.HttpClientTransportFactory;
import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import java.util.*;
import javax.xml.rpc.Stub;
import javax.xml.rpc.handler.HandlerChain;

// Referenced classes of package com.sun.xml.rpc.client:
//            StreamingSender, SenderException, StreamingSenderState, ClientTransportFactory, 
//            ClientTransport

public abstract class StubBase extends StreamingSender
    implements Stub, SerializerConstants, _Initializable {

    protected HandlerChain _handlerChain;
    private Map _properties;
    private boolean _mustInitialize;
    private ClientTransport _transport;
    private ClientTransportFactory _transportFactory;
    protected static final Set _recognizedProperties;

    protected StubBase(HandlerChain handlerChain) {
        _properties = new HashMap();
        _mustInitialize = true;
        _handlerChain = handlerChain;
    }

    public void _setProperty(String name, Object value) {
        if(!_recognizedProperties.contains(name)) {
            throw new IllegalArgumentException("Call object does not recognize property: " + name);
        } else {
            _properties.put(name, value);
            return;
        }
    }

    public Object _getProperty(String name) {
        return _properties.get(name);
    }

    public Iterator _getPropertyNames() {
        return _properties.keySet().iterator();
    }

    public void _initialize(InternalTypeMappingRegistry registry) throws Exception {
        _mustInitialize = false;
    }

    protected void _preSendingHook(StreamingSenderState state) throws Exception {
        if(_mustInitialize)
            throw new SenderException("sender.stub.notInitialized");
        SOAPMessageContext messageContext = state.getMessageContext();
        Object tmp = _getProperty("javax.xml.rpc.security.auth.username");
        if(tmp != null)
            messageContext.setProperty("javax.xml.rpc.security.auth.username", tmp);
        tmp = _getProperty("javax.xml.rpc.security.auth.password");
        if(tmp != null)
            messageContext.setProperty("javax.xml.rpc.security.auth.password", tmp);
        tmp = _getProperty("javax.xml.rpc.service.endpoint.address");
        if(tmp != null)
            messageContext.setProperty("javax.xml.rpc.service.endpoint.address", tmp);
        tmp = _getProperty("javax.xml.rpc.http.session.maintain");
        if(tmp != null)
            messageContext.setProperty("javax.xml.rpc.http.session.maintain", tmp);
    }

    protected ClientTransport _getTransport() {
        if(_transport == null)
            _transport = _getTransportFactory().create();
        return _transport;
    }

    public ClientTransportFactory _getTransportFactory() {
        if(_transportFactory == null)
            _transportFactory = new HttpClientTransportFactory();
        return _transportFactory;
    }

    public void _setTransportFactory(ClientTransportFactory f) {
        _transportFactory = f;
        _transport = null;
    }

    static  {
        Set temp = new HashSet();
        temp.add("javax.xml.rpc.security.auth.username");
        temp.add("javax.xml.rpc.security.auth.password");
        temp.add("javax.xml.rpc.service.endpoint.address");
        temp.add("javax.xml.rpc.http.session.maintain");
        _recognizedProperties = Collections.unmodifiableSet(temp);
    }
}
