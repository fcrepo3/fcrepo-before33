// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Handler.java

package com.sun.xml.rpc.soap.message;


// Referenced classes of package com.sun.xml.rpc.soap.message:
//            SOAPMessageContext

public interface Handler {

    public abstract void handle(SOAPMessageContext soapmessagecontext) throws Exception;
}
