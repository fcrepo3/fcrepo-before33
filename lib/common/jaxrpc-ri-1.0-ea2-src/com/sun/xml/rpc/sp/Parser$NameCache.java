// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Parser.java

package com.sun.xml.rpc.sp;


// Referenced classes of package com.sun.xml.rpc.sp:
//            Parser

final class Parser$NameCache {

    Parser$NameCacheEntry hashtable[];

    Parser$NameCache() {
        hashtable = new Parser$NameCacheEntry[541];
    }

    String lookup(char value[], int len) {
        return lookupEntry(value, len).name;
    }

    Parser$NameCacheEntry lookupEntry(char value[], int len) {
        int index = 0;
        for(int i = 0; i < len; i++)
            index = index * 31 + value[i];

        index &= 0x7fffffff;
        index %= hashtable.length;
        Parser$NameCacheEntry entry;
        for(entry = hashtable[index]; entry != null; entry = entry.next)
            if(entry.matches(value, len))
                return entry;

        entry = new Parser$NameCacheEntry();
        entry.chars = new char[len];
        System.arraycopy(value, 0, entry.chars, 0, len);
        entry.name = new String(entry.chars);
        entry.name = entry.name.intern();
        entry.next = hashtable[index];
        hashtable[index] = entry;
        return entry;
    }
}
