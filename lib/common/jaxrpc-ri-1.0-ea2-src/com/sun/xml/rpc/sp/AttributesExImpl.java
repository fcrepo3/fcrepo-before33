// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AttributesExImpl.java

package com.sun.xml.rpc.sp;

import org.xml.sax.Attributes;

// Referenced classes of package com.sun.xml.rpc.sp:
//            AttributesEx

final class AttributesExImpl
    implements AttributesEx {

    int length;
    String data[];
    private String idAttributeName;
    private static final String SPECIFIED_TRUE = "";

    public AttributesExImpl() {
        length = 0;
        data = null;
    }

    public AttributesExImpl(Attributes atts) {
        setAttributes(atts);
    }

    public int getLength() {
        return length;
    }

    public String getURI(int index) {
        if(index >= 0 && index < length)
            return data[index * 7];
        else
            return null;
    }

    public String getLocalName(int index) {
        if(index >= 0 && index < length)
            return data[index * 7 + 1];
        else
            return null;
    }

    public String getQName(int index) {
        if(index >= 0 && index < length)
            return data[index * 7 + 2];
        else
            return null;
    }

    public String getType(int index) {
        if(index >= 0 && index < length)
            return data[index * 7 + 3];
        else
            return null;
    }

    public String getValue(int index) {
        if(index >= 0 && index < length)
            return data[index * 7 + 4];
        else
            return null;
    }

    public String getDefault(int index) {
        if(index >= 0 && index < length)
            return data[index * 7 + 5];
        else
            return null;
    }

    public boolean isSpecified(int index) {
        if(index >= 0 && index < length)
            return data[index * 7 + 6] == "";
        else
            return false;
    }

    public int getIndex(String uri, String localName) {
        int max = length * 7;
        for(int i = 0; i < max; i += 7)
            if(data[i].equals(uri) && data[i + 1].equals(localName))
                return i / 7;

        return -1;
    }

    public int getIndex(String qName) {
        int max = length * 7;
        for(int i = 0; i < max; i += 7)
            if(data[i + 2].equals(qName))
                return i / 7;

        return -1;
    }

    public String getType(String uri, String localName) {
        int max = length * 7;
        for(int i = 0; i < max; i += 7)
            if(data[i].equals(uri) && data[i + 1].equals(localName))
                return data[i + 3];

        return null;
    }

    public String getType(String qName) {
        int max = length * 7;
        for(int i = 0; i < max; i += 7)
            if(data[i + 2].equals(qName))
                return data[i + 3];

        return null;
    }

    public String getValue(String uri, String localName) {
        int max = length * 7;
        for(int i = 0; i < max; i += 7)
            if(data[i].equals(uri) && data[i + 1].equals(localName))
                return data[i + 4];

        return null;
    }

    public String getValue(String qName) {
        int max = length * 7;
        for(int i = 0; i < max; i += 7)
            if(data[i + 2].equals(qName))
                return data[i + 4];

        return null;
    }

    public void clear() {
        int max = length * 7;
        for(int i = 0; i < max; i++)
            data[i] = null;

        length = 0;
    }

    public void setAttributes(Attributes atts) {
        clear();
        length = atts.getLength();
        if(length > 0) {
            data = new String[length * 7];
            for(int i = 0; i < length; i++) {
                data[i * 7] = atts.getURI(i);
                data[i * 7 + 1] = atts.getLocalName(i);
                data[i * 7 + 2] = atts.getQName(i);
                data[i * 7 + 3] = atts.getType(i);
                data[i * 7 + 4] = atts.getValue(i);
            }

        }
    }

    public void addAttribute(String uri, String localName, String qName, String type, String value) {
        ensureCapacity(length + 1);
        data[length * 7] = uri;
        data[length * 7 + 1] = localName;
        data[length * 7 + 2] = qName;
        data[length * 7 + 3] = type;
        data[length * 7 + 4] = value;
        length++;
    }

    public void addAttribute(String uri, String localName, String qName, String type, String value, String defaultValue, boolean isSpecified) {
        ensureCapacity(length + 1);
        data[length * 7] = uri;
        data[length * 7 + 1] = localName;
        data[length * 7 + 2] = qName;
        data[length * 7 + 3] = type;
        data[length * 7 + 4] = value;
        data[length * 7 + 5] = defaultValue;
        data[length * 7 + 6] = isSpecified ? "" : null;
        length++;
    }

    public void setAttribute(int index, String uri, String localName, String qName, String type, String value) {
        if(index >= 0 && index < length) {
            data[index * 7] = uri;
            data[index * 7 + 1] = localName;
            data[index * 7 + 2] = qName;
            data[index * 7 + 3] = type;
            data[index * 7 + 4] = value;
        } else {
            badIndex(index);
        }
    }

    public void setAttribute(int index, String uri, String localName, String qName, String type, String value, String defaultValue, 
            boolean isSpecified) {
        if(index >= 0 && index < length) {
            data[index * 7] = uri;
            data[index * 7 + 1] = localName;
            data[index * 7 + 2] = qName;
            data[index * 7 + 3] = type;
            data[index * 7 + 4] = value;
            data[index * 7 + 5] = defaultValue;
            data[index * 7 + 6] = isSpecified ? "" : null;
        } else {
            badIndex(index);
        }
    }

    public void removeAttribute(int index) {
        if(index >= 0 && index < length) {
            data[index * 7] = null;
            data[index * 7 + 1] = null;
            data[index * 7 + 2] = null;
            data[index * 7 + 3] = null;
            data[index * 7 + 4] = null;
            data[index * 7 + 5] = null;
            data[index * 7 + 6] = null;
            if(index < length - 1)
                System.arraycopy(data, (index + 1) * 7, data, index * 7, (length - index - 1) * 7);
            length--;
        } else {
            badIndex(index);
        }
    }

    public void setURI(int index, String uri) {
        if(index >= 0 && index < length)
            data[index * 7] = uri;
        else
            badIndex(index);
    }

    public void setLocalName(int index, String localName) {
        if(index >= 0 && index < length)
            data[index * 7 + 1] = localName;
        else
            badIndex(index);
    }

    public void setQName(int index, String qName) {
        if(index >= 0 && index < length)
            data[index * 7 + 2] = qName;
        else
            badIndex(index);
    }

    public void setType(int index, String type) {
        if(index >= 0 && index < length)
            data[index * 7 + 3] = type;
        else
            badIndex(index);
    }

    public void setValue(int index, String value) {
        if(index >= 0 && index < length)
            data[index * 7 + 4] = value;
        else
            badIndex(index);
    }

    public void setDefault(int index, String defaultValue) {
        if(index >= 0 && index < length)
            data[index * 7 + 5] = defaultValue;
        else
            badIndex(index);
    }

    public void setSpecified(int index, boolean specified) {
        if(index >= 0 && index < length)
            data[index * 7 + 6] = specified ? "" : null;
        else
            badIndex(index);
    }

    public String getIdAttributeName() {
        return idAttributeName;
    }

    void setIdAttributeName(String name) {
        idAttributeName = name;
    }

    private void ensureCapacity(int n) {
        if(n <= 0)
            return;
        int max;
        if(data == null || data.length == 0) {
            max = 35;
        } else {
            if(data.length >= n * 7)
                return;
            max = data.length;
        }
        for(; max < n * 7; max *= 2);
        String newData[] = new String[max];
        if(length > 0)
            System.arraycopy(data, 0, newData, 0, length * 7);
        data = newData;
    }

    private void badIndex(int index) throws ArrayIndexOutOfBoundsException {
        String msg = "Attempt to modify attribute at illegal index: " + index;
        throw new ArrayIndexOutOfBoundsException(msg);
    }
}
