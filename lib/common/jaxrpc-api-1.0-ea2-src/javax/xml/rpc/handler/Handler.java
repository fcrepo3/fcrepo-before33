// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Handler.java

package javax.xml.rpc.handler;

import java.util.Map;
import javax.xml.rpc.soap.SOAPFaultException;

// Referenced classes of package javax.xml.rpc.handler:
//            MessageContext, HandlerChain

public interface Handler {

    public abstract void handleRequest(MessageContext messagecontext, HandlerChain handlerchain) throws SOAPFaultException;

    public abstract void handleResponse(MessageContext messagecontext, HandlerChain handlerchain) throws SOAPFaultException;

    public abstract void init(Map map);

    public abstract void destroy();
}
