// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   SOAPFaultInfoSerializer.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.encoding.simpletype.XSDQNameEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDStringEncoder;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.soap.message.SOAPFaultInfo;
import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            ObjectSerializerBase, SOAPDeserializationState, SimpleTypeSerializer, Initializable,
//            JAXRPCDeserializer, JAXRPCSerializer, CombinedSerializer, InternalTypeMappingRegistry,
//            SOAPDeserializationContext, SOAPSerializationContext

public class SOAPFaultInfoSerializer extends ObjectSerializerBase
    implements Initializable {

    protected static final QName FAULTACTOR_QNAME = new QName("", "faultactor");
    protected static final QName XSD_STRING_TYPE_QNAME;
    protected static final QName XSD_QNAME_TYPE_QNAME;
    private static final int DETAIL_INDEX = 0;
    protected static final CombinedSerializer _XSDStringSerializer;
    protected static final CombinedSerializer _XSDQNameSerializer;
    protected static final QName FAULTCODE_QNAME = new QName("", "faultcode");
    protected static final QName FAULTSTRING_QNAME = new QName("", "faultstring");
    protected static final QName DETAIL_QNAME = new QName("", "detail");

    public SOAPFaultInfoSerializer(boolean encodeType, boolean isNullable) {
        super(SOAPConstants.QNAME_SOAP_FAULT, encodeType, isNullable, null);
    }

    public void initialize(InternalTypeMappingRegistry internaltypemappingregistry) throws Exception {
    }

    public Object doDeserialize(SOAPDeserializationState state, XMLReader reader, SOAPDeserializationContext context) throws Exception {
        SOAPFaultInfo instance = null;
        boolean isComplete = true;
        QName code = null;
        String string = null;
        String actor = null;
        Object detail = null;
        SOAPInstanceBuilder builder = null;
        reader.nextElementContent();
        XMLReaderUtil.verifyReaderState(reader, 1);
        QName elementName = reader.getName();
        if(elementName.equals(FAULTCODE_QNAME))
            code = (QName)_XSDQNameSerializer.deserialize(FAULTCODE_QNAME, reader, context);
        reader.nextElementContent();
        XMLReaderUtil.verifyReaderState(reader, 1);
        elementName = reader.getName();
        if(elementName.equals(FAULTSTRING_QNAME))
            string = (String)_XSDStringSerializer.deserialize(FAULTSTRING_QNAME, reader, context);
        if(reader.nextElementContent() == 1) {
            elementName = reader.getName();
            if(elementName.equals(FAULTACTOR_QNAME)) {
                actor = (String)_XSDStringSerializer.deserialize(FAULTACTOR_QNAME, reader, context);
                if(reader.nextElementContent() == 1)
                    elementName = reader.getName();
            }
            instance = new SOAPFaultInfo(code, string, actor, detail);
            if(elementName.equals(DETAIL_QNAME)) {
                detail = deserializeDetail(state, reader, context, instance);
                if(detail instanceof SOAPDeserializationState)
                    isComplete = false;
                else
                    instance.setDetail(detail);
                reader.nextElementContent();
            }
        }
        if(instance == null)
            instance = new SOAPFaultInfo(code, string, actor, detail);
        XMLReaderUtil.verifyReaderState(reader, 2);
        if (isComplete)
            return instance;
        else
            return state;
    }

    public void doSerializeInstance(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        SOAPFaultInfo instance = (SOAPFaultInfo)obj;
        _XSDQNameSerializer.serialize(instance.getCode(), FAULTCODE_QNAME, null, writer, context);
        _XSDStringSerializer.serialize(instance.getString(), FAULTSTRING_QNAME, null, writer, context);
        if(instance.getActor() != null)
            _XSDStringSerializer.serialize(instance.getActor(), FAULTACTOR_QNAME, null, writer, context);
        serializeDetail(instance.getDetail(), writer, context);
    }

    protected Object deserializeDetail(SOAPDeserializationState state, XMLReader reader, SOAPDeserializationContext context, SOAPFaultInfo instance) throws Exception {
        reader.skipElement();
        return null;
    }

    protected void serializeDetail(Object obj, XMLWriter xmlwriter, SOAPSerializationContext soapserializationcontext) throws Exception {
    }

    static  {
        XSD_STRING_TYPE_QNAME = SchemaConstants.QNAME_TYPE_STRING;
        XSD_QNAME_TYPE_QNAME = SchemaConstants.QNAME_TYPE_QNAME;
        _XSDStringSerializer = new SimpleTypeSerializer(XSD_STRING_TYPE_QNAME, false, true, null, XSDStringEncoder.getInstance());
        _XSDQNameSerializer = new SimpleTypeSerializer(XSD_QNAME_TYPE_QNAME, false, true, null, XSDQNameEncoder.getInstance());
    }
}
