// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SchemaKinds.java

package com.sun.xml.rpc.wsdl.document.schema;

import com.sun.xml.rpc.wsdl.framework.Kind;

public class SchemaKinds {

    public static final Kind XSD_ATTRIBUTE = new Kind("xsd:attribute");
    public static final Kind XSD_ATTRIBUTE_GROUP = new Kind("xsd:attributeGroup");
    public static final Kind XSD_CONSTRAINT = new Kind("xsd:constraint");
    public static final Kind XSD_ELEMENT = new Kind("xsd:element");
    public static final Kind XSD_GROUP = new Kind("xsd:group");
    public static final Kind XSD_IDENTITY_CONSTRAINT = new Kind("xsd:identityConstraint");
    public static final Kind XSD_NOTATION = new Kind("xsd:notation");
    public static final Kind XSD_TYPE = new Kind("xsd:type");

    private SchemaKinds() {
    }

}
