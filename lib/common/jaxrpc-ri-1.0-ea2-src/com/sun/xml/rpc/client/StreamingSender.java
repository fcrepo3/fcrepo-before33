// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StreamingSender.java

package com.sun.xml.rpc.client;

import com.sun.xml.messaging.util.ByteInputStream;
import com.sun.xml.messaging.util.ByteOutputStream;
import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.soap.message.*;
import com.sun.xml.rpc.soap.streaming.SOAPProtocolViolationException;
import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.util.xml.XmlUtil;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

// Referenced classes of package com.sun.xml.rpc.client:
//            StreamingSenderState, SenderException, ClientTransport

public abstract class StreamingSender {

    private static final SOAPFaultInfoSerializer faultInfoSerializer = new SOAPFaultInfoSerializer(true, false);
    private static final QName QNAME_SOAP_FAULT = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Fault");

    protected StreamingSender() {
    }

    protected StreamingSenderState _start(HandlerChain handlerChain) {
        SOAPMessageContext messageContext = new SOAPMessageContext();
        return new StreamingSenderState(messageContext, handlerChain);
    }

    protected String _getActor() {
        return null;
    }

    protected void _send(String endpoint, StreamingSenderState state) throws Exception {
        _preSendingHook(state);
        _preRequestWritingHook(state);
        _writeRequest(state);
        _postRequestWritingHook(state);
        _preRequestSendingHook(state);
        _getTransport().invoke(endpoint, state.getMessageContext());
        _postRequestSendingHook(state);
        XMLReader reader = null;
        try {
            SOAPFaultInfo fault = null;
            _preHandlingHook(state);
            java.io.InputStream istream = null;
            javax.xml.transform.Source source = state.getResponse().getMessage().getSOAPPart().getContent();
            if(source instanceof StreamSource) {
                istream = ((StreamSource)source).getInputStream();
            } else {
                Transformer transformer = XmlUtil.newTransformer();
                ByteOutputStream bos = new ByteOutputStream();
                transformer.transform(source, new StreamResult(bos));
                istream = new ByteInputStream(bos.getBytes(), bos.getCount());
            }
            reader = getXMLReaderFactory().createXMLReader(istream);
            _preEnvelopeReadingHook(state);
            reader.nextElementContent();
            SOAPDeserializationContext deserializationContext = new SOAPDeserializationContext();
            deserializationContext.setMessage(state.getResponse().getMessage());
            if(reader.getState() == 1 && "http://schemas.xmlsoap.org/soap/envelope/".equals(reader.getURI()) && "Envelope".equals(reader.getLocalName())) {
                boolean envelopePushedEncodingStyle = deserializationContext.processEncodingStyle(reader);
                _preHeaderReadingHook(state);
                if(state.isFailure())
                    return;
                reader.nextElementContent();
                if(reader.getState() == 1 && "http://schemas.xmlsoap.org/soap/envelope/".equals(reader.getURI())) {
                    if("Header".equals(reader.getLocalName())) {
                        boolean headerPushedEncodingStyle = deserializationContext.processEncodingStyle(reader);
                        _processHeaders(reader, deserializationContext, state);
                        _postHeaderReadingHook(state);
                        if(headerPushedEncodingStyle)
                            deserializationContext.popEncodingStyle();
                        reader.nextElementContent();
                    }
                    if(reader.getState() == 1 && "http://schemas.xmlsoap.org/soap/envelope/".equals(reader.getURI()) && "Body".equals(reader.getLocalName())) {
                        boolean bodyPushedEncodingStyle = deserializationContext.processEncodingStyle(reader);
                        Object faultOrFaultState = null;
                        if(reader.nextElementContent() == 2)
                            throw new SOAPProtocolViolationException("soap.protocol.emptyBody");
                        _preBodyReadingHook(state);
                        if(state.isFailure())
                            return;
                        if(reader.getName().equals(QNAME_SOAP_FAULT))
                            faultOrFaultState = _readBodyFaultElement(reader, deserializationContext, state);
                        else
                            _readFirstBodyElement(reader, deserializationContext, state);
                        if(state.isFailure())
                            return;
                        deserializationContext.deserializeMultiRefObjects(reader);
                        _postBodyReadingHook(state);
                        for(; reader.nextElementContent() == 1; reader.skipElement());
                        deserializationContext.doneDeserializing();
                        if(bodyPushedEncodingStyle)
                            deserializationContext.popEncodingStyle();
                        if(faultOrFaultState != null)
                            if(faultOrFaultState instanceof SOAPFaultInfo)
                                fault = (SOAPFaultInfo)faultOrFaultState;
                            else
                            if(faultOrFaultState instanceof SOAPDeserializationState)
                                fault = (SOAPFaultInfo)((SOAPDeserializationState)faultOrFaultState).getInstance();
                            else
                                throw new SenderException("sender.response.unrecognizedFault");
                    } else {
                        throw new SOAPProtocolViolationException("soap.protocol.missingBody");
                    }
                } else {
                    throw new SOAPProtocolViolationException("soap.protocol.invalidEnvelopeContent");
                }
                if(envelopePushedEncodingStyle)
                    deserializationContext.popEncodingStyle();
            } else {
                throw new SOAPProtocolViolationException("soap.protocol.notAnEnvelope");
            }
            if(state.isFailure())
                return;
            _postEnvelopeReadingHook(state);
            if(fault != null)
                _raiseFault(fault, state);
        }
        catch(Exception e) {
            throw e;
        }
        finally {
            if(reader != null)
                reader.close();
            try {
                _postHandlingHook(state);
            }
            finally {
                _postSendingHook(state);
            }
        }
    }

