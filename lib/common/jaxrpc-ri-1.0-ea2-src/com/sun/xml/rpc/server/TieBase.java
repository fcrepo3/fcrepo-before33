// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   TieBase.java

package com.sun.xml.rpc.server;

import com.sun.xml.rpc.client.HandlerChainImpl;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistryImpl;
import java.rmi.Remote;
import javax.xml.rpc.encoding.TypeMappingRegistry;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerChain;

// Referenced classes of package com.sun.xml.rpc.server:
//            StreamingHandler, Tie

public abstract class TieBase extends StreamingHandler
    implements Tie {

    protected TypeMappingRegistry typeMappingRegistry;
    protected InternalTypeMappingRegistry internalTypeMappingRegistry;
    protected HandlerChain handlerChain;
    private Remote _servant;

    protected HandlerChain getHandlerChain() {
        return handlerChain;
    }

    protected TieBase(TypeMappingRegistry registry) throws Exception {
        typeMappingRegistry = registry;
        internalTypeMappingRegistry = new InternalTypeMappingRegistryImpl(registry);
        handlerChain = new HandlerChainImpl();
    }

    public void setTarget(Remote servant) {
        _servant = servant;
    }

    public Remote getTarget() {
        return _servant;
    }

    public void destroy() {
        handlerChain.destroy();
    }
}
