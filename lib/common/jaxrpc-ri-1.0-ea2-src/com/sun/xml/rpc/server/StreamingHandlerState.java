// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StreamingHandlerState.java

package com.sun.xml.rpc.server;

import com.sun.xml.rpc.soap.message.InternalSOAPMessage;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;

public class StreamingHandlerState {

    private SOAPMessageContext _context;
    private InternalSOAPMessage _request;
    private InternalSOAPMessage _response;

    public StreamingHandlerState(SOAPMessageContext context) {
        _context = context;
        _request = new InternalSOAPMessage(_context.getMessage());
        _response = null;
    }

    public SOAPMessageContext getMessageContext() {
        return _context;
    }

    public boolean isFailure() {
        if(_response == null)
            return false;
        else
            return _response.isFailure();
    }

    public InternalSOAPMessage getRequest() {
        return _request;
    }

    public InternalSOAPMessage getResponse() {
        if(_response == null)
            _response = new InternalSOAPMessage(_context.createMessage());
        return _response;
    }

    public InternalSOAPMessage resetResponse() {
        _response = null;
        return getResponse();
    }
}
