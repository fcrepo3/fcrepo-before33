// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ModelInfo.java

package com.sun.xml.rpc.processor.config;

import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.modeler.Modeler;
import java.util.Properties;

// Referenced classes of package com.sun.xml.rpc.processor.config:
//            Configuration, TypeMappingRegistryInfo, HandlerChainInfo

public abstract class ModelInfo {

    private Configuration _parent;
    private String _name;
    private TypeMappingRegistryInfo _typeMappingRegistryInfo;
    private HandlerChainInfo _clientHandlerChainInfo;
    private HandlerChainInfo _serverHandlerChainInfo;

    protected ModelInfo() {
    }

    public Configuration getParent() {
        return _parent;
    }

    public void setParent(Configuration c) {
        _parent = c;
    }

    public String getName() {
        return _name;
    }

    public void setName(String s) {
        _name = s;
    }

    public Configuration getConfiguration() {
        return _parent;
    }

    public TypeMappingRegistryInfo getTypeMappingRegistry() {
        return _typeMappingRegistryInfo;
    }

    public void setTypeMappingRegistry(TypeMappingRegistryInfo i) {
        _typeMappingRegistryInfo = i;
    }

    public HandlerChainInfo getClientHandlerChainInfo() {
        return _clientHandlerChainInfo;
    }

    public void setClientHandlerChainInfo(HandlerChainInfo i) {
        _clientHandlerChainInfo = i;
    }

    public HandlerChainInfo getServerHandlerChainInfo() {
        return _serverHandlerChainInfo;
    }

    public void setServerHandlerChainInfo(HandlerChainInfo i) {
        _serverHandlerChainInfo = i;
    }

    public Model buildModel(Properties options) {
        return getModeler(options).buildModel();
    }

    protected abstract Modeler getModeler(Properties properties);
}
