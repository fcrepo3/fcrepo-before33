// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   TypeMappingImpl.java

package com.sun.xml.rpc.encoding;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            TypeMappingImpl

public class TypeMappingImpl$Entry {

    TypeMappingImpl$Entry next;
    int hash;
    TypeMappingImpl$Row row;

    TypeMappingImpl$Entry(TypeMappingImpl$Entry next, int hash, TypeMappingImpl$Row row) {
        if(row == null) {
            throw new IllegalArgumentException("row may not be null");
        } else {
            this.next = next;
            this.hash = hash;
            this.row = row;
            return;
        }
    }

    TypeMappingImpl$Entry getEntryMatching(Class javaType) {
        TypeMappingImpl$Entry candidate;
        for(candidate = this; candidate != TypeMappingImpl.NULL_ENTRY && !candidate.row.javaType.equals(javaType); candidate = candidate.next);
        return candidate;
    }

    TypeMappingImpl$Entry getEntryMatchingSubclass(Class javaType) {
        TypeMappingImpl$Entry bestCandidate = TypeMappingImpl.NULL_ENTRY;
        for(TypeMappingImpl$Entry eachCandidate = this; eachCandidate != TypeMappingImpl.NULL_ENTRY; eachCandidate = eachCandidate.next)
            if(eachCandidate.row.javaType.isAssignableFrom(javaType) && (bestCandidate == TypeMappingImpl.NULL_ENTRY || bestCandidate.row.javaType.isAssignableFrom(eachCandidate.row.javaType)))
                bestCandidate = eachCandidate;

        return bestCandidate;
    }

    TypeMappingImpl$Entry getEntryMatching(QName xmlType) {
        TypeMappingImpl$Entry candidate;
        for(candidate = this; candidate != TypeMappingImpl.NULL_ENTRY && !candidate.row.xmlType.equals(xmlType); candidate = candidate.next);
        return candidate;
    }

    TypeMappingImpl$Entry getNonPrimitiveEntryMatching(QName xmlType) {
        TypeMappingImpl$Entry candidate;
        for(candidate = this; candidate != TypeMappingImpl.NULL_ENTRY && (!candidate.row.xmlType.equals(xmlType) || candidate.row.javaType.isPrimitive()); candidate = candidate.next);
        return candidate;
    }

    TypeMappingImpl$Entry getEntryMatching(Class javaType, QName xmlType) {
        TypeMappingImpl$Entry candidate;
        for(candidate = this; candidate != TypeMappingImpl.NULL_ENTRY && (!candidate.row.javaType.equals(javaType) || !candidate.row.xmlType.equals(xmlType)); candidate = candidate.next);
        return candidate;
    }

    TypeMappingImpl$Entry getEntryMatchingSubclass(Class javaType, QName xmlType) {
        TypeMappingImpl$Entry bestCandidate = TypeMappingImpl.NULL_ENTRY;
        for(TypeMappingImpl$Entry eachCandidate = this; eachCandidate != TypeMappingImpl.NULL_ENTRY; eachCandidate = eachCandidate.next)
            if(eachCandidate.row.javaType.isAssignableFrom(javaType) && eachCandidate.row.xmlType.equals(xmlType) && (bestCandidate == TypeMappingImpl.NULL_ENTRY || bestCandidate.row.javaType.isAssignableFrom(eachCandidate.row.javaType)))
                bestCandidate = eachCandidate;

        return bestCandidate;
    }
}
