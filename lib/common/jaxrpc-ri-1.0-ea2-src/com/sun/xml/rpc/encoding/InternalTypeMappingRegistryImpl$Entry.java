// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   InternalTypeMappingRegistryImpl.java

package com.sun.xml.rpc.encoding;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            InternalTypeMappingRegistryImpl

public class InternalTypeMappingRegistryImpl$Entry {

    InternalTypeMappingRegistryImpl$Entry next;
    int hash;
    InternalTypeMappingRegistryImpl$Row row;

    static InternalTypeMappingRegistryImpl$Entry createNull(InternalTypeMappingRegistryImpl$Row nullRow) {
        InternalTypeMappingRegistryImpl$Entry nullEntry = new InternalTypeMappingRegistryImpl$Entry(0, nullRow);
        nullEntry.next = nullEntry;
        return nullEntry;
    }

    private InternalTypeMappingRegistryImpl$Entry(int hash, InternalTypeMappingRegistryImpl$Row row) {
        if(row == null) {
            throw new IllegalArgumentException("row may not be null");
        } else {
            next = null;
            this.hash = hash;
            this.row = row;
            return;
        }
    }

    InternalTypeMappingRegistryImpl$Entry(InternalTypeMappingRegistryImpl$Entry next, int hash, InternalTypeMappingRegistryImpl$Row row) {
        this(hash, row);
        if(next == null) {
            throw new IllegalArgumentException("next may not be null");
        } else {
            this.next = next;
            return;
        }
    }

    boolean matches(String encoding, Class javaType) {
        return !row.encoding.equals(encoding) || row.javaType == null ? false : row.javaType.equals(javaType);
    }

    boolean matches(String encoding, QName xmlType) {
        return !row.encoding.equals(encoding) || row.xmlType == null ? false : row.xmlType.equals(xmlType);
    }

    boolean matches(String encoding, Class javaType, QName xmlType) {
        return (row.xmlType == null ? xmlType == null : row.xmlType.equals(xmlType)) && (row.javaType == null ? javaType == null : row.javaType.equals(javaType)) && row.encoding.equals(encoding);
    }

    InternalTypeMappingRegistryImpl$Entry getEntryMatching(String encoding, Class javaType) {
        InternalTypeMappingRegistryImpl$Entry candidate;
        for(candidate = this; candidate != InternalTypeMappingRegistryImpl.NULL_ENTRY && !candidate.matches(encoding, javaType); candidate = candidate.next);
        return candidate;
    }

    InternalTypeMappingRegistryImpl$Entry getEntryMatching(String encoding, QName xmlType) {
        InternalTypeMappingRegistryImpl$Entry candidate;
        for(candidate = this; candidate != InternalTypeMappingRegistryImpl.NULL_ENTRY && !candidate.matches(encoding, xmlType); candidate = candidate.next);
        return candidate;
    }

    InternalTypeMappingRegistryImpl$Entry getEntryMatching(String encoding, Class javaType, QName xmlType) {
        InternalTypeMappingRegistryImpl$Entry candidate;
        for(candidate = this; candidate != InternalTypeMappingRegistryImpl.NULL_ENTRY && !candidate.matches(encoding, javaType, xmlType); candidate = candidate.next);
        return candidate;
    }
}
