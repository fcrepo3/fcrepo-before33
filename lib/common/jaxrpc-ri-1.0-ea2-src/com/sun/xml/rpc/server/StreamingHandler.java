// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   StreamingHandler.java

package com.sun.xml.rpc.server;

import com.sun.xml.messaging.util.ByteInputStream;
import com.sun.xml.messaging.util.ByteOutputStream;
import com.sun.xml.rpc.encoding.JAXRPCSerializer;
import com.sun.xml.rpc.encoding.ReferenceableSerializer;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPFaultInfoSerializer;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.soap.message.Handler;
import com.sun.xml.rpc.soap.message.InternalSOAPMessage;
import com.sun.xml.rpc.soap.message.SOAPBlockInfo;
import com.sun.xml.rpc.soap.message.SOAPFaultInfo;
import com.sun.xml.rpc.soap.message.SOAPHeaderBlockInfo;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.PrefixFactoryImpl;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderFactory;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.streaming.XMLWriterFactory;
import com.sun.xml.rpc.util.localization.Localizable;
import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import com.sun.xml.rpc.util.localization.Localizer;
import com.sun.xml.rpc.util.xml.XmlUtil;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogSource;

// Referenced classes of package com.sun.xml.rpc.server:
//            StreamingHandlerState

public abstract class StreamingHandler
    implements Handler {

    private static final SOAPFaultInfoSerializer soapFaultInfoSerializer = new SOAPFaultInfoSerializer(false, false);
    private Localizer localizer;
    private LocalizableMessageFactory messageFactory;
    private static final String MUST_UNDERSTAND_FAULT_MESSAGE_STRING = "SOAP must understand error";
    private static final String NO_BODY_INFO_MESSAGE_STRING = "Missing body information";
    private static final String BODY_EXPECTED_MESSAGE_STRING = "SOAP body expected";
    private static final String INVALID_ENVELOPE_CONTENT_MESSAGE_STRING = "Invalid content in SOAP envelope";
    private static final String INVALID_ENVELOPE_MESSAGE_STRING = "Invalid SOAP envelope";
    private static final String ILLEGAL_VALUE_OF_MUST_UNDERSTAND_ATTRIBUTE_FAULT_MESSAGE_STRING = "Illegal value of SOAP mustUnderstand attribute";
    private static final Log logger = LogSource.getInstance("com.sun.xml.rpc.server");

    protected StreamingHandler() {
        localizer = new Localizer();
        messageFactory = new LocalizableMessageFactory("com.sun.xml.rpc.resources.tie");
    }

    protected String getActor() {
        return null;
    }

    public void handle(SOAPMessageContext context) {
        StreamingHandlerState state = new StreamingHandlerState(context);
        try {
            XMLReader reader = null;
            try {
                preHandlingHook(state);
                if(state.isFailure())
                    return;
                java.io.InputStream istream = null;
                javax.xml.transform.Source source = state.getRequest().getMessage().getSOAPPart().getContent();
                if(source instanceof StreamSource) {
                    istream = ((StreamSource)source).getInputStream();
                } else {
                    Transformer transformer = XmlUtil.newTransformer();
                    ByteOutputStream bos = new ByteOutputStream();
                    transformer.transform(source, new StreamResult(bos));
                    istream = new ByteInputStream(bos.getBytes(), bos.getCount());
                }
                reader = getXMLReaderFactory().createXMLReader(istream);
                preEnvelopeReadingHook(state);
                reader.nextElementContent();
                SOAPDeserializationContext deserializationContext = new SOAPDeserializationContext();
                deserializationContext.setMessage(state.getRequest().getMessage());
                if(reader.getState() == 1 && "http://schemas.xmlsoap.org/soap/envelope/".equals(reader.getURI()) && "Envelope".equals(reader.getLocalName())) {
                    boolean envelopePushedEncodingStyle = deserializationContext.processEncodingStyle(reader);
                    preHeaderReadingHook(state);
                    if(state.isFailure())
                        return;
                    reader.nextElementContent();
                    if(reader.getState() == 1 && "http://schemas.xmlsoap.org/soap/envelope/".equals(reader.getURI())) {
                        if("Header".equals(reader.getLocalName())) {
                            boolean headerPushedEncodingStyle = deserializationContext.processEncodingStyle(reader);
                            processHeaders(reader, deserializationContext, state);
                            if(state.isFailure())
                                return;
                            postHeaderReadingHook(state);
                            if(state.isFailure())
                                return;
                            if(headerPushedEncodingStyle)
                                deserializationContext.popEncodingStyle();
                            reader.nextElementContent();
                        }
                        if(reader.getState() == 1 && "http://schemas.xmlsoap.org/soap/envelope/".equals(reader.getURI()) && "Body".equals(reader.getLocalName())) {
                            boolean bodyPushedEncodingStyle = deserializationContext.processEncodingStyle(reader);
                            if(reader.nextElementContent() == 2) {
                                SOAPFaultInfo fault = new SOAPFaultInfo(SOAPConstants.FAULT_CODE_CLIENT, "Missing body information", getActor());
                                reportFault(fault, state);
                            } else {
                                peekFirstBodyElement(reader, deserializationContext, state);
                                if(state.isFailure())
                                    return;
                                preBodyReadingHook(state);
                                if(state.isFailure())
                                    return;
                                readFirstBodyElement(reader, deserializationContext, state);
                                if(state.isFailure())
                                    return;
                                deserializationContext.deserializeMultiRefObjects(reader);
                                postBodyReadingHook(state);
                                for(; reader.nextElementContent() == 1; reader.skipElement());
                                if(bodyPushedEncodingStyle)
                                    deserializationContext.popEncodingStyle();
                                deserializationContext.doneDeserializing();
                            }
                        } else {
                            SOAPFaultInfo fault = new SOAPFaultInfo(SOAPConstants.FAULT_CODE_CLIENT, "SOAP body expected", getActor());
                            reportFault(fault, state);
                        }
                    } else {
                        SOAPFaultInfo fault = new SOAPFaultInfo(SOAPConstants.FAULT_CODE_CLIENT, "Invalid content in SOAP envelope", getActor());
                        reportFault(fault, state);
                    }
                    if(envelopePushedEncodingStyle)
                        deserializationContext.popEncodingStyle();
                } else {
                    SOAPFaultInfo fault = new SOAPFaultInfo(SOAPConstants.FAULT_CODE_CLIENT, "Invalid SOAP envelope", getActor());
                    reportFault(fault, state);
                }
                if(state.isFailure())
                    return;
                postEnvelopeReadingHook(state);
                if(state.isFailure())
                    return;
                processingHook(state);
            }
            catch(Exception e) {
                String message = null;
                if(e instanceof Localizable)
                    message = localizer.localize((Localizable)e);
                else
                    message = localizer.localize(messageFactory.getMessage("error.caughtExceptionWhileHandlingRequest", new Object[] {
                        e.toString()
                    }));
                logger.error(message, e);
                String faultMessage = localizer.localize(messageFactory.getMessage("message.faultMessageForException", message));
                SOAPFaultInfo fault = new SOAPFaultInfo(SOAPConstants.FAULT_CODE_SERVER, faultMessage, getActor());
                reportFault(fault, state);
            }
            finally {
                if(reader != null)
                    reader.close();
                try {
                    preResponseWritingHook(state);
                    writeResponse(state);
                    context.setFailure(state.getResponse().isFailure());
                    context.setMessage(state.getResponse().getMessage());
                    postResponseWritingHook(state);
                }
                catch(Exception e) {
                    String message = null;
                    if(e instanceof Localizable)
                        message = localizer.localize((Localizable)e);
                    else
                        message = localizer.localize(messageFactory.getMessage("error.caughtExceptionWhileHandlingRequest", new Object[] {
                            e.toString()
                        }));
                    logger.error(message, e);
                    String faultMessage = localizer.localize(messageFactory.getMessage("message.faultMessageForException", message));
                    SOAPFaultInfo fault = new SOAPFaultInfo(SOAPConstants.FAULT_CODE_SERVER, faultMessage, getActor());
                    reportFault(fault, state);
                    writeResponse(state);
                    context.setFailure(state.getResponse().isFailure());
                    context.setMessage(state.getResponse().getMessage());
                }
            }
        }
        catch(Exception e) {
            String message = null;
            if(e instanceof Localizable)
                message = localizer.localize((Localizable)e);
            else
                message = localizer.localize(messageFactory.getMessage("error.caughtExceptionWhilePreparingResponse", new Object[] {
                    e.toString()
                }));
            logger.error(message, e);
            context.writeInternalServerErrorResponse();
        }
        finally {
            try {
                postHandlingHook(state);
            }
            catch(Exception e) {
                String message = null;
                if(e instanceof Localizable)
                    logger.error(localizer.localize((Localizable)e), e);
                else
                    logger.error(localizer.localize(messageFactory.getMessage("error.caughtExceptionPostHandlingRequest", new Object[] {
                        e
                    })));
                logger.error(message, e);
                context.writeInternalServerErrorResponse();
            }
        }
    }

    protected void processHeaders(XMLReader reader, SOAPDeserializationContext deserializationContext, StreamingHandlerState state) throws Exception {
        for(; reader.nextElementContent() != 2; processHeaderElement(reader, deserializationContext, state));
    }

    protected void processHeaderElement(XMLReader reader, SOAPDeserializationContext deserializationContext, StreamingHandlerState state) throws Exception {
        Attributes attributes = reader.getAttributes();
        String actorAttr = attributes.getValue("http://schemas.xmlsoap.org/soap/envelope/", "actor");
        String mustUnderstandAttr = attributes.getValue("http://schemas.xmlsoap.org/soap/envelope/", "actor");
        boolean mustUnderstand = false;
        if(mustUnderstandAttr != null)
            if(mustUnderstandAttr.equals("1"))
                mustUnderstand = true;
            else
            if(!mustUnderstandAttr.equals("0")) {
                SOAPFaultInfo fault = new SOAPFaultInfo(SOAPConstants.FAULT_CODE_CLIENT, "Illegal value of SOAP mustUnderstand attribute", getActor());
                reportFault(fault, state);
                return;
            }
        if(getActor() == null && (actorAttr == null || actorAttr.equals("http://schema.xmlsoap.org/soap/actor/next")) || getActor() != null && getActor().equals(actorAttr)) {
            SOAPHeaderBlockInfo headerInfo = new SOAPHeaderBlockInfo(reader.getName(), actorAttr, mustUnderstand);
            boolean succeeded = readHeaderElement(headerInfo, reader, deserializationContext, state);
            SOAPFaultInfo fault;
            if(!succeeded && mustUnderstand) {
                SOAPFaultInfo fault2 = new SOAPFaultInfo(SOAPConstants.FAULT_CODE_MUST_UNDERSTAND, "SOAP must understand error", getActor());
                reportFault(fault2, state);
                state.getRequest().setHeaderNotUnderstood(true);
                return;
            } else {
                return;
            }
        }
        if(mustUnderstand) {
            SOAPFaultInfo fault = new SOAPFaultInfo(SOAPConstants.FAULT_CODE_MUST_UNDERSTAND, "SOAP must understand error", getActor());
            reportFault(fault, state);
            state.getRequest().setHeaderNotUnderstood(true);
            return;
        } else {
            reader.skipElement();
            return;
        }
    }

    protected boolean readHeaderElement(SOAPHeaderBlockInfo headerInfo, XMLReader reader, SOAPDeserializationContext deserializationContext, StreamingHandlerState state) throws Exception {
        reader.skipElement();
        return false;
    }

    protected void peekFirstBodyElement(XMLReader xmlreader, SOAPDeserializationContext soapdeserializationcontext, StreamingHandlerState streaminghandlerstate) throws Exception {
    }

    protected void readFirstBodyElement(XMLReader reader, SOAPDeserializationContext deserializationContext, StreamingHandlerState state) throws Exception {
        reader.skipElement();
    }

    protected String[] getNamespaceDeclarations() {
        return null;
    }

    protected String getDefaultEnvelopeEncodingStyle() {
        return "http://schemas.xmlsoap.org/soap/encoding/";
    }

    protected String getPreferredCharacterEncoding() {
        return "UTF-8";
    }

    protected void writeResponse(StreamingHandlerState state) throws Exception {
        SOAPBlockInfo bodyInfo = state.getResponse().getBody();
        boolean pushedEncodingStyle = false;
        if(bodyInfo == null || bodyInfo.getName() == null || bodyInfo.getSerializer() == null) {
            SOAPFaultInfo fault = new SOAPFaultInfo(SOAPConstants.FAULT_CODE_SERVER, "Missing body information", getActor());
            reportFault(fault, state);
            return;
        }
        ByteArrayOutputStream bufferedStream = new ByteArrayOutputStream();
        XMLWriter writer = getXMLWriterFactory().createXMLWriter(bufferedStream, getPreferredCharacterEncoding());
        writer.setPrefixFactory(new PrefixFactoryImpl("ans"));
        SOAPSerializationContext serializationContext = new SOAPSerializationContext("ID");
        serializationContext.setMessage(state.getResponse().getMessage());
        writer.startElement("Envelope", "http://schemas.xmlsoap.org/soap/envelope/", "env");
        writer.writeNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        writer.writeNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        writer.writeNamespaceDeclaration("enc", "http://schemas.xmlsoap.org/soap/encoding/");
        String namespaceDeclarations[] = getNamespaceDeclarations();
        if(namespaceDeclarations != null) {
            for(int i = 0; i < namespaceDeclarations.length; i += 2)
                writer.writeNamespaceDeclaration(namespaceDeclarations[i], namespaceDeclarations[i + 1]);

        }
        if(getDefaultEnvelopeEncodingStyle() != null)
            pushedEncodingStyle = serializationContext.pushEncodingStyle(getDefaultEnvelopeEncodingStyle(), writer);
        boolean wroteHeader = false;
        for(Iterator iter = state.getResponse().headers(); iter.hasNext();) {
            SOAPHeaderBlockInfo headerInfo = (SOAPHeaderBlockInfo)iter.next();
            if(headerInfo.getValue() != null && headerInfo.getSerializer() != null) {
                if(!wroteHeader) {
                    writer.startElement("Header", "http://schemas.xmlsoap.org/soap/envelope/");
                    wroteHeader = true;
                }
                serializationContext.beginFragment();
                JAXRPCSerializer serializer = headerInfo.getSerializer();
                if(serializer instanceof ReferenceableSerializer)
                    ((ReferenceableSerializer)serializer).serializeInstance(headerInfo.getValue(), headerInfo.getName(), false, writer, serializationContext);
                else
                    serializer.serialize(headerInfo.getValue(), headerInfo.getName(), null, writer, serializationContext);
                serializationContext.endFragment();
            }
        }

        if(wroteHeader)
            writer.endElement();
        writer.startElement("Body", "http://schemas.xmlsoap.org/soap/envelope/", "env");
        serializationContext.beginFragment();
        bodyInfo.getSerializer().serialize(bodyInfo.getValue(), bodyInfo.getName(), null, writer, serializationContext);
        serializationContext.serializeMultiRefObjects(writer);
        serializationContext.endFragment();
        writer.endElement();
        writer.endElement();
        if(pushedEncodingStyle)
            serializationContext.popEncodingStyle();
        writer.close();
        byte data[] = bufferedStream.toByteArray();
        state.getResponse().getMessage().getSOAPPart().setContent(new StreamSource(new ByteInputStream(data, data.length)));
    }

    protected void preHandlingHook(StreamingHandlerState state) throws Exception {
        callRequestHandlers(state);
    }

    protected void postHandlingHook(StreamingHandlerState streaminghandlerstate) throws Exception {
    }

    protected void preEnvelopeReadingHook(StreamingHandlerState streaminghandlerstate) throws Exception {
    }

    protected void preHeaderReadingHook(StreamingHandlerState streaminghandlerstate) throws Exception {
    }

    protected void postHeaderReadingHook(StreamingHandlerState streaminghandlerstate) throws Exception {
    }

    protected void preBodyReadingHook(StreamingHandlerState streaminghandlerstate) throws Exception {
    }

    protected void postBodyReadingHook(StreamingHandlerState streaminghandlerstate) throws Exception {
    }

    protected void postEnvelopeReadingHook(StreamingHandlerState streaminghandlerstate) throws Exception {
    }

    protected void processingHook(StreamingHandlerState streaminghandlerstate) throws Exception {
    }

    protected void preResponseWritingHook(StreamingHandlerState streaminghandlerstate) throws Exception {
    }

    protected void postResponseWritingHook(StreamingHandlerState state) throws Exception {
        if(!state.getMessageContext().isFailure())
            callResponseHandlers(state);
    }

    protected void callRequestHandlers(StreamingHandlerState state) throws Exception {
        HandlerChain handlerChain = getHandlerChain();
        if(handlerChain != null)
            handlerChain.handleRequest(state.getMessageContext(), handlerChain);
    }

    protected void callResponseHandlers(StreamingHandlerState state) throws Exception {
        HandlerChain handlerChain = getHandlerChain();
        if(handlerChain != null)
            handlerChain.handleResponse(state.getMessageContext(), handlerChain);
    }

    protected HandlerChain getHandlerChain() {
        return null;
    }

    protected XMLReaderFactory getXMLReaderFactory() {
        return XMLReaderFactory.newInstance();
    }

    protected XMLWriterFactory getXMLWriterFactory() {
        return XMLWriterFactory.newInstance();
    }

    protected void reportFault(SOAPFaultInfo fault, StreamingHandlerState state) {
        if(state.getRequest().isHeaderNotUnderstood()) {
            return;
        } else {
            state.resetResponse();
            SOAPBlockInfo faultBlock = new SOAPBlockInfo(SOAPConstants.QNAME_SOAP_FAULT);
            faultBlock.setValue(fault);
            faultBlock.setSerializer(soapFaultInfoSerializer);
            state.getResponse().setBody(faultBlock);
            state.getResponse().setFailure(true);
            return;
        }
    }

}
