// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   TypeMappingImpl.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.util.*;
import javax.xml.rpc.encoding.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            TypeMappingException, ExtendedTypeMapping

public class TypeMappingImpl
    implements ExtendedTypeMapping {

    protected static final String EMPTY_STRING_ARRAY[] = new String[0];
    protected static final boolean UNIQUE_IS_REQUIRED = true;
    protected static final boolean UNIQUE_IS_OPTIONAL = false;
    protected static final TypeMappingImpl$Row NULL_ROW;
    protected static final TypeMappingImpl$Entry NULL_ENTRY;
    private TypeMappingImpl$Entry table[];
    private int count;
    private int threshold;
    private float loadFactor;
    protected ExtendedTypeMapping parent;
    protected String namespaceURIs[];
    protected List tuples;

    private int hashToIndex(int hash) {
        return (hash & 0x7fffffff) % table.length;
    }

    private TypeMappingImpl$Entry getHashBucket(int hash) {
        return table[hashToIndex(hash)];
    }

    private void put(int hash, TypeMappingImpl$Row row) {
        if(count >= threshold)
            rehash();
        int index = hashToIndex(hash);
        table[index] = new TypeMappingImpl$Entry(table[index], hash, row);
        count++;
    }

    private void rehash() {
        int oldCapacity = table.length;
        TypeMappingImpl$Entry oldMap[] = table;
        int newCapacity = oldCapacity * 2 + 1;
        TypeMappingImpl$Entry newMap[] = new TypeMappingImpl$Entry[newCapacity];
        Arrays.fill(newMap, NULL_ENTRY);
        threshold = (int)((float)newCapacity * loadFactor);
        table = newMap;
        for(int i = oldCapacity; i-- > 0;) {
            for(TypeMappingImpl$Entry old = oldMap[i]; old != NULL_ENTRY;) {
                TypeMappingImpl$Entry e = old;
                old = old.next;
                int index = hashToIndex(e.hash);
                e.next = table[index];
                table[index] = e;
            }

        }

    }

    public TypeMappingImpl() {
        loadFactor = 0.75F;
        parent = null;
        namespaceURIs = EMPTY_STRING_ARRAY;
        tuples = new ArrayList();
        int initialCapacity = 57;
        table = new TypeMappingImpl$Entry[initialCapacity];
        Arrays.fill(table, NULL_ENTRY);
        count = 0;
        threshold = (int)((float)initialCapacity * loadFactor);
    }

    public TypeMappingImpl(ExtendedTypeMapping parent) {
        this();
        this.parent = parent;
    }

    public String[] getSupportedNamespaces() {
        return namespaceURIs;
    }

    public void setSupportedNamespaces(String namespaceURIs[]) {
        if(namespaceURIs != null)
            this.namespaceURIs = namespaceURIs;
        else
            this.namespaceURIs = EMPTY_STRING_ARRAY;
    }

    public boolean isRegistered(Class javaType, QName xmlType) {
        if(xmlType == null)
            throw new IllegalArgumentException("XML type may not be null");
        if(javaType == null)
            throw new IllegalArgumentException("Java type may not be null");
        int jTypeHash = javaType.hashCode();
        int xTypeHash = xmlType.hashCode();
        int combinedHash = jTypeHash ^ xTypeHash;
        TypeMappingImpl$Entry existingEntry = getHashBucket(combinedHash).getEntryMatching(javaType, xmlType);
        boolean isRegistered = existingEntry != NULL_ENTRY;
        if(!isRegistered && parent != null)
            isRegistered = parent.isRegistered(javaType, xmlType);
        return isRegistered;
    }

    public void register(Class javaType, QName xmlType, SerializerFactory sf, DeserializerFactory dsf) {
        if(xmlType == null)
            throw new IllegalArgumentException("XML type may not be null");
        if(javaType == null)
            throw new IllegalArgumentException("Java type may not be null");
        try {
            int jTypeHash = javaType.hashCode();
            int xTypeHash = xmlType.hashCode();
            int combinedHash = jTypeHash ^ xTypeHash;
            TypeMappingImpl$Row existingRow = getHashBucket(combinedHash).getEntryMatching(javaType, xmlType).row;
            if(existingRow != NULL_ROW) {
                existingRow.serializerFactory = sf;
                existingRow.deserializerFactory = dsf;
            } else {
                TypeMappingImpl$Row newRow = new TypeMappingImpl$Row(javaType, xmlType, sf, dsf);
                put(jTypeHash, newRow);
                put(xTypeHash, newRow);
                put(combinedHash, newRow);
                tuples.add(newRow);
            }
        }
        catch(Exception e) {
            throw new TypeMappingException("typemapping.registration.failed.nested.exception", new LocalizableExceptionAdapter(e));
        }
    }

    protected TypeMappingImpl$Entry getEntryMatching(Class javaType) {
        return getHashBucket(javaType.hashCode()).getEntryMatching(javaType);
    }

    protected TypeMappingImpl$Entry getEntryMatching(QName xmlType) {
        return getHashBucket(xmlType.hashCode()).getEntryMatching(xmlType);
    }

    protected TypeMappingImpl$Entry getNonPrimitiveEntryMatching(QName xmlType) {
        return getHashBucket(xmlType.hashCode()).getNonPrimitiveEntryMatching(xmlType);
    }

    protected TypeMappingImpl$Entry getEntryMatching(Class javaType, QName xmlType) {
        return getHashBucket(javaType.hashCode() ^ xmlType.hashCode()).getEntryMatching(javaType, xmlType);
    }

    protected TypeMappingImpl$Entry getEntryClosestTo(Class javaType, QName xmlType) {
        TypeMappingImpl$Entry entry = getEntryMatching(javaType, xmlType);
        if(entry == NULL_ENTRY)
            entry = getEntryMatching(xmlType).getEntryMatchingSubclass(javaType, xmlType);
        return entry;
    }

    protected TypeMappingImpl$Entry getEntryCloesestTo(Class javaType) {
        TypeMappingImpl$Entry matchingEntry = getEntryMatching(javaType);
        if(matchingEntry != NULL_ENTRY)
            return matchingEntry;
        List superTypes = new ArrayList();
        Class superClass = javaType.getSuperclass();
        if(superClass != null && !superClass.equals(java.lang.Object.class))
            superTypes.add(superClass);
        superTypes.addAll(Arrays.asList(javaType.getInterfaces()));
        for(int i = 0; i < superTypes.size(); i++) {
            Class currentType = (Class)superTypes.get(i);
            if(currentType == null)
                continue;
            matchingEntry = getEntryMatching(currentType);
            if(matchingEntry != NULL_ENTRY)
                break;
            superClass = currentType.getSuperclass();
            if(superClass != null && !superClass.equals(java.lang.Object.class))
                superTypes.add(superClass);
        }

        return matchingEntry;
    }

    protected SerializerFactory getSerializer(Class javaType, boolean uniqueRequired) {
        try {
            TypeMappingImpl$Entry matchingRowEntry = getEntryCloesestTo(javaType);
            SerializerFactory factory = matchingRowEntry.row.serializerFactory;
            return factory;
        }
        catch(Exception e) {
            throw new TypeMappingException("typemapping.retrieval.failed.nested.exception", new LocalizableExceptionAdapter(e));
        }
    }

    protected SerializerFactory getSerializer(QName xmlType, boolean uniqueRequired) {
        try {
            TypeMappingImpl$Entry matchingRowEntry = getNonPrimitiveEntryMatching(xmlType);
            SerializerFactory factory = matchingRowEntry.row.serializerFactory;
            if(uniqueRequired && matchingRowEntry.next.getNonPrimitiveEntryMatching(xmlType) != NULL_ENTRY)
                return null;
            else
                return factory;
        }
        catch(Exception e) {
            throw new TypeMappingException("typemapping.retrieval.failed.nested.exception", new LocalizableExceptionAdapter(e));
        }
    }

    protected DeserializerFactory getDeserializer(Class javaType, boolean uniqueRequired) {
        try {
            TypeMappingImpl$Entry matchingRowEntry = getEntryMatching(javaType);
            DeserializerFactory factory = matchingRowEntry.row.deserializerFactory;
            if(uniqueRequired && matchingRowEntry.next.getEntryMatching(javaType) != NULL_ENTRY)
                return null;
            else
                return factory;
        }
        catch(Exception e) {
            throw new TypeMappingException("typemapping.retrieval.failed.nested.exception", new LocalizableExceptionAdapter(e));
        }
    }

    protected DeserializerFactory getDeserializer(QName xmlType, boolean uniqueRequired) {
        try {
            TypeMappingImpl$Entry matchingRowEntry = getNonPrimitiveEntryMatching(xmlType);
            DeserializerFactory factory = matchingRowEntry.row.deserializerFactory;
            if(uniqueRequired && matchingRowEntry.next.getNonPrimitiveEntryMatching(xmlType) != NULL_ENTRY)
                return null;
            else
                return factory;
        }
        catch(Exception e) {
            throw new TypeMappingException("typemapping.retrieval.failed.nested.exception", new LocalizableExceptionAdapter(e));
        }
    }

    protected Class getJavaType(QName xmlType, boolean uniqueRequired) {
        try {
            TypeMappingImpl$Entry matchingRowEntry = getNonPrimitiveEntryMatching(xmlType);
            Class javaType = matchingRowEntry.row.javaType;
            if(uniqueRequired && matchingRowEntry.next.getNonPrimitiveEntryMatching(xmlType) != NULL_ENTRY)
                return null;
            else
                return javaType;
        }
        catch(Exception e) {
            throw new TypeMappingException("typemapping.retrieval.failed.nested.exception", new LocalizableExceptionAdapter(e));
        }
    }

    protected QName getXmlType(Class javaType, boolean uniqueRequired) {
        try {
            TypeMappingImpl$Entry matchingRowEntry = getEntryMatching(javaType);
            QName xmlType = matchingRowEntry.row.xmlType;
            if(uniqueRequired && matchingRowEntry.next.getEntryMatching(javaType) != NULL_ENTRY)
                return null;
            else
                return xmlType;
        }
        catch(Exception e) {
            throw new TypeMappingException("typemapping.retrieval.failed.nested.exception", new LocalizableExceptionAdapter(e));
        }
    }

    public SerializerFactory getSerializer(Class javaType, QName xmlType) {
        SerializerFactory factory;
        if(javaType == null) {
            if(xmlType == null)
                throw new IllegalArgumentException("getSerializer requires a Java type and/or an XML type");
            factory = getSerializer(xmlType, false);
        } else
        if(xmlType == null)
            factory = getSerializer(javaType, false);
        else
            try {
                factory = getEntryClosestTo(javaType, xmlType).row.serializerFactory;
            }
            catch(Exception e) {
                throw new TypeMappingException("typemapping.retrieval.failed.nested.exception", new LocalizableExceptionAdapter(e));
            }
        if(factory == null && parent != null)
            factory = parent.getSerializer(javaType, xmlType);
        return factory;
    }

    public DeserializerFactory getDeserializer(Class javaType, QName xmlType) {
        DeserializerFactory factory = null;
        if(javaType == null) {
            if(xmlType == null)
                throw new IllegalArgumentException("getDeserializer requires a Java type and/or an XML type");
            factory = getDeserializer(xmlType, false);
        } else
        if(xmlType == null)
            factory = getDeserializer(javaType, false);
        else
            try {
                factory = getEntryClosestTo(javaType, xmlType).row.deserializerFactory;
            }
            catch(Exception e) {
                throw new TypeMappingException("typemapping.retrieval.failed.nested.exception", new LocalizableExceptionAdapter(e));
            }
        if(factory == null && parent != null)
            factory = parent.getDeserializer(javaType, xmlType);
        return factory;
    }

    public void removeSerializer(Class javaType, QName xmlType) {
        if(javaType == null || xmlType == null)
            throw new IllegalArgumentException();
        try {
            getEntryMatching(javaType, xmlType).row.serializerFactory = null;
        }
        catch(Exception e) {
            throw new TypeMappingException("typemapping.retrieval.failed.nested.exception", new LocalizableExceptionAdapter(e));
        }
    }

    public void removeDeserializer(Class javaType, QName xmlType) {
        if(javaType == null || xmlType == null)
            throw new IllegalArgumentException();
        try {
            getEntryMatching(javaType, xmlType).row.deserializerFactory = null;
        }
        catch(Exception e) {
            throw new TypeMappingException("typemapping.retrieval.failed.nested.exception", new LocalizableExceptionAdapter(e));
        }
    }

    public Class getJavaType(QName xmlType) {
        if(xmlType == null)
            throw new IllegalArgumentException("non null xmlType required");
        Class javaType = getJavaType(xmlType, false);
        if(javaType == null && parent != null)
            javaType = parent.getJavaType(xmlType);
        return javaType;
    }

    public QName getXmlType(Class javaType) {
        if(javaType == null)
            throw new IllegalArgumentException("non null xmjavaType required");
        QName xmlType = getXmlType(javaType, false);
        if(xmlType == null && parent != null)
            xmlType = parent.getXmlType(javaType);
        return xmlType;
    }

    static  {
        NULL_ROW = new TypeMappingImpl$Row();
        NULL_ENTRY = new TypeMappingImpl$Entry(null, 0, NULL_ROW);
        NULL_ENTRY.next = NULL_ENTRY;
    }
}
