// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AttachmentSerializer.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.encoding.simpletype.AttachmentEncoder;
import com.sun.xml.rpc.encoding.simpletype.SimpleTypeConstants;
import com.sun.xml.rpc.encoding.simpletype.SimpleTypeEncoder;
import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;
import java.util.Iterator;
import javax.activation.DataHandler;
import javax.xml.rpc.namespace.QName;
import javax.xml.soap.*;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            SimpleTypeSerializer, SerializationException, DeserializationException, SerializerBase, 
//            SOAPSerializationContext, SOAPDeserializationContext, SerializerCallback

public class AttachmentSerializer extends SimpleTypeSerializer {

    protected AttachmentEncoder attachmentEncoder;
    protected boolean serializerAsAttachment;

    public AttachmentSerializer(QName type, boolean encodeType, boolean isNullable, String encodingStyle, boolean serializerAsAttachment, SimpleTypeEncoder encoder) {
        super(type, encodeType, isNullable, encodingStyle, encoder);
        this.serializerAsAttachment = serializerAsAttachment;
        if(encoder instanceof AttachmentEncoder)
            attachmentEncoder = (AttachmentEncoder)encoder;
        else
        if(serializerAsAttachment)
            throw new SerializationException("soap.no.attachment.encoder.and.serializeAsAttachment", type.toString());
    }

    public AttachmentSerializer(QName type, boolean encodeType, boolean isNullable, String encodingStyle, boolean serializerAsAttachment, AttachmentEncoder encoder) {
        super(type, encodeType, isNullable, encodingStyle, null);
        this.serializerAsAttachment = serializerAsAttachment;
        attachmentEncoder = encoder;
    }

    public void serialize(Object obj, QName name, SerializerCallback callback, XMLWriter writer, SOAPSerializationContext context) {
        if(serializerAsAttachment)
            serializeAsAttachment(obj, name, callback, writer, context);
        else
        if(super.encoder != null)
            super.serialize(obj, name, callback, writer, context);
        else
            throw new UnsupportedOperationException();
    }

    private void serializeAsAttachment(Object obj, QName name, SerializerCallback callback, XMLWriter writer, SOAPSerializationContext context) {
        try {
            writer.startElement(name == null ? super.type : name);
            if(obj == null) {
                if(!super.isNullable)
                    throw new SerializationException("xsd.unexpectedNull");
                writer.writeAttribute(SimpleTypeConstants.QNAME_XSI_NIL, "1");
            } else {
                if(super.encodeType) {
                    String attrVal = XMLWriterUtil.encodeQName(writer, super.type);
                    writer.writeAttribute(SimpleTypeConstants.QNAME_XSI_TYPE, attrVal);
                }
                String id = context.nextID();
                writer.writeAttribute(SOAPConstants.QNAME_ATTR_HREF, "cid:" + id);
                SOAPMessage message = context.getMessage();
                AttachmentPart attachment = message.createAttachmentPart(attachmentEncoder.objectToDataHandler(obj));
                attachment.setContentId(id);
                message.addAttachmentPart(attachment);
            }
            writer.endElement();
        }
        catch(JAXRPCExceptionBase e) {
            throw new SerializationException(e);
        }
        catch(Exception e) {
            throw new SerializationException(new LocalizableExceptionAdapter(e));
        }
    }

    public Object deserialize(QName name, XMLReader reader, SOAPDeserializationContext context) {
        boolean pushedEncodingStyle = false;
        try {
            String href = getHRef(reader);
            if(href != null) {
                skipEmptyContent(reader);
                SOAPMessage message = context.getMessage();
                MimeHeaders mimeHeaders = new MimeHeaders();
                mimeHeaders.addHeader("Content-Id", href.substring(4));
                Iterator attachments = message.getAttachments(mimeHeaders);
                if(!attachments.hasNext())
                    throw new DeserializationException("soap.missing.attachment.for.id", href);
                AttachmentPart attachment = (AttachmentPart)attachments.next();
                if(attachments.hasNext())
                    throw new DeserializationException("soap.multiple.attachments.for.id", href);
                else
                    return deserialize(attachment.getDataHandler(), context);
            }
        }
        catch(JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        }
        catch(Exception e) {
            throw new DeserializationException(new LocalizableExceptionAdapter(e));
        }
        return super.deserialize(name, reader, context);
    }

    public Object deserialize(DataHandler dataHandler, SOAPDeserializationContext context) throws DeserializationException, UnsupportedOperationException {
        if(attachmentEncoder == null)
            throw new UnsupportedOperationException();
        try {
            return attachmentEncoder.dataHandlerToObject(dataHandler);
        }
        catch(JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        }
        catch(Exception e) {
            throw new DeserializationException(new LocalizableExceptionAdapter(e));
        }
    }

    protected String getHRef(XMLReader reader) throws Exception {
        String href = null;
        Attributes attrs = reader.getAttributes();
        href = attrs.getValue("", "href");
        if(href != null && !href.startsWith("cid:"))
            throw new DeserializationException("soap.nonLocalReference", href);
        else
            return href;
    }
}