    protected void _sendOneWay(String endpoint, StreamingSenderState state) throws Exception {
        _preSendingHook(state);
        _preRequestWritingHook(state);
        _writeRequest(state);
        _postRequestWritingHook(state);
        _preRequestSendingHook(state);
        _getTransport().invokeOneWay(endpoint, state.getMessageContext());
        _postRequestSendingHook(state);
        _postSendingHook(state);
    }

    protected void _processHeaders(XMLReader reader, SOAPDeserializationContext deserializationContext, StreamingSenderState state) throws Exception {
        for(; reader.nextElementContent() != 2; _processHeaderElement(reader, deserializationContext, state));
    }

    protected void _processHeaderElement(XMLReader reader, SOAPDeserializationContext deserializationContext, StreamingSenderState state) throws Exception {
        Attributes attributes = reader.getAttributes();
        String actorAttr = attributes.getValue("http://schemas.xmlsoap.org/soap/envelope/", "actor");
        String mustUnderstandAttr = attributes.getValue("http://schemas.xmlsoap.org/soap/envelope/", "actor");
        boolean mustUnderstand = false;
        if(mustUnderstandAttr != null)
            if(mustUnderstandAttr.equals("1"))
                mustUnderstand = true;
            else
            if(!mustUnderstandAttr.equals("0"))
                throw new SenderException("sender.response.illegalValueOfMustUnderstandAttribute", mustUnderstandAttr);
        if(_getActor() == null && (actorAttr == null || actorAttr.equals("http://schema.xmlsoap.org/soap/actor/next")) || _getActor() != null && _getActor().equals(actorAttr)) {
            SOAPHeaderBlockInfo headerInfo = new SOAPHeaderBlockInfo(reader.getName(), actorAttr, mustUnderstand);
            boolean succeeded = _readHeaderElement(headerInfo, reader, deserializationContext, state);
            if(!succeeded && mustUnderstand)
                throw new SenderException("sender.response.headerNotUnderstood", headerInfo.getName().getLocalPart());
            else
                return;
        }
        if(mustUnderstand) {
            throw new SenderException("sender.response.headerNotUnderstood", reader.getLocalName());
        } else {
            reader.skipElement();
            return;
        }
    }

    protected boolean _readHeaderElement(SOAPHeaderBlockInfo headerInfo, XMLReader reader, SOAPDeserializationContext deserializationContext, StreamingSenderState state) throws Exception {
        reader.skipElement();
        return false;
    }

    protected Object _readBodyFaultElement(XMLReader reader, SOAPDeserializationContext deserializationContext, StreamingSenderState state) throws Exception {
        return faultInfoSerializer.deserialize(null, reader, deserializationContext);
    }

    protected void _readFirstBodyElement(XMLReader reader, SOAPDeserializationContext deserializationContext, StreamingSenderState state) throws Exception {
        reader.skipElement();
    }

    protected void _raiseFault(SOAPFaultInfo fault, StreamingSenderState state) throws Exception {
        if(fault.getDetail() != null && (fault.getDetail() instanceof Exception))
            throw (Exception)fault.getDetail();
        else
            throw new SenderException("sender.response.fault", new Object[] {
                fault.getString(), fault.getCode().getLocalPart(), fault.getCode().getNamespaceURI()
            });
    }

