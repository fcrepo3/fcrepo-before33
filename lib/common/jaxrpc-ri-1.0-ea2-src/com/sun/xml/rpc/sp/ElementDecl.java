// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ElementDecl.java

package com.sun.xml.rpc.sp;


// Referenced classes of package com.sun.xml.rpc.sp:
//            SimpleHashtable, ContentModel

class ElementDecl {

    String name;
    String id;
    String contentType;
    ContentModel model;
    boolean ignoreWhitespace;
    boolean isFromInternalSubset;
    SimpleHashtable attributes;

    ElementDecl(String s) {
        attributes = new SimpleHashtable();
        name = s;
    }
}
