// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   EnumerationFacet.java

package com.sun.xml.rpc.processor.schema;

import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import java.util.*;

// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            ConstrainingFacet

public class EnumerationFacet extends ConstrainingFacet {

    private List values;

    public EnumerationFacet() {
        super(SchemaConstants.QNAME_ENUMERATION);
        values = new ArrayList();
    }

    public void addValue(String s) {
        values.add(s);
    }

    public Iterator values() {
        return values.iterator();
    }
}
