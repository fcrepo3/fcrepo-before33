package fedora.client.objecteditor.types;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * Turns xml for method defs into java object instances.
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
 */
class MethodDefinitionsDeserializer
        extends DefaultHandler {

    /** The deserialized MethodDefinition objects */
    private List m_result;

    // values gathered and built while parsing
    private String m_methodName;
    private String m_methodLabel;
    private List m_methodParms;
    private String m_parmName;
    private String m_parmLabel;
    private String m_parmRequired;
    private String m_parmDefaultValue;
    private List m_parmValidValues;

    public MethodDefinitionsDeserializer(InputStream xml) 
            throws IOException {
        m_result=new ArrayList();
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser parser = spf.newSAXParser();
            parser.parse(xml, this);
        } catch (Exception e) {
            if (e.getMessage()!=null) {
                throw new IOException(e.getMessage());
            } else {
                throw new IOException("Error parsing method definitions: "
                        + e.getClass().getName());
            }
        }
    }

    public List getResult() {
        return m_result;
    }

    public void startElement(String uri, 
                             String localName, 
                             String qName, 
                             Attributes a) {
        if (localName.equals("Method")) {
            m_methodName=null;
            m_methodLabel=null;
            m_methodParms=new ArrayList();
            for (int i=0; i<a.getLength(); i++) {
                String name=a.getLocalName(i);
                if (name.equals("operationName")) {
                    m_methodName=a.getValue(i);
                } else if (name.equals("label")) {
                    m_methodLabel=a.getValue(i);
                }
            }
        } else if (localName.equals("UserInputParm")) {
            m_parmName=null;
            m_parmLabel=null;
            m_parmRequired=null;
            m_parmDefaultValue=null;
            m_parmValidValues=new ArrayList();
            for (int i=0; i<a.getLength(); i++) {
                String name=a.getLocalName(i);
                if (name.equals("parmName")) {
                    m_parmName=a.getValue(i);
                } else if (name.equals("label")) {
                    m_parmLabel=a.getValue(i);
                } else if (name.equals("required")) {
                    m_parmRequired=a.getValue(i);
                } else if (name.equals("defaultValue")) {
                    m_parmDefaultValue=a.getValue(i);
                }
            } 
        } else if (localName.equals("ValidParm")) {
            for (int i=0; i<a.getLength(); i++) {
                String name=a.getLocalName(i);
                if (name.equals("value")) {
                    m_parmValidValues.add(a.getValue(i));
                }
            }
        }
    }

    public void endElement(String uri, 
                           String localName, 
                           String qName) 
            throws SAXException {
        if (localName.equals("UserInputParm")) {
            if (m_parmName==null || m_parmName.length()==0) {
                throw new SAXException("UserInputParm must have a name "
                        + "attribute with length>1.");
            }
            boolean parmRequired=false;
            if (m_parmRequired!=null) {
                if (m_parmRequired.equalsIgnoreCase("true")
                        || m_parmRequired.equalsIgnoreCase("yes")
                        || m_parmRequired.equals("1")) {
                    parmRequired=true;
                }
            }
            m_methodParms.add(new ParameterDefinition(m_parmName,
                                                      m_parmLabel,
                                                      parmRequired,
                                                      m_parmDefaultValue,
                                                      m_parmValidValues));
        } else if (localName.equals("Method")) {
            if (m_methodName==null || m_methodName.length()==0) {
                throw new SAXException("Method must have a name attribute "
                        + "with length>1.");
            }
            m_result.add(new MethodDefinition(m_methodName, 
                                              m_methodLabel,
                                              m_methodParms));
        }
    }
}
