// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   NamespaceSupport.java

package com.sun.xml.rpc.sp;

import java.util.*;

// Referenced classes of package com.sun.xml.rpc.sp:
//            NamespaceSupport

final class NamespaceSupport$Context {

    HashMap prefixTable;
    HashMap uriTable;
    Map elementNameTable;
    Map attributeNameTable;
    String defaultNS;
    private List declarations;
    private boolean tablesDirty;
    private NamespaceSupport$Context parent;
    private final NamespaceSupport this$0; /* synthetic field */

    NamespaceSupport$Context(NamespaceSupport this$0) {
        this.this$0 = this$0;
        defaultNS = null;
        declarations = null;
        tablesDirty = false;
        parent = null;
        copyTables();
    }

    void setParent(NamespaceSupport$Context parent) {
        this.parent = parent;
        declarations = null;
        prefixTable = parent.prefixTable;
        uriTable = parent.uriTable;
        elementNameTable = parent.elementNameTable;
        attributeNameTable = parent.attributeNameTable;
        defaultNS = parent.defaultNS;
        tablesDirty = false;
    }

    void declarePrefix(String prefix, String uri) {
        if(!tablesDirty)
            copyTables();
        if(declarations == null)
            declarations = new ArrayList();
        prefix = prefix.intern();
        uri = uri.intern();
        if("".equals(prefix)) {
            if("".equals(uri))
                defaultNS = null;
            else
                defaultNS = uri;
        } else {
            prefixTable.put(prefix, uri);
            uriTable.put(uri, prefix);
        }
        declarations.add(prefix);
    }

    String[] processName(String qName, boolean isAttribute) {
        Map table;
        if(isAttribute)
            table = elementNameTable;
        else
            table = attributeNameTable;
        String name[] = (String[])table.get(qName);
        if(name != null)
            return name;
        name = new String[3];
        int index = qName.indexOf(':');
        if(index == -1) {
            if(isAttribute || defaultNS == null)
                name[0] = "";
            else
                name[0] = defaultNS;
            name[1] = qName.intern();
            name[2] = name[1];
        } else {
            String prefix = qName.substring(0, index);
            String local = qName.substring(index + 1);
            String uri;
            if("".equals(prefix))
                uri = defaultNS;
            else
                uri = (String)prefixTable.get(prefix);
            if(uri == null)
                return null;
            name[0] = uri;
            name[1] = local.intern();
            name[2] = qName.intern();
        }
        table.put(name[2], name);
        tablesDirty = true;
        return name;
    }

    String getURI(String prefix) {
        if("".equals(prefix))
            return defaultNS;
        if(prefixTable == null)
            return null;
        else
            return (String)prefixTable.get(prefix);
    }

    String getPrefix(String uri) {
        if(uriTable == null)
            return null;
        else
            return (String)uriTable.get(uri);
    }

    Iterator getDeclaredPrefixes() {
        if(declarations == null)
            return NamespaceSupport.access$000();
        else
            return declarations.iterator();
    }

    Iterator getPrefixes() {
        if(prefixTable == null)
            return NamespaceSupport.access$000();
        else
            return prefixTable.keySet().iterator();
    }

    private void copyTables() {
        if(prefixTable != null)
            prefixTable = (HashMap)prefixTable.clone();
        else
            prefixTable = new HashMap();
        if(uriTable != null)
            uriTable = (HashMap)uriTable.clone();
        else
            uriTable = new HashMap();
        elementNameTable = new HashMap();
        attributeNameTable = new HashMap();
        tablesDirty = true;
    }
}
