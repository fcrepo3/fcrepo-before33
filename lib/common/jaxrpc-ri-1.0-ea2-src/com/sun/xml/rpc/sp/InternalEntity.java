// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   InternalEntity.java

package com.sun.xml.rpc.sp;


// Referenced classes of package com.sun.xml.rpc.sp:
//            EntityDecl

class InternalEntity extends EntityDecl {

    char buf[];

    InternalEntity(String name, char value[]) {
        super.name = name;
        buf = value;
    }
}
