package fedora.server.validation;

import fedora.server.errors.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * <p><b>Title:</b> DOIntegrityHandler.java</p>
 * <p><b>Description:</b> Description: Parses digital object to pick up data
 * elements and attributes that are necessary to perform Level 3 validation
 * (Integrity checks).</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version 1.0
 */
class DOIntegrityHandler extends DefaultHandler
{
  /** The namespace for METS */
  private final static String METS="http://www.loc.gov/METS/";

  /** The namespace for XLINK */
  private final static String XLINK="http://www.w3.org/TR/xlink";
  // Mets says the above, but the spec at http://www.w3.org/TR/xlink/
  // says it's http://www.w3.org/1999/xlink

  /**
   * URI-to-namespace prefix mapping info from SAX2 startPrefixMapping events.
   */
  private HashMap nsPrefixMap;

  /** The digital object integrity variables are those elements and
   *  attributes that have been parsed out of the digital object xml
   *  to be used as part of the referential integrity checks in
   *  Level 3 Validation.
   */
  protected DOIntegrityVariables iVars;

  // Variables for keeping state during SAX parse
  private boolean rootFound = false;
  private boolean inBehaviorSec = false;

  // Temporary variables during the SAX parse
  private Vector tmp_vBDefPIDs;
  private Vector tmp_vBMechPIDs;
  private Vector tmp_vDSURLs;

  public void startDocument() throws SAXException
  {
    nsPrefixMap = new HashMap();
    iVars = new DOIntegrityVariables();
    tmp_vBDefPIDs = new Vector();
    tmp_vBMechPIDs = new Vector();
    tmp_vDSURLs = new Vector();
  }

  public void endDocument() throws SAXException
  {
    iVars.bDefPIDs = (String[])tmp_vBDefPIDs.toArray(new String[0]);
    iVars.bMechPIDs = (String[])tmp_vBMechPIDs.toArray(new String[0]);
  }

  public void startPrefixMapping(String prefix, String uri) throws SAXException
  {
    nsPrefixMap.put(uri, prefix);
  }

  public void skippedEntity(String name) throws SAXException
  {
    StringBuffer sb = new StringBuffer();
    sb.append('&');
    sb.append(name);
    sb.append(';');
    char[] text = new char[sb.length()];
    sb.getChars(0, sb.length(), text, 0);
    this.characters(text, 0, text.length);
  }


  public void characters(char ch[], int start, int length)  throws SAXException
  {
  }

  public void startElement(String namespaceURI, String localName, String qName, Attributes attrs)
    throws SAXException
  {
    if (namespaceURI.equals(METS))
    {
      if (localName.equalsIgnoreCase("mets"))
      {
        rootFound = true;
      }
      else if (localName.equalsIgnoreCase("behaviorSec"))
      {
        inBehaviorSec = true;
      }
      else if (localName.equalsIgnoreCase("interfaceDef") && inBehaviorSec)
      {
        tmp_vBDefPIDs.add(attrs.getValue(XLINK, "href"));
      }
      else if (localName.equalsIgnoreCase("mechanism") && inBehaviorSec)
      {
        tmp_vBMechPIDs.add(attrs.getValue(XLINK, "href"));
      }
    }
  }

  public void endElement(String namespaceURI, String localName, String qName) throws SAXException
  {
    if (namespaceURI.equals(METS))
    {
      if (localName.equalsIgnoreCase("behaviorSec"))
      {
        inBehaviorSec = false;
      }
    }
  }
}