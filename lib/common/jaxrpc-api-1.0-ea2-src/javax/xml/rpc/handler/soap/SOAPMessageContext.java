// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPMessageContext.java

package javax.xml.rpc.handler.soap;

import javax.xml.rpc.handler.MessageContext;
import javax.xml.soap.SOAPMessage;

public interface SOAPMessageContext
    extends MessageContext {

    public abstract SOAPMessage getMessage();

    public abstract void setMessage(SOAPMessage soapmessage);
}
