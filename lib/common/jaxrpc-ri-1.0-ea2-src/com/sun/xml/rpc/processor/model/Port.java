// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Port.java

package com.sun.xml.rpc.processor.model;

import com.sun.xml.rpc.processor.config.HandlerChainInfo;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.wsdl.framework.ExternalEntityReference;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model:
//            ModelObject, ModelVisitor, Operation

public class Port extends ModelObject {

    private QName _name;
    private List _operations;
    private ExternalEntityReference _portTypeRef;
    private JavaInterface _javaInterface;
    private String _address;
    private HandlerChainInfo _clientHandlerChainInfo;
    private HandlerChainInfo _serverHandlerChainInfo;

    public Port(QName name) {
        _operations = new ArrayList();
        _name = name;
    }

    public QName getName() {
        return _name;
    }

    public void addOperation(Operation operation) {
        _operations.add(operation);
    }

    public Iterator getOperations() {
        return _operations.iterator();
    }

    public ExternalEntityReference getPortTypeRef() {
        return _portTypeRef;
    }

    public void setPortTypeRef(ExternalEntityReference r) {
        _portTypeRef = r;
    }

    public JavaInterface getJavaInterface() {
        return _javaInterface;
    }

    public void setJavaInterface(JavaInterface i) {
        _javaInterface = i;
    }

    public String getAddress() {
        return _address;
    }

    public void setAddress(String s) {
        _address = s;
    }

    public HandlerChainInfo getClientHandlerChainInfo() {
        if(_clientHandlerChainInfo == null)
            _clientHandlerChainInfo = new HandlerChainInfo();
        return _clientHandlerChainInfo;
    }

    public void setClientHandlerChainInfo(HandlerChainInfo i) {
        _clientHandlerChainInfo = i;
    }

    public HandlerChainInfo getServerHandlerChainInfo() {
        if(_serverHandlerChainInfo == null)
            _serverHandlerChainInfo = new HandlerChainInfo();
        return _serverHandlerChainInfo;
    }

    public void setServerHandlerChainInfo(HandlerChainInfo i) {
        _serverHandlerChainInfo = i;
    }

    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
