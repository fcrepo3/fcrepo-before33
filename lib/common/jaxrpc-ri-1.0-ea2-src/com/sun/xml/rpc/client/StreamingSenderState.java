// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StreamingSenderState.java

package com.sun.xml.rpc.client;

import com.sun.xml.rpc.soap.message.InternalSOAPMessage;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import javax.xml.rpc.handler.HandlerChain;

public class StreamingSenderState {

    private SOAPMessageContext _context;
    private InternalSOAPMessage _request;
    private InternalSOAPMessage _response;
    private HandlerChain _handlerChain;

    public StreamingSenderState(SOAPMessageContext context, HandlerChain handlerChain) {
        _context = context;
        _context.setMessage(_context.createMessage());
        _handlerChain = handlerChain;
    }

    public SOAPMessageContext getMessageContext() {
        return _context;
    }

    public boolean isFailure() {
        return _context.isFailure();
    }

    public InternalSOAPMessage getRequest() {
        if(_request == null)
            _request = new InternalSOAPMessage(_context.getMessage());
        return _request;
    }

    public InternalSOAPMessage getResponse() {
        if(_response == null) {
            _response = new InternalSOAPMessage(_context.getMessage());
            _response.setOperationCode(getRequest().getOperationCode());
        }
        return _response;
    }

    public HandlerChain getHandlerChain() {
        return _handlerChain;
    }
}
