// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SimpleHashtable.java

package com.sun.xml.rpc.sp;


// Referenced classes of package com.sun.xml.rpc.sp:
//            SimpleHashtable

class SimpleHashtable$Entry {

    int hash;
    Object key;
    Object value;
    SimpleHashtable$Entry next;

    protected SimpleHashtable$Entry(int hash, Object key, Object value, SimpleHashtable$Entry next) {
        this.hash = hash;
        this.key = key;
        this.value = value;
        this.next = next;
    }
}
