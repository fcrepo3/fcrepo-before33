// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPMessageContext.java

package com.sun.xml.rpc.soap.message;

import com.sun.xml.messaging.util.ByteInputStream;
import com.sun.xml.rpc.util.NullIterator;
import com.sun.xml.rpc.util.xml.XmlUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.rpc.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;

public class SOAPMessageContext
    implements javax.xml.rpc.handler.soap.SOAPMessageContext {

    private SOAPMessage _message;
    private boolean _failure;
    private Map _properties;
    private static MessageFactory _messageFactory;
    private static final String DEFAULT_SERVER_ERROR_ENVELOPE = "<?xml version='1.0' encoding='UTF-8'?><env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\"><env:Body><env:Fault><faultcode>env:Server</faultcode><faultstring>Internal server error</faultstring></env:Fault></env:Body></env:Envelope>";

    public SOAPMessageContext() {
    }

    public SOAPMessage getMessage() {
        return _message;
    }

    public void setMessage(SOAPMessage message) {
        _message = message;
    }

    public boolean isFailure() {
        return _failure;
    }

    public void setFailure(boolean b) {
        _failure = b;
    }

    public Object getProperty(String name) {
        if(_properties == null)
            return null;
        else
            return _properties.get(name);
    }

    public void setProperty(String name, Object value) {
        if(_properties == null)
            _properties = new HashMap();
        _properties.put(name, value);
    }

    public void removeProperty(String name) {
        if(_properties != null)
            _properties.remove(name);
    }

    public boolean containsProperty(String name) {
        if(_properties == null)
            return false;
        else
            return _properties.containsKey(name);
    }

    public Iterator getPropertyNames() {
        if(_properties == null)
            return new NullIterator();
        else
            return _properties.keySet().iterator();
    }

    public SOAPMessage createMessage() {
        try {
            return _messageFactory.createMessage();
        }
        catch(SOAPException soapexception) {
            return null;
        }
    }

    public SOAPMessage createMessage(MimeHeaders headers, InputStream in) throws IOException {
        try {
            return _messageFactory.createMessage(headers, in);
        }
        catch(SOAPException soapexception) {
            return null;
        }
    }

    public void writeInternalServerErrorResponse() {
        try {
            setFailure(true);
            SOAPMessage message = createMessage();
            message.getSOAPPart().setContent(new StreamSource(XmlUtil.getUTF8ByteInputStream("<?xml version='1.0' encoding='UTF-8'?><env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\"><env:Body><env:Fault><faultcode>env:Server</faultcode><faultstring>Internal server error</faultstring></env:Fault></env:Body></env:Envelope>")));
            setMessage(message);
        }
        catch(SOAPException soapexception) { }
    }

    public void writeSimpleErrorResponse(QName faultCode, String faultString) {
        try {
            setFailure(true);
            SOAPMessage message = createMessage();
            ByteArrayOutputStream bufferedStream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(bufferedStream, "UTF-8");
            writer.write("<?xml version='1.0' encoding='UTF-8'?>\n<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\"><env:Body><env:Fault><faultcode>env:");
            writer.write(faultCode.getLocalPart());
            writer.write("</faultcode><faultstring>");
            writer.write(faultString);
            writer.write("</faultstring></env:Fault></env:Body></env:Envelope>");
            writer.close();
            byte data[] = bufferedStream.toByteArray();
            message.getSOAPPart().setContent(new StreamSource(new ByteInputStream(data, data.length)));
            setMessage(message);
        }
        catch(Exception exception) {
            writeInternalServerErrorResponse();
        }
    }

    static  {
        try {
            _messageFactory = MessageFactory.newInstance();
        }
        catch(SOAPException soapexception) { }
    }
}
