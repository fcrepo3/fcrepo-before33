// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ClientTransport.java

package com.sun.xml.rpc.client;

import com.sun.xml.rpc.soap.message.SOAPMessageContext;

public interface ClientTransport {

    public abstract void invoke(String s, SOAPMessageContext soapmessagecontext);

    public abstract void invokeOneWay(String s, SOAPMessageContext soapmessagecontext);
}
