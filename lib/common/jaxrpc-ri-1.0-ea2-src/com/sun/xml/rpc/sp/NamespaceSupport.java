// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   NamespaceSupport.java

package com.sun.xml.rpc.sp;

import java.util.*;

public class NamespaceSupport {

    public static final String XMLNS = "http://www.w3.org/XML/1998/namespace";
    private static final Iterator EMPTY_ENUMERATION = (new ArrayList()).iterator();
    private NamespaceSupport$Context contexts[];
    private NamespaceSupport$Context currentContext;
    private int contextPos;

    public NamespaceSupport() {
        reset();
    }

    public void reset() {
        contexts = new NamespaceSupport$Context[32];
        contextPos = 0;
        contexts[contextPos] = currentContext = new NamespaceSupport$Context(this);
        currentContext.declarePrefix("xml", "http://www.w3.org/XML/1998/namespace");
    }

    public void pushContext() {
        int max = contexts.length;
        contextPos++;
        if(contextPos >= max) {
            NamespaceSupport$Context newContexts[] = new NamespaceSupport$Context[max * 2];
            System.arraycopy(contexts, 0, newContexts, 0, max);
            max *= 2;
            contexts = newContexts;
        }
        currentContext = contexts[contextPos];
        if(currentContext == null)
            contexts[contextPos] = currentContext = new NamespaceSupport$Context(this);
        if(contextPos > 0)
            currentContext.setParent(contexts[contextPos - 1]);
    }

    public void popContext() {
        contextPos--;
        if(contextPos < 0) {
            throw new EmptyStackException();
        } else {
            currentContext = contexts[contextPos];
            return;
        }
    }

    public void slideContextUp() {
        contextPos--;
        currentContext = contexts[contextPos];
    }

    public void slideContextDown() {
        contextPos++;
        if(contexts[contextPos] == null)
            contexts[contextPos] = contexts[contextPos - 1];
        currentContext = contexts[contextPos];
    }

    public boolean declarePrefix(String prefix, String uri) {
        if(prefix.equals("xml") || prefix.equals("xmlns")) {
            return false;
        } else {
            currentContext.declarePrefix(prefix, uri);
            return true;
        }
    }

    public String[] processName(String qName, String parts[], boolean isAttribute) {
        String myParts[] = currentContext.processName(qName, isAttribute);
        if(myParts == null) {
            return null;
        } else {
            parts[0] = myParts[0];
            parts[1] = myParts[1];
            parts[2] = myParts[2];
            return parts;
        }
    }

    public String getURI(String prefix) {
        return currentContext.getURI(prefix);
    }

    public Iterator getPrefixes() {
        return currentContext.getPrefixes();
    }

    public String getPrefix(String uri) {
        return currentContext.getPrefix(uri);
    }

    public Iterator getPrefixes(String uri) {
        List prefixes = new ArrayList();
        for(Iterator allPrefixes = getPrefixes(); allPrefixes.hasNext();) {
            String prefix = (String)allPrefixes.next();
            if(uri.equals(getURI(prefix)))
                prefixes.add(prefix);
        }

        return prefixes.iterator();
    }

    public Iterator getDeclaredPrefixes() {
        return currentContext.getDeclaredPrefixes();
    }

    static Iterator access$000() {
        return EMPTY_ENUMERATION;
    }

}
