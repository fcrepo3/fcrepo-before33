package fedora.server.format;

import java.util.*;

import fedora.server.errors.ServerException;

/**
 * Information known about a format.
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class Format {

    private String m_label;
    private String m_identifier;
    private String m_xmlSchemaLocation;
    private String m_xmlNamespace;
    private String m_oaiPrefix;

    public Format(String label,
                  String identifier,
                  String xmlSchemaLocation,
                  String xmlNamespace,
                  String oaiPrefix) {
        m_label=label;
        m_identifier=identifier;
        m_xmlSchemaLocation=xmlSchemaLocation;
        m_xmlNamespace=xmlNamespace;
        m_oaiPrefix=oaiPrefix;
    }

    /**
     * Get the label.
     */
    public String getLabel() {
        return m_label;
    }

    /**
     * Get the identifier.
     */
    public String getIdentifier() {
        return m_identifier;
    }

    /**
     * Get a URL where the XML schema can be found.
     * If no such URL is known or appropriate for this format, return null.
     */
    public String getXMLSchemaLocation() {
        return m_xmlSchemaLocation;
    }

    /**
     * Get the XML namespace URI of the root element.
     * If no such URI is known or appropriate for this format, return null.
     */
    public String getXMLNamespace() {
        return m_xmlNamespace;
    }
 
    /**
     * Get the OAI "metadataPrefix" for this XML format.
     * If no such string is known or appropriate for this format, return null.
     */
    public String getOAIPrefix() {
        return m_oaiPrefix;
    }

}
