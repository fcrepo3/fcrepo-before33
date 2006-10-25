package fedora.server.journal.xmlhelpers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import fedora.server.journal.JournalException;
import fedora.server.storage.types.DSBinding;
import fedora.server.storage.types.DSBindingMap;

/**
 * 
 * <p>
 * <b>Title:</b> BindingMapXmlReader.java
 * </p>
 * <p>
 * <b>Description:</b> Reads an entire <code>DSBindingMap</code> from the
 * Journal file.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class BindingMapXmlReader extends AbstractXmlReader {

    public DSBindingMap readBindingMap(XMLEventReader reader)
            throws JournalException, XMLStreamException {
        DSBindingMap map = readBindingMapStartTag(reader);
        map.dsBindings = readBindingsUntilEndOfMap(reader);
        return map;
    }

    /**
     * The start tag of the binding map must contain the four required
     * attributes.
     */
    private DSBindingMap readBindingMapStartTag(XMLEventReader reader)
            throws XMLStreamException, JournalException {
        DSBindingMap map = new DSBindingMap();
        XMLEvent event = reader.nextTag();
        if (!isStartTagEvent(event, QNAME_TAG_DS_BINDING_MAP)) {
            throw getNotStartTagException(QNAME_TAG_DS_BINDING_MAP, event);
        }

        StartElement start = event.asStartElement();
        map.dsBindMapID = getRequiredAttributeValue(start,
                QNAME_ATTR_DS_BIND_MAP_ID);
        map.dsBindMechanismPID = getRequiredAttributeValue(start,
                QNAME_ATTR_DS_BIND_MECHANISM_PID);
        map.dsBindMapLabel = getRequiredAttributeValue(start,
                QNAME_ATTR_DS_BIND_MAP_LABEL);
        map.state = getRequiredAttributeValue(start, QNAME_ATTR_STATE);
        return map;
    }

    /**
     * Read binding tags until we reach the end of the bindings map.
     */
    private DSBinding[] readBindingsUntilEndOfMap(XMLEventReader reader)
            throws XMLStreamException, JournalException {
        List<DSBinding> bindings = new ArrayList<DSBinding>();
        while (true) {
            XMLEvent event = reader.nextTag();
            if (isStartTagEvent(event, QNAME_TAG_DS_BINDING)) {
                bindings.add(readBinding(reader, event.asStartElement()));
            } else if (isEndTagEvent(event, QNAME_TAG_DS_BINDING_MAP)) {
                break;
            } else {
                throw getNotNextMemberOrEndOfGroupException(
                        QNAME_TAG_DS_BINDING_MAP, QNAME_TAG_DS_BINDING, event);
            }
        }
        return (DSBinding[]) bindings.toArray(new DSBinding[bindings.size()]);
    }

    /**
     * A binding consists of a start tag with attributes, followed by an end
     * tag.
     */
    private DSBinding readBinding(XMLEventReader reader, StartElement start)
            throws JournalException, XMLStreamException {
        DSBinding binding = new DSBinding();
        binding.bindKeyName = getRequiredAttributeValue(start,
                QNAME_ATTR_BIND_KEY_NAME);
        binding.bindLabel = getRequiredAttributeValue(start,
                QNAME_ATTR_BIND_LABEL);
        binding.datastreamID = getRequiredAttributeValue(start,
                QNAME_ATTR_DATASTREAM_ID);
        binding.seqNo = getRequiredAttributeValue(start, QNAME_ATTR_SEQ_NO);
        XMLEvent endTag = reader.nextTag();
        if (!isEndTagEvent(endTag, QNAME_TAG_DS_BINDING)) {
            throw getNotEndTagException(QNAME_TAG_DS_BINDING, endTag);
        }
        return binding;
    }
}
