// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Entity.java

package com.sun.xml.rpc.wsdl.framework;

import java.util.HashMap;
import java.util.Map;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            ValidationException, Elemental, QNameAction, EntityAction, 
//            EntityReferenceAction

public abstract class Entity
    implements Elemental {

    private Map _properties;

    public Entity() {
    }

    public Object getProperty(String key) {
        if(_properties == null)
            return null;
        else
            return _properties.get(key);
    }

    public void setProperty(String key, Object value) {
        if(value == null) {
            removeProperty(key);
            return;
        }
        if(_properties == null)
            _properties = new HashMap();
        _properties.put(key, value);
    }

    public void removeProperty(String key) {
        if(_properties != null)
            _properties.remove(key);
    }

    public void withAllSubEntitiesDo(EntityAction entityaction) {
    }

    public void withAllQNamesDo(QNameAction action) {
        action.perform(getElementName());
    }

    public void withAllEntityReferencesDo(EntityReferenceAction entityreferenceaction) {
    }

    public abstract void validateThis();

    protected void failValidation(String key) {
        throw new ValidationException(key, getElementName().getLocalPart());
    }

    protected void failValidation(String key, String arg) {
        throw new ValidationException(key, new Object[] {
            arg, getElementName().getLocalPart()
        });
    }

    public abstract QName getElementName();
}
