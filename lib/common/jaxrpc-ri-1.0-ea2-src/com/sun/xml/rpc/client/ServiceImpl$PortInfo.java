// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ServiceImpl.java

package com.sun.xml.rpc.client;

import java.util.HashMap;
import java.util.Map;
import javax.xml.rpc.handler.HandlerChain;

// Referenced classes of package com.sun.xml.rpc.client:
//            HandlerChainImpl, ServiceImpl

public class ServiceImpl$PortInfo {

    Map operationMap;
    String targetEndpoint;
    String defaultNamespace;
    HandlerChain handlerChain;

    public ServiceImpl$PortInfo() {
        operationMap = new HashMap();
        targetEndpoint = "";
        defaultNamespace = "";
        handlerChain = null;
    }

    public ServiceImpl$OperationInfo getOperation(String operationName) {
        ServiceImpl$OperationInfo operation = (ServiceImpl$OperationInfo)operationMap.get(operationName);
        if(operation == null) {
            operation = new ServiceImpl$OperationInfo();
            operation.setNamespace(defaultNamespace);
            operationMap.put(operationName, operation);
        }
        return operation;
    }

    public void setDefaultNamespace(String namespace) {
        defaultNamespace = namespace;
    }

    public boolean isOperationKnown(String operationName) {
        return operationMap.get(operationName) != null;
    }

    public String getTargetEndpoint() {
        return targetEndpoint;
    }

    public void setTargetEndpoint(String target) {
        targetEndpoint = target;
    }

    public HandlerChain getHandlerChain() {
        if(handlerChain == null)
            handlerChain = new HandlerChainImpl();
        return handlerChain;
    }

    public void setHandlerChain(HandlerChain handlerChain) {
        this.handlerChain = handlerChain;
    }
}
