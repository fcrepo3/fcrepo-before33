// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   MIMEMultipartRelated.java

package com.sun.xml.rpc.wsdl.document.mime;

import com.sun.xml.rpc.wsdl.framework.*;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document.mime:
//            MIMEConstants, MIMEPart

public class MIMEMultipartRelated extends Extension {

    private List _parts;

    public MIMEMultipartRelated() {
        _parts = new ArrayList();
    }

    public QName getElementName() {
        return MIMEConstants.QNAME_MULTIPART_RELATED;
    }

    public void add(MIMEPart part) {
        _parts.add(part);
    }

    public Iterator getParts() {
        return _parts.iterator();
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        super.withAllSubEntitiesDo(action);
        for(Iterator iter = _parts.iterator(); iter.hasNext(); action.perform((Entity)iter.next()));
    }

    public void accept(ExtensionVisitor visitor) throws Exception {
        visitor.preVisit(this);
        visitor.postVisit(this);
    }

    public void validateThis() {
    }
}
