// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ExtensibilityHelper.java

package com.sun.xml.rpc.wsdl.framework;

import java.util.*;

// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            Entity, Extension, EntityAction, ExtensionVisitor

public class ExtensibilityHelper {

    private List _extensions;

    public ExtensibilityHelper() {
    }

    public void addExtension(Extension e) {
        if(_extensions == null)
            _extensions = new ArrayList();
        _extensions.add(e);
    }

    public Iterator extensions() {
        if(_extensions == null)
            return new ExtensibilityHelper$1(this);
        else
            return _extensions.iterator();
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        if(_extensions != null) {
            for(Iterator iter = _extensions.iterator(); iter.hasNext(); action.perform((Entity)iter.next()));
        }
    }

    public void accept(ExtensionVisitor visitor) throws Exception {
        if(_extensions != null) {
            for(Iterator iter = _extensions.iterator(); iter.hasNext(); ((Extension)iter.next()).accept(visitor));
        }
    }
}
