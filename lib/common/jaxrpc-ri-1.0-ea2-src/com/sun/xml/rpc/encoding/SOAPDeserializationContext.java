// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPDeserializationContext.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.xml.XmlUtil;
import java.util.*;
import javax.xml.soap.SOAPMessage;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            SOAPDeserializationState, DeserializationException

public class SOAPDeserializationContext {

    protected Stack encodingStyleContext;
    protected String curEncodingStyle;
    protected boolean isSOAPEncodingStyle;
    protected List encodingStyleURIs;
    protected SOAPMessage message;
    protected Map stateIds;

    public SOAPDeserializationContext() {
        encodingStyleContext = new Stack();
        curEncodingStyle = null;
        isSOAPEncodingStyle = false;
        encodingStyleURIs = null;
        stateIds = new HashMap();
        pushEncodingStyle("");
    }

    public SOAPDeserializationState getStateFor(String id) {
        if(id == null)
            return null;
        SOAPDeserializationState elementState = (SOAPDeserializationState)stateIds.get(id);
        if(elementState == null) {
            elementState = new SOAPDeserializationState();
            stateIds.put(id, elementState);
        }
        return elementState;
    }

    public void deserializeMultiRefObjects(XMLReader reader) {
        try {
            SOAPDeserializationState elementState;
            for(; reader.nextElementContent() == 1; elementState.deserialize(null, reader, this)) {
                String id = reader.getAttributes().getValue("", "id");
                if(id == null)
                    throw new DeserializationException("soap.missingTrailingBlockID");
                elementState = getStateFor(id);
            }

        }
        catch(JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        }
        catch(Exception e) {
            throw new DeserializationException(new LocalizableExceptionAdapter(e));
        }
    }

    public void doneDeserializing() {
        SOAPDeserializationState state;
        for(Iterator iter = stateIds.values().iterator(); iter.hasNext(); state.promoteToCompleteOrFail())
            state = (SOAPDeserializationState)iter.next();

    }

    public void setMessage(SOAPMessage m) {
        message = m;
    }

    public SOAPMessage getMessage() {
        return message;
    }

    public void pushEncodingStyle(String newEncodingStyle) {
        encodingStyleContext.push(newEncodingStyle);
        initEncodingStyleInfo();
    }

    public void popEncodingStyle() {
        encodingStyleContext.pop();
        initEncodingStyleInfo();
    }

    public String getEncodingStyle() {
        return curEncodingStyle;
    }

    public boolean processEncodingStyle(XMLReader reader) throws Exception {
        Attributes attrs = reader.getAttributes();
        String newEncodingStyle = attrs.getValue("http://schemas.xmlsoap.org/soap/envelope/", "encodingStyle");
        if(newEncodingStyle == null) {
            return false;
        } else {
            pushEncodingStyle(newEncodingStyle);
            return true;
        }
    }

    public void verifyEncodingStyle(String expectedEncodingStyle) {
        if(expectedEncodingStyle == null)
            return;
        if(expectedEncodingStyle == "http://schemas.xmlsoap.org/soap/encoding/" || expectedEncodingStyle.equals("http://schemas.xmlsoap.org/soap/encoding/")) {
            if(isSOAPEncodingStyle)
                return;
        } else
        if(encodingStyleURIs == null) {
            if(curEncodingStyle.startsWith(expectedEncodingStyle))
                return;
        } else {
            for(int i = 0; i < encodingStyleURIs.size(); i++) {
                String uri = (String)encodingStyleURIs.get(i);
                if(uri.startsWith(expectedEncodingStyle))
                    return;
            }

        }
        throw new DeserializationException("soap.unexpectedEncodingStyle", new Object[] {
            expectedEncodingStyle, curEncodingStyle
        });
    }

    private void initEncodingStyleInfo() {
        curEncodingStyle = (String)encodingStyleContext.peek();
        if(curEncodingStyle.indexOf(' ') == -1) {
            encodingStyleURIs = null;
            isSOAPEncodingStyle = curEncodingStyle.startsWith("http://schemas.xmlsoap.org/soap/encoding/");
        } else {
            encodingStyleURIs = XmlUtil.parseTokenList(curEncodingStyle);
            isSOAPEncodingStyle = false;
            for(int i = 0; i < encodingStyleURIs.size(); i++) {
                String uri = (String)encodingStyleURIs.get(i);
                if(!uri.startsWith("http://schemas.xmlsoap.org/soap/encoding/"))
                    continue;
                isSOAPEncodingStyle = true;
                break;
            }

        }
    }
}
