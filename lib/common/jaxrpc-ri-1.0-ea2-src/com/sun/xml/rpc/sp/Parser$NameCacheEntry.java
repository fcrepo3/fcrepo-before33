// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Parser.java

package com.sun.xml.rpc.sp;


// Referenced classes of package com.sun.xml.rpc.sp:
//            Parser

final class Parser$NameCacheEntry {

    String name;
    char chars[];
    Parser$NameCacheEntry next;

    Parser$NameCacheEntry() {
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
