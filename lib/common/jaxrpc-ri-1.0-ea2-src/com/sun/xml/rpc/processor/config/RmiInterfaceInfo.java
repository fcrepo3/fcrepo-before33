// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   RmiInterfaceInfo.java

package com.sun.xml.rpc.processor.config;


// Referenced classes of package com.sun.xml.rpc.processor.config:
//            RmiServiceInfo, HandlerChainInfo

public class RmiInterfaceInfo {

    private RmiServiceInfo parent;
    private String soapAction;
    private String soapActionBase;
    private String name;
    private String servantName;
    private HandlerChainInfo clientHandlerChainInfo;
    private HandlerChainInfo serverHandlerChainInfo;

    public RmiInterfaceInfo() {
    }

    public RmiServiceInfo getParent() {
        return parent;
    }

    public void setParent(RmiServiceInfo rsi) {
        parent = rsi;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public String getServantName() {
        return servantName;
    }

    public void setServantName(String s) {
        servantName = s;
    }

    public String getSOAPAction() {
        return soapAction;
    }

    public void setSOAPAction(String s) {
        soapAction = s;
    }

    public String getSOAPActionBase() {
        return soapActionBase;
    }

    public void setSOAPActionBase(String s) {
        soapActionBase = s;
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
