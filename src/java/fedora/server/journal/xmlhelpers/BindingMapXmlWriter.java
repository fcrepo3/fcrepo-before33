/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2006 by 
 * The Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */

package fedora.server.journal.xmlhelpers;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import fedora.server.storage.types.DSBinding;
import fedora.server.storage.types.DSBindingMap;

/**
 * 
 * <p>
 * <b>Title:</b> BindingMapXmlWriter.java
 * </p>
 * <p>
 * <b>Description:</b> Write an entire <code>DSBindingMap</code> instance to
 * the Journal file.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class BindingMapXmlWriter extends AbstractXmlWriter {

    public void writeBindingMap(String key, DSBindingMap bindingMap,
            XMLEventWriter writer) throws XMLStreamException {
        putStartTag(writer, QNAME_TAG_ARGUMENT);
        putAttribute(writer, QNAME_ATTR_NAME, key);
        putAttribute(writer, QNAME_ATTR_TYPE, ARGUMENT_TYPE_BINDING_MAP);
        writeMap(bindingMap, writer);
        putEndTag(writer, QNAME_TAG_ARGUMENT);
    }

    private void writeMap(DSBindingMap bindingMap, XMLEventWriter writer)
            throws XMLStreamException {
        putStartTag(writer, QNAME_TAG_DS_BINDING_MAP);
        putAttributeIfNotNull(writer, QNAME_ATTR_DS_BIND_MAP_ID,
                bindingMap.dsBindMapID);
        putAttributeIfNotNull(writer, QNAME_ATTR_DS_BIND_MECHANISM_PID,
                bindingMap.dsBindMechanismPID);
        putAttributeIfNotNull(writer, QNAME_ATTR_DS_BIND_MAP_LABEL,
                bindingMap.dsBindMapLabel);
        putAttributeIfNotNull(writer, QNAME_ATTR_STATE, bindingMap.state);

        if (bindingMap.dsBindings != null) {
            for (int i = 0; i < bindingMap.dsBindings.length; i++) {
                writingBinding(bindingMap.dsBindings[i], writer);
            }
        }
        putEndTag(writer, QNAME_TAG_DS_BINDING_MAP);
    }

    private void writingBinding(DSBinding binding, XMLEventWriter writer)
            throws XMLStreamException {
        putStartTag(writer, QNAME_TAG_DS_BINDING);
        putAttributeIfNotNull(writer, QNAME_ATTR_BIND_KEY_NAME,
                binding.bindKeyName);
        putAttributeIfNotNull(writer, QNAME_ATTR_BIND_LABEL, binding.bindLabel);
        putAttributeIfNotNull(writer, QNAME_ATTR_DATASTREAM_ID,
                binding.datastreamID);
        putAttributeIfNotNull(writer, QNAME_ATTR_SEQ_NO, binding.seqNo);
        putEndTag(writer, QNAME_TAG_DS_BINDING);
    }

}
