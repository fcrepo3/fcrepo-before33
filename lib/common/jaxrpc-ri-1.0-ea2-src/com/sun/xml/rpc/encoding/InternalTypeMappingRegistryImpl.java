// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   InternalTypeMappingRegistryImpl.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import java.util.Arrays;
import javax.xml.rpc.encoding.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            ExtendedTypeMapping, DynamicSerializer, ReferenceableSerializerImpl, Initializable, 
//            SingletonSerializerFactory, SingletonDeserializerFactory, PolymorphicArraySerializer, InternalTypeMappingRegistry, 
//            SerializerConstants, TypeMappingUtil

public class InternalTypeMappingRegistryImpl
    implements InternalTypeMappingRegistry, SerializerConstants {

    protected static final InternalTypeMappingRegistryImpl$Row NULL_ROW;
    protected static final InternalTypeMappingRegistryImpl$Entry NULL_ENTRY;
    private InternalTypeMappingRegistryImpl$Entry table[];
    private int count;
    private int threshold;
    private float loadFactor;
    protected TypeMappingRegistry registry;

    private int hashToIndex(int hash) {
        return (hash & 0x7fffffff) % table.length;
    }

    private InternalTypeMappingRegistryImpl$Entry get(int hash) {
        return table[hashToIndex(hash)];
    }

    private InternalTypeMappingRegistryImpl$Entry put(int hash, InternalTypeMappingRegistryImpl$Row row) {
        if(count >= threshold)
            rehash();
        int index = hashToIndex(hash);
        table[index] = new InternalTypeMappingRegistryImpl$Entry(table[index], hash, row);
        count++;
        return table[index];
    }

    private void rehash() {
        int oldCapacity = table.length;
        InternalTypeMappingRegistryImpl$Entry oldMap[] = table;
        int newCapacity = oldCapacity * 2 + 1;
        InternalTypeMappingRegistryImpl$Entry newMap[] = new InternalTypeMappingRegistryImpl$Entry[newCapacity];
        Arrays.fill(newMap, NULL_ENTRY);
        threshold = (int)((float)newCapacity * loadFactor);
        table = newMap;
        for(int i = oldCapacity; i-- > 0;) {
            for(InternalTypeMappingRegistryImpl$Entry old = oldMap[i]; old != NULL_ENTRY;) {
                InternalTypeMappingRegistryImpl$Entry e = old;
                old = old.next;
                int index = hashToIndex(e.hash);
                e.next = table[index];
                table[index] = e;
            }

        }

    }

    public InternalTypeMappingRegistryImpl(TypeMappingRegistry registry) throws Exception {
        loadFactor = 0.75F;
        this.registry = null;
        int initialCapacity = 57;
        table = new InternalTypeMappingRegistryImpl$Entry[initialCapacity];
        Arrays.fill(table, NULL_ENTRY);
        count = 0;
        threshold = (int)((float)initialCapacity * loadFactor);
        this.registry = registry;
        ExtendedTypeMapping soapMappings = (ExtendedTypeMapping)registry.getTypeMapping("http://schemas.xmlsoap.org/soap/encoding/");
        if(soapMappings != null) {
            CombinedSerializer anyTypeSerializer = new DynamicSerializer(SchemaConstants.QNAME_TYPE_URTYPE, true, true, "http://schemas.xmlsoap.org/soap/encoding/");
            anyTypeSerializer = new ReferenceableSerializerImpl(false, anyTypeSerializer);
            ((Initializable)anyTypeSerializer).initialize(this);
            soapMappings.register(java.lang.Object.class, SchemaConstants.QNAME_TYPE_URTYPE, new SingletonSerializerFactory(anyTypeSerializer), new SingletonDeserializerFactory(anyTypeSerializer));
            QName ELEMENT_NAME = new QName("element");
            CombinedSerializer polymorphicArraySerializer = new PolymorphicArraySerializer(SOAPConstants.QNAME_ENCODING_ARRAY, false, true, "http://schemas.xmlsoap.org/soap/encoding/", ELEMENT_NAME);
            polymorphicArraySerializer = new ReferenceableSerializerImpl(false, polymorphicArraySerializer);
            ((Initializable)polymorphicArraySerializer).initialize(this);
            soapMappings.register(java.lang.Object[].class, SOAPConstants.QNAME_ENCODING_ARRAY, new SingletonSerializerFactory(polymorphicArraySerializer), new SingletonDeserializerFactory(polymorphicArraySerializer));
        }
    }

    protected InternalTypeMappingRegistryImpl$Row getRowMatching(String encoding, Class javaType, QName xmlType) {
        int hash = encoding.hashCode() ^ javaType.hashCode() ^ xmlType.hashCode();
        InternalTypeMappingRegistryImpl$Entry matchingRowEntry = get(hash).getEntryMatching(encoding, javaType, xmlType);
        if(matchingRowEntry == NULL_ENTRY) {
            InternalTypeMappingRegistryImpl$Row row = new InternalTypeMappingRegistryImpl$Row(encoding, javaType, xmlType);
            put(encoding.hashCode() ^ javaType.hashCode(), row);
            put(encoding.hashCode() ^ xmlType.hashCode(), row);
            matchingRowEntry = put(hash, row);
        }
        return matchingRowEntry.row;
    }

    protected InternalTypeMappingRegistryImpl$Row getRowMatching(String encoding, QName xmlType) {
        int hash = encoding.hashCode() ^ xmlType.hashCode();
        InternalTypeMappingRegistryImpl$Entry matchingRowEntry = get(hash).getEntryMatching(encoding, xmlType);
        if(matchingRowEntry == NULL_ENTRY) {
            InternalTypeMappingRegistryImpl$Row row = new InternalTypeMappingRegistryImpl$Row(encoding, null, xmlType);
            matchingRowEntry = put(hash, row);
        }
        return matchingRowEntry.row;
    }

    protected InternalTypeMappingRegistryImpl$Row getRowMatching(String encoding, Class javaType) {
        int hash = encoding.hashCode() ^ javaType.hashCode();
        InternalTypeMappingRegistryImpl$Entry matchingRowEntry = get(hash).getEntryMatching(encoding, javaType);
        if(matchingRowEntry == NULL_ENTRY) {
            InternalTypeMappingRegistryImpl$Row row = new InternalTypeMappingRegistryImpl$Row(encoding, javaType, null);
            matchingRowEntry = put(hash, row);
        }
        return matchingRowEntry.row;
    }

    public Serializer getSerializer(String encoding, Class javaType, QName xmlType) throws Exception {
        InternalTypeMappingRegistryImpl$Row row;
        if(javaType == null) {
            if(xmlType == null)
                throw new IllegalArgumentException("getSerializer requires a Java type and/or an XML type");
            row = getRowMatching(encoding, xmlType);
        } else
        if(xmlType == null)
            row = getRowMatching(encoding, javaType);
        else
            row = getRowMatching(encoding, javaType, xmlType);
        if(row.serializer == null) {
            TypeMapping mapping = TypeMappingUtil.getTypeMapping(registry, encoding);
            Serializer serializer = TypeMappingUtil.getSerializer(mapping, javaType, xmlType);
            row.serializer = serializer;
            if(serializer instanceof Initializable)
                ((Initializable)serializer).initialize(this);
        }
        return row.serializer;
    }

    public Serializer getSerializer(String encoding, Class javaType) throws Exception {
        return getSerializer(encoding, javaType, null);
    }

    public Serializer getSerializer(String encoding, QName xmlType) throws Exception {
        return getSerializer(encoding, null, xmlType);
    }

    public Deserializer getDeserializer(String encoding, Class javaType, QName xmlType) throws Exception {
        InternalTypeMappingRegistryImpl$Row row;
        if(javaType == null) {
            if(xmlType == null)
                throw new IllegalArgumentException("getSerializer requires a Java type and/or an XML type");
            row = getRowMatching(encoding, xmlType);
        } else
        if(xmlType == null)
            row = getRowMatching(encoding, javaType);
        else
            row = getRowMatching(encoding, javaType, xmlType);
        if(row.deserializer == null) {
            TypeMapping mapping = TypeMappingUtil.getTypeMapping(registry, encoding);
            Deserializer deserializer = TypeMappingUtil.getDeserializer(mapping, javaType, xmlType);
            row.deserializer = deserializer;
            if(deserializer instanceof Initializable)
                ((Initializable)deserializer).initialize(this);
        }
        return row.deserializer;
    }

    public Deserializer getDeserializer(String encoding, Class javaType) throws Exception {
        return getDeserializer(encoding, javaType, null);
    }

    public Deserializer getDeserializer(String encoding, QName xmlType) throws Exception {
        return getDeserializer(encoding, null, xmlType);
    }

    public Class getJavaType(String encoding, QName xmlType) throws Exception {
        if(xmlType == null)
            throw new IllegalArgumentException("getJavaType requires an XML type");
        InternalTypeMappingRegistryImpl$Row row = getRowMatching(encoding, xmlType);
        if(row.javaType == null) {
            ExtendedTypeMapping mapping = (ExtendedTypeMapping)TypeMappingUtil.getTypeMapping(registry, encoding);
            if(mapping != null)
                return mapping.getJavaType(xmlType);
            else
                return null;
        } else {
            return row.javaType;
        }
    }

    public QName getXmlType(String encoding, Class javaType) throws Exception {
        if(javaType == null)
            throw new IllegalArgumentException("getXmlType requires a Java type");
        InternalTypeMappingRegistryImpl$Row row = getRowMatching(encoding, javaType);
        if(row.xmlType == null) {
            ExtendedTypeMapping mapping = (ExtendedTypeMapping)TypeMappingUtil.getTypeMapping(registry, encoding);
            if(mapping != null)
                return mapping.getXmlType(javaType);
            else
                return null;
        } else {
            return row.xmlType;
        }
    }

    static  {
        NULL_ROW = InternalTypeMappingRegistryImpl$Row.createNull();
        NULL_ENTRY = InternalTypeMappingRegistryImpl$Entry.createNull(NULL_ROW);
    }
}
