// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   MIMEPart.java

package com.sun.xml.rpc.wsdl.document.mime;

import com.sun.xml.rpc.wsdl.framework.*;
import java.util.Iterator;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document.mime:
//            MIMEConstants

public class MIMEPart extends Extension
    implements Extensible {

    private String _name;
    private ExtensibilityHelper _helper;

    public MIMEPart() {
        _helper = new ExtensibilityHelper();
    }

    public QName getElementName() {
        return MIMEConstants.QNAME_PART;
    }

    public String getName() {
        return _name;
    }

    public void setName(String s) {
        _name = s;
    }

    public void addExtension(Extension e) {
        _helper.addExtension(e);
    }

    public Iterator extensions() {
        return _helper.extensions();
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        _helper.withAllSubEntitiesDo(action);
    }

    public void validateThis() {
    }
}
