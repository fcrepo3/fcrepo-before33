// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Service.java

package com.sun.xml.rpc.processor.model;

import com.sun.xml.rpc.processor.model.java.JavaInterface;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model:
//            ModelObject, ModelException, Port, ModelVisitor

public class Service extends ModelObject {

    private QName name;
    private List ports;
    private Map portsByName;
    private JavaInterface javaInterface;

    public Service(QName name, JavaInterface javaInterface) {
        ports = new ArrayList();
        portsByName = new HashMap();
        this.name = name;
        this.javaInterface = javaInterface;
    }

    public QName getName() {
        return name;
    }

    public void addPort(Port port) {
        if(portsByName.containsKey(port.getName())) {
            throw new ModelException("model.uniqueness");
        } else {
            ports.add(port);
            portsByName.put(port.getName(), port);
            return;
        }
    }

    public Iterator getPorts() {
        return ports.iterator();
    }

    public Port getPortByName(QName n) {
        return (Port)portsByName.get(n);
    }

    public JavaInterface getJavaInterface() {
        return javaInterface;
    }

    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
