// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Parser2.java

package com.sun.xml.rpc.sp;


// Referenced classes of package com.sun.xml.rpc.sp:
//            Parser2

final class Parser2$NameCacheEntry {

    String name;
    char chars[];
    Parser2$NameCacheEntry next;

    Parser2$NameCacheEntry() {
    }

    boolean matches(char value[], int len) {
        if(chars.length != len)
            return false;
        for(int i = 0; i < len; i++)
            if(value[i] != chars[i])
                return false;

        return true;
    }
}
