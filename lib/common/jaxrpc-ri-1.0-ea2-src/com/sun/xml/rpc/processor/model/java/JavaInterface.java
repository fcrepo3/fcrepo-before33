// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JavaInterface.java

package com.sun.xml.rpc.processor.model.java;

import com.sun.xml.rpc.processor.model.ModelException;
import java.util.*;

// Referenced classes of package com.sun.xml.rpc.processor.model.java:
//            JavaMethod

public class JavaInterface {

    private final String name;
    private final String impl;
    private final List methods;
    private ArrayList interfaces;

    public JavaInterface(String name) {
        interfaces = new ArrayList();
        this.name = name;
        impl = null;
        methods = new ArrayList();
    }

    public JavaInterface(String name, String impl) {
        interfaces = new ArrayList();
        this.name = name;
        this.impl = impl;
        methods = new ArrayList();
    }

    public String getName() {
        return name;
    }

    public String getImpl() {
        return impl;
    }

    public Iterator getMethods() {
        return methods.iterator();
    }

    public boolean hasMethod(JavaMethod method) {
        for(int i = 0; i < methods.size(); i++)
            if(method.equals((JavaMethod)methods.get(i)))
                return true;

        return false;
    }

    public void addMethod(JavaMethod method) {
        if(hasMethod(method)) {
            throw new ModelException("model.uniqueness");
        } else {
            methods.add(method);
            return;
        }
    }

    public boolean hasInterface(String interfaceName) {
        for(int i = 0; i < interfaces.size(); i++)
            if(interfaceName.equals((String)interfaces.get(i)))
                return true;

        return false;
    }

    public void addInterface(String interfaceName) {
        if(hasInterface(interfaceName)) {
            return;
        } else {
            interfaces.add(interfaceName);
            return;
        }
    }

    public Iterator getInterfaces() {
        return interfaces.iterator();
    }
}
