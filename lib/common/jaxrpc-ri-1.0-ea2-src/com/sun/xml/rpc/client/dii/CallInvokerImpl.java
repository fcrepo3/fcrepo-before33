// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   CallInvokerImpl.java

package com.sun.xml.rpc.client.dii;

import com.sun.xml.rpc.client.*;
import com.sun.xml.rpc.client.http.HttpClientTransport;
import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.encoding.soap.SOAPResponseStructure;
import com.sun.xml.rpc.soap.message.*;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.rmi.RemoteException;
import javax.xml.rpc.JAXRPCException;

// Referenced classes of package com.sun.xml.rpc.client.dii:
//            DynamicInvocationException, CallInvoker, CallPropertyConstants, CallRequest, 
//            CallImpl

public class CallInvokerImpl extends StreamingSender
    implements CallInvoker, CallPropertyConstants {

    private static ClientTransportFactory transportFactory = null;
    protected JAXRPCDeserializer faultDeserializer;
    protected JAXRPCDeserializer responseDeserializer;

    public CallInvokerImpl() {
    }

    public static void setTransportFactory(ClientTransportFactory factory) {
        transportFactory = factory;
    }

    public SOAPResponseStructure doInvoke(CallRequest callInfo, JAXRPCSerializer requestSerializer, JAXRPCDeserializer responseDeserializer, JAXRPCDeserializer faultDeserializer) throws RemoteException {
        this.responseDeserializer = responseDeserializer;
        this.faultDeserializer = faultDeserializer;
        try {
            CallImpl call = callInfo.call;
            StreamingSenderState state = setupRequest(callInfo, requestSerializer);
            _send(call.getTargetEndpointAddress(), state);
            SOAPResponseStructure responseStruct = null;
            Object responseObject = state.getResponse().getBody().getValue();
            if(responseObject instanceof SOAPDeserializationState)
                responseStruct = (SOAPResponseStructure)((SOAPDeserializationState)responseObject).getInstance();
            else
                responseStruct = (SOAPResponseStructure)responseObject;
            return responseStruct;
        }
        catch(RemoteException e) {
            throw e;
        }
        catch(Exception e) {
            if(e instanceof RuntimeException)
                throw (RuntimeException)e;
            else
                throw new RemoteException(e.getMessage(), e);
        }
    }

    public void doInvokeOneWay(CallRequest callInfo, JAXRPCSerializer requestSerializer) {
        try {
            CallImpl call = callInfo.call;
            StreamingSenderState state = setupRequest(callInfo, requestSerializer);
            _sendOneWay(call.getTargetEndpointAddress(), state);
        }
        catch(JAXRPCException e) {
            throw e;
        }
        catch(Exception e) {
            if(e instanceof RuntimeException)
                throw (RuntimeException)e;
            else
                throw new DynamicInvocationException(new LocalizableExceptionAdapter(e));
        }
    }

    private StreamingSenderState setupRequest(CallRequest callInfo, JAXRPCSerializer requestSerializer) throws Exception {
        CallImpl call = callInfo.call;
        StreamingSenderState state = _start(call.getHandlerChain());
        InternalSOAPMessage request = state.getRequest();
        SOAPBlockInfo bodyBlock = new SOAPBlockInfo(call.getOperationName());
        bodyBlock.setValue(((Object) (callInfo.parameters)));
        bodyBlock.setSerializer(requestSerializer);
        request.setBody(bodyBlock);
        SOAPMessageContext messageContext = state.getMessageContext();
        Object username = call.getProperty("javax.xml.rpc.security.auth.username");
        if(username != null)
            messageContext.setProperty("javax.xml.rpc.security.auth.username", username);
        Object password = call.getProperty("javax.xml.rpc.security.auth.password");
        if(password != null)
            messageContext.setProperty("javax.xml.rpc.security.auth.password", password);
        Object endpoint = call.getProperty("javax.xml.rpc.service.endpoint.address");
        if(endpoint != null)
            messageContext.setProperty("javax.xml.rpc.service.endpoint.address", endpoint);
        Object operation = call.getProperty("javax.xml.rpc.soap.operation.style");
        if(operation != null)
            messageContext.setProperty("javax.xml.rpc.soap.operation.style", operation);
        Boolean isSOAPActionUsed = (Boolean)call.getRequiredProperty("javax.xml.rpc.soap.http.soapaction.use");
        if(isSOAPActionUsed.booleanValue())
            messageContext.setProperty("http.soap.action", call.getRequiredProperty("javax.xml.rpc.soap.http.soapaction.uri"));
        Object session = call.getProperty("javax.xml.rpc.http.session.maintain");
        if(session != null)
            messageContext.setProperty("javax.xml.rpc.http.session.maintain", session);
        Object encoding = call.getProperty("javax.xml.rpc.encodingstyle.namespace.uri");
        if(encoding != null)
            messageContext.setProperty("javax.xml.rpc.encodingstyle.namespace.uri", encoding);
        return state;
    }

    public ClientTransport _getTransport() {
        if(transportFactory != null)
            return transportFactory.create();
        else
            return new HttpClientTransport();
    }

    protected void _readFirstBodyElement(XMLReader bodyReader, SOAPDeserializationContext deserializationContext, StreamingSenderState state) throws Exception {
        Object responseStructObj = getResponseDeserializer().deserialize(null, bodyReader, deserializationContext);
        SOAPBlockInfo bodyBlock = new SOAPBlockInfo(null);
        bodyBlock.setValue(responseStructObj);
        state.getResponse().setBody(bodyBlock);
    }

    protected JAXRPCDeserializer getFaultDeserializer() {
        return faultDeserializer;
    }

    protected JAXRPCDeserializer getResponseDeserializer() {
        return responseDeserializer;
    }

}
