// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   TypeMappingRegistryImpl.java

package com.sun.xml.rpc.encoding;

import java.util.*;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.encoding.TypeMappingRegistry;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            TypeMappingException, TypeMappingImpl, SerializerConstants

public class TypeMappingRegistryImpl
    implements TypeMappingRegistry, SerializerConstants {

    protected Map mappings;
    protected TypeMapping defaultMapping;

    public TypeMappingRegistryImpl() {
        mappings = new HashMap();
        defaultMapping = null;
    }

    public TypeMapping register(String namespaceURI, TypeMapping mapping) {
        if(mapping == null || namespaceURI == null)
            throw new IllegalArgumentException();
        if(!mappingSupportsEncoding(mapping, namespaceURI)) {
            throw new TypeMappingException("typemapping.mappingDoesNotSupportEncoding", namespaceURI);
        } else {
            TypeMapping oldMapping = (TypeMapping)mappings.get(namespaceURI);
            mappings.put(namespaceURI, mapping);
            return oldMapping;
        }
    }

    public void registerDefault(TypeMapping mapping) {
        defaultMapping = mapping;
    }

    public TypeMapping getDefaultTypeMapping() {
        return defaultMapping;
    }

    public Iterator getTypeMappings() {
        return mappings.values().iterator();
    }

    public String[] getRegisteredNamespaces() {
        Set namespaceSet = mappings.keySet();
        return (String[])namespaceSet.toArray(new String[namespaceSet.size()]);
    }

    public TypeMapping getTypeMapping(String namespaceURI) {
        if(namespaceURI == null)
            throw new IllegalArgumentException();
        TypeMapping mapping = (TypeMapping)mappings.get(namespaceURI);
        if(mapping == null)
            mapping = defaultMapping;
        return mapping;
    }

    public TypeMapping createTypeMapping() {
        return new TypeMappingImpl();
    }

    public TypeMapping unregisterTypeMapping(String namespaceURI) {
        return (TypeMapping)mappings.remove(namespaceURI);
    }

    public boolean removeTypeMapping(TypeMapping mapping) {
        if(mapping == null)
            throw new IllegalArgumentException("mapping cannot be null");
        Set typeEntries = mappings.entrySet();
        Iterator eachEntry = typeEntries.iterator();
        boolean typeMappingFound = false;
        while(eachEntry.hasNext())  {
            java.util.Map$Entry currentEntry = (java.util.Map$Entry)eachEntry.next();
            if(mapping.equals(currentEntry.getValue())) {
                eachEntry.remove();
                typeMappingFound = true;
            }
        }
        return typeMappingFound;
    }

    public void clear() {
        mappings.clear();
    }

    protected boolean mappingSupportsEncoding(TypeMapping mapping, String namespaceURI) {
        String encodings[] = mapping.getSupportedNamespaces();
        for(int i = 0; i < encodings.length; i++)
            if(encodings[i].equals(namespaceURI))
                return true;

        return false;
    }
}
