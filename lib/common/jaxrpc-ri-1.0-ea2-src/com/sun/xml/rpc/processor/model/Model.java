// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Model.java

package com.sun.xml.rpc.processor.model;

import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model:
//            ModelObject, ModelException, Service, ModelVisitor

public class Model extends ModelObject {

    private QName name;
    private String targetNamespace;
    private List services;
    private Map servicesByName;

    public Model(QName name) {
        services = new ArrayList();
        servicesByName = new HashMap();
        this.name = name;
    }

    public QName getName() {
        return name;
    }

    public String getTargetNamespaceURI() {
        return targetNamespace;
    }

    public void setTargetNamespaceURI(String s) {
        targetNamespace = s;
    }

    public void addService(Service service) {
        if(servicesByName.containsKey(service.getName())) {
            throw new ModelException("model.uniqueness");
        } else {
            services.add(service);
            servicesByName.put(service.getName(), service);
            return;
        }
    }

    public Iterator getServices() {
        return services.iterator();
    }

    public Service getServiceByName(QName name) {
        return (Service)servicesByName.get(name);
    }

    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
