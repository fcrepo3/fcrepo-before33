// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   RmiServiceInfo.java

package com.sun.xml.rpc.processor.config;

import java.util.*;

// Referenced classes of package com.sun.xml.rpc.processor.config:
//            RmiInterfaceInfo, ModelInfo, RmiModelInfo, HandlerChainInfo

public class RmiServiceInfo {

    private RmiModelInfo parent;
    private String name;
    private List interfaces;
    private String javaPackageName;
    private HandlerChainInfo clientHandlerChainInfo;
    private HandlerChainInfo serverHandlerChainInfo;

    public RmiServiceInfo() {
        interfaces = new ArrayList();
    }

    public RmiModelInfo getParent() {
        return parent;
    }

    public void setParent(RmiModelInfo m) {
        parent = m;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public String getJavaPackageName() {
        return javaPackageName;
    }

    public void setJavaPackageName(String s) {
        javaPackageName = s;
    }

    public void add(RmiInterfaceInfo i) {
        interfaces.add(i);
        i.setParent(this);
    }

    public Iterator getInterfaces() {
        return interfaces.iterator();
    }

    public HandlerChainInfo getClientHandlerChainInfo() {
        if(clientHandlerChainInfo != null)
            return clientHandlerChainInfo;
        else
            return parent.getClientHandlerChainInfo();
    }

    public void setClientHandlerChainInfo(HandlerChainInfo i) {
        clientHandlerChainInfo = i;
    }

    public HandlerChainInfo getServerHandlerChainInfo() {
        if(serverHandlerChainInfo != null)
            return serverHandlerChainInfo;
        else
            return parent.getServerHandlerChainInfo();
    }

    public void setServerHandlerChainInfo(HandlerChainInfo i) {
        serverHandlerChainInfo = i;
    }
}