    protected void _writeRequest(StreamingSenderState state) throws Exception {
        SOAPBlockInfo bodyInfo = state.getRequest().getBody();
        boolean pushedEncodingStyle = false;
        if(bodyInfo == null || bodyInfo.getName() == null || bodyInfo.getSerializer() == null)
            throw new SenderException("sender.request.missingBodyInfo");
        ByteArrayOutputStream bufferedStream = new ByteArrayOutputStream();
        XMLWriter writer = getXMLWriterFactory().createXMLWriter(bufferedStream, _getPreferredCharacterEncoding());
        writer.setPrefixFactory(new PrefixFactoryImpl("ans"));
        SOAPSerializationContext serializationContext = new SOAPSerializationContext("ID");
        serializationContext.setMessage(state.getRequest().getMessage());
        writer.startElement("Envelope", "http://schemas.xmlsoap.org/soap/envelope/", "env");
        writer.writeNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        writer.writeNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        writer.writeNamespaceDeclaration("enc", "http://schemas.xmlsoap.org/soap/encoding/");
        String namespaceDeclarations[] = _getNamespaceDeclarations();
        if(namespaceDeclarations != null) {
            for(int i = 0; i < namespaceDeclarations.length; i += 2)
                writer.writeNamespaceDeclaration(namespaceDeclarations[i], namespaceDeclarations[i + 1]);

        }
        if(_getDefaultEnvelopeEncodingStyle() != null)
            pushedEncodingStyle = serializationContext.pushEncodingStyle(_getDefaultEnvelopeEncodingStyle(), writer);
        boolean wroteHeader = false;
        for(Iterator iter = state.getRequest().headers(); iter.hasNext();) {
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
        state.getRequest().getMessage().getSOAPPart().setContent(new StreamSource(new ByteInputStream(data, data.length)));
    }

    protected String[] _getNamespaceDeclarations() {
        return null;
    }

    protected String _getDefaultEnvelopeEncodingStyle() {
        return "http://schemas.xmlsoap.org/soap/encoding/";
    }

    protected String _getPreferredCharacterEncoding() {
        return "UTF-8";
    }

    protected void _preSendingHook(StreamingSenderState streamingsenderstate) throws Exception {
    }

    protected void _postSendingHook(StreamingSenderState streamingsenderstate) throws Exception {
    }

    protected void _preHandlingHook(StreamingSenderState state) throws Exception {
        callResponseHandlers(state);
    }

    protected void _postHandlingHook(StreamingSenderState streamingsenderstate) throws Exception {
    }

    protected void _preRequestWritingHook(StreamingSenderState streamingsenderstate) throws Exception {
    }

    protected void _postRequestWritingHook(StreamingSenderState streamingsenderstate) throws Exception {
    }

    protected void _preRequestSendingHook(StreamingSenderState state) throws Exception {
        callRequestHandlers(state);
    }

    protected void _postRequestSendingHook(StreamingSenderState streamingsenderstate) throws Exception {
    }

    protected void _preEnvelopeReadingHook(StreamingSenderState streamingsenderstate) throws Exception {
    }

    protected void _preHeaderReadingHook(StreamingSenderState streamingsenderstate) throws Exception {
    }

    protected void _postHeaderReadingHook(StreamingSenderState streamingsenderstate) throws Exception {
    }

    protected void _preBodyReadingHook(StreamingSenderState streamingsenderstate) throws Exception {
    }

    protected void _postBodyReadingHook(StreamingSenderState streamingsenderstate) throws Exception {
    }

    protected void _postEnvelopeReadingHook(StreamingSenderState streamingsenderstate) throws Exception {
    }

    protected void callRequestHandlers(StreamingSenderState state) throws Exception {
        HandlerChain handlerChain = state.getHandlerChain();
        if(handlerChain != null)
            handlerChain.handleRequest(state.getMessageContext(), handlerChain);
    }

    protected void callResponseHandlers(StreamingSenderState state) throws Exception {
        HandlerChain handlerChain = state.getHandlerChain();
        if(handlerChain != null)
            handlerChain.handleResponse(state.getMessageContext(), handlerChain);
    }

    protected abstract ClientTransport _getTransport();

    protected XMLReaderFactory getXMLReaderFactory() {
        return XMLReaderFactory.newInstance();
    }

    protected XMLWriterFactory getXMLWriterFactory() {
        return XMLWriterFactory.newInstance();
    }

}
