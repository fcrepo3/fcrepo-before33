// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HandlerChainInfoData.java

package com.sun.xml.rpc.processor.config.parser;

import com.sun.xml.rpc.processor.config.HandlerChainInfo;

public class HandlerChainInfoData {

    private HandlerChainInfo client;
    private HandlerChainInfo server;

    public HandlerChainInfoData() {
    }

    public HandlerChainInfo getClientHandlerChainInfo() {
        return client;
    }

    public void setClientHandlerChainInfo(HandlerChainInfo i) {
        client = i;
    }

    public HandlerChainInfo getServerHandlerChainInfo() {
        return server;
    }

    public void setServerHandlerChainInfo(HandlerChainInfo i) {
        server = i;
    }
}
