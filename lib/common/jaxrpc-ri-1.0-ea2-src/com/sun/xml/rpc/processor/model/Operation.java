// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Operation.java

package com.sun.xml.rpc.processor.model;

import com.sun.xml.rpc.processor.model.java.JavaMethod;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model:
//            ModelObject, ModelException, Fault, ModelVisitor, 
//            Request, Response

public class Operation extends ModelObject {

    private QName _name;
    private String _uniqueName;
    private Request _request;
    private Response _response;
    private JavaMethod _javaMethod;
    private String _soapAction;
    private SOAPStyle _style;
    private Map _faults;

    public Operation(QName name) {
        _name = name;
        _uniqueName = name.getLocalPart();
        _faults = new HashMap();
    }

    public QName getName() {
        return _name;
    }

    public String getUniqueName() {
        return _uniqueName;
    }

    public void setUniqueName(String s) {
        _uniqueName = s;
    }

    public Request getRequest() {
        return _request;
    }

    public void setRequest(Request r) {
        _request = r;
    }

    public Response getResponse() {
        return _response;
    }

    public void setResponse(Response r) {
        _response = r;
    }

    public boolean isOverloaded() {
        return !_name.getLocalPart().equals(_uniqueName);
    }

    public void addFault(Fault f) {
        if(_faults.containsKey(f.getName())) {
            throw new ModelException("model.uniqueness");
        } else {
            _faults.put(f.getName(), f);
            return;
        }
    }

    public Iterator getFaults() {
        return _faults.values().iterator();
    }

    public int getFaultCount() {
        return _faults.size();
    }

    public JavaMethod getJavaMethod() {
        return _javaMethod;
    }

    public void setJavaMethod(JavaMethod i) {
        _javaMethod = i;
    }

    public String getSOAPAction() {
        return _soapAction;
    }

    public void setSOAPAction(String s) {
        _soapAction = s;
    }

    public SOAPStyle getStyle() {
        return _style;
    }

    public void setStyle(SOAPStyle s) {
        _style = s;
    }

    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
