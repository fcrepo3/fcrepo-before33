// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JavaMethod.java

package com.sun.xml.rpc.processor.model.java;

import com.sun.xml.rpc.processor.model.ModelException;
import java.util.*;

// Referenced classes of package com.sun.xml.rpc.processor.model.java:
//            JavaParameter, JavaType

public class JavaMethod {

    private String _name;
    private List _parameters;
    private List _exceptions;
    private JavaType _returnType;

    public JavaMethod(String name) {
        _name = name;
        _parameters = new ArrayList();
        _exceptions = new ArrayList();
        _returnType = null;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public JavaType getReturnType() {
        return _returnType;
    }

    public void setReturnType(JavaType returnType) {
        _returnType = returnType;
    }

    public boolean hasParameter(String paramName) {
        for(int i = 0; i < _parameters.size(); i++)
            if(paramName.equals(((JavaParameter)_parameters.get(i)).getName()))
                return true;

        return false;
    }

    public void addParameter(JavaParameter param) {
        if(hasParameter(param.getName())) {
            throw new ModelException("model.uniqueness");
        } else {
            _parameters.add(param);
            return;
        }
    }

    public Iterator getParameters() {
        return _parameters.iterator();
    }

    public int getParameterCount() {
        return _parameters.size();
    }

    public boolean hasException(String exception) {
        return _exceptions.contains(exception);
    }

    public void addException(String exception) {
        if(hasException(exception)) {
            throw new ModelException("model.uniqueness");
        } else {
            _exceptions.add(exception);
            return;
        }
    }

    public Iterator getExceptions() {
        return _exceptions.iterator();
    }
}
