// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HandlerRegistry.java

package javax.xml.rpc.handler;

import java.io.Serializable;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package javax.xml.rpc.handler:
//            HandlerChain

public interface HandlerRegistry
    extends Serializable {

    public abstract HandlerChain getHandlerChain(QName qname);

    public abstract void setHandlerChain(QName qname, HandlerChain handlerchain);
}
