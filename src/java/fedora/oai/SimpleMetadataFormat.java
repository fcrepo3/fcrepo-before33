package fedora.oai;

/**
 *
 * <p><b>Title:</b> SimpleMetadataFormat.java</p>
 * <p><b>Description:</b> A simple implementation of MetadataFormat that
 * provides getters on the values passed in the constructor.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class SimpleMetadataFormat
        implements MetadataFormat {

    private String m_prefix;
    private String m_schemaLocation;
    private String m_namespaceURI;

    public SimpleMetadataFormat(String prefix, String schemaLocation,
            String namespaceURI) {
        m_prefix=prefix;
        m_schemaLocation=schemaLocation;
        m_namespaceURI=namespaceURI;
    }

    public String getPrefix() {
        return m_prefix;
    }

    public String getSchemaLocation() {
        return m_schemaLocation;
    }

    public String getNamespaceURI() {
        return m_namespaceURI;
    }
}