// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPResponseSerializer.java

package com.sun.xml.rpc.encoding.soap;

import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.SOAPInstanceBuilder;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding.soap:
//            SOAPResponseStructure, SOAPResponseSerializer

public class SOAPResponseSerializer$SOAPResponseStructureBuilder
    implements SOAPInstanceBuilder {

    SOAPResponseStructure instance;
    List outParameterNames;

    public void setOutParameterName(int index, QName name) {
        outParameterNames.set(index, name);
    }

    SOAPResponseSerializer$SOAPResponseStructureBuilder(SOAPResponseStructure instance) {
        this.instance = null;
        outParameterNames = new ArrayList();
        this.instance = instance;
    }

    public int memberGateType(int memberIndex) {
        return 6;
    }

    public void construct() {
    }

    public void setMember(int index, Object memberValue) {
        try {
            if(index == 0)
                instance.returnValue = memberValue;
            else
                instance.outParameters.put(outParameterNames.get(index), memberValue);
        }
        catch(Exception e) {
            throw new DeserializationException("nestedSerializationError", new LocalizableExceptionAdapter(e));
        }
    }

    public void initialize() {
    }

    public void setInstance(Object instance) {
        instance = (SOAPResponseStructure)instance;
    }

    public Object getInstance() {
        return instance;
    }
}
