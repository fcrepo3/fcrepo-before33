// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ModelObject.java

package com.sun.xml.rpc.processor.model;

import java.util.HashMap;
import java.util.Map;

// Referenced classes of package com.sun.xml.rpc.processor.model:
//            ModelVisitor

public abstract class ModelObject {

    private Map _properties;

    public ModelObject() {
    }

    public abstract void accept(ModelVisitor modelvisitor) throws Exception;

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
}
