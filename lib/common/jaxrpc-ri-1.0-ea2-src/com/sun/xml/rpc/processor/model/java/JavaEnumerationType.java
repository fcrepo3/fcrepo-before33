// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JavaEnumerationType.java

package com.sun.xml.rpc.processor.model.java;

import java.util.*;

// Referenced classes of package com.sun.xml.rpc.processor.model.java:
//            JavaType, JavaEnumerationEntry

public class JavaEnumerationType extends JavaType {

    private List entries;
    private JavaType baseType;

    public JavaEnumerationType(String name, JavaType baseType, boolean present) {
        super(name, present, "null");
        entries = new ArrayList();
        this.baseType = baseType;
    }

    public JavaType getBaseType() {
        return baseType;
    }

    public void add(JavaEnumerationEntry e) {
        entries.add(e);
    }

    public Iterator getEntries() {
        return entries.iterator();
    }

    public int getEntriesCount() {
        return entries.size();
    }
}
