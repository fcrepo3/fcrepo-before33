// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AttributeDecl.java

package com.sun.xml.rpc.sp;


class AttributeDecl {

    String name;
    String type;
    String values[];
    String defaultValue;
    boolean isRequired;
    boolean isFixed;
    boolean isFromInternalSubset;
    static final String CDATA = "CDATA";
    static final String ID = "ID";
    static final String IDREF = "IDREF";
    static final String IDREFS = "IDREFS";
    static final String ENTITY = "ENTITY";
    static final String ENTITIES = "ENTITIES";
    static final String NMTOKEN = "NMTOKEN";
    static final String NMTOKENS = "NMTOKENS";
    static final String NOTATION = "NOTATION";
    static final String ENUMERATION = "ENUMERATION";

    AttributeDecl(String s) {
        name = s;
    }
}
