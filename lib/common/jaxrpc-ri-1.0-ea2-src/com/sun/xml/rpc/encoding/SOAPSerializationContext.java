// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPSerializationContext.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.streaming.XMLWriter;
import java.util.*;
import javax.xml.soap.SOAPMessage;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            SOAPSerializationState, SerializationException, ReferenceableSerializer

public class SOAPSerializationContext {

    protected HashMap map;
    protected Map properties;
    protected LinkedList list;
    protected String prefix;
    protected long next;
    protected Stack encodingStyleContext;
    protected String curEncodingStyle;
    protected Set activeObjects;
    protected SOAPMessage message;

    public SOAPSerializationContext() {
        this(null);
    }

    public SOAPSerializationContext(String prefix) {
        encodingStyleContext = new Stack();
        curEncodingStyle = null;
        if(prefix == null)
            prefix = "ID";
        map = new HashMap();
        properties = new HashMap();
        list = new LinkedList();
        this.prefix = prefix;
        next = 1L;
    }

    public SOAPSerializationState registerObject(Object obj, ReferenceableSerializer serializer) {
        SOAPSerializationContext$MapKey key = new SOAPSerializationContext$MapKey(obj);
        SOAPSerializationState state = (SOAPSerializationState)map.get(key);
        if(state == null) {
            state = new SOAPSerializationState(obj, nextID(), serializer);
            map.put(key, state);
            list.add(state);
        }
        return state;
    }

    public SOAPSerializationState lookupObject(Object obj) {
        SOAPSerializationContext$MapKey key = new SOAPSerializationContext$MapKey(obj);
        return (SOAPSerializationState)map.get(key);
    }

    public void serializeMultiRefObjects(XMLWriter writer) {
        Object obj;
        ReferenceableSerializer ser;
        for(; !list.isEmpty(); ser.serializeInstance(obj, null, true, writer, this)) {
            SOAPSerializationState state = (SOAPSerializationState)list.removeFirst();
            obj = state.getObject();
            ser = state.getSerializer();
        }

    }

    public String nextID() {
        return prefix + next++;
    }

    public boolean pushEncodingStyle(String newEncodingStyle, XMLWriter writer) throws Exception {
        if(newEncodingStyle == curEncodingStyle || newEncodingStyle.equals(curEncodingStyle)) {
            return false;
        } else {
            writer.writeAttribute(SOAPConstants.QNAME_ENVELOPE_ENCODINGSTYLE, newEncodingStyle);
            encodingStyleContext.push(newEncodingStyle);
            initEncodingStyleInfo();
            return true;
        }
    }

    public void popEncodingStyle() {
        encodingStyleContext.pop();
        initEncodingStyleInfo();
    }

    private void initEncodingStyleInfo() {
        if(encodingStyleContext.empty())
            curEncodingStyle = null;
        else
            curEncodingStyle = (String)encodingStyleContext.peek();
    }

    public String getEncodingStyle() {
        return curEncodingStyle;
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public void removeProperty(String key) {
        properties.remove(key);
    }

    public void beginFragment() {
        activeObjects = new HashSet();
    }

    public void beginSerializing(Object obj) throws SerializationException {
        if(obj != null && activeObjects != null) {
            if(activeObjects.contains(obj))
                throw new SerializationException("soap.circularReferenceDetected", new Object[] {
                    obj
                });
            activeObjects.add(obj);
        }
    }

    public void doneSerializing(Object obj) throws SerializationException {
        if(obj != null && activeObjects != null)
            activeObjects.remove(obj);
    }

    public void endFragment() {
        activeObjects = null;
    }

    public void setMessage(SOAPMessage m) {
        message = m;
    }

    public SOAPMessage getMessage() {
        return message;
    }
}
