package fedora.client.batch;

import fedora.client.APIMStubFactory;
import fedora.server.errors.GeneralException;
import fedora.server.errors.RepositoryConfigurationException;
import fedora.server.management.FedoraAPIM;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamManagedContent;
import fedora.server.storage.types.DatastreamReferencedContent;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.utilities.StreamUtility;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;


/**
 *
 * <p><b>Title:</b> BatchModifyParser.java</p>
 * <p><b>Description:</b> A class for parsing the special XML format in Batch
 * Modify.
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
 * @author rlw@virginia.edu
 * @version $Id $
 */
class BatchModifyParser extends DefaultHandler
{

  private String host;
  private int port;
  private String user;
  private String pass;
  private String objectPID;
  private String dsControlGroupType;
  private String datastreamID;
  private String dsState;

  private FedoraAPIM APIM;

  /** The namespaces we know we will encounter */
  private final static String BMFF = "http://www.fedora.info/batch/modify";

  /**
   * URI-to-namespace prefix mapping info from SAX2 startPrefixMapping events.
   */
  private HashMap nsPrefixMap;
  private HashMap m_prefixUris;

  private Datastream datastream;
  private DatastreamManagedContent dmc;
  private DatastreamReferencedContent drc;
  private DatastreamXMLMetadata dxm;
  private DatastreamManagedContent origdmc;
  private DatastreamReferencedContent origdrc;
  private DatastreamXMLMetadata origdxm;

  // Variables for keeping state during SAX parse.

  private StringBuffer m_dsXMLBuffer;
  private StringBuffer m_dsFirstElementBuffer;
  private ArrayList m_dsPrefixes;
  private int m_xmlDataLevel;
  private boolean m_inXMLMetadata;
  private boolean m_firstInlineXMLElement;
  private boolean isStateChanged;

  /**
   *   Constructor allows this class to initiate the parsing
   */
  public BatchModifyParser(String host, int port, String user, String pass, InputStream in)
    throws RepositoryConfigurationException, GeneralException
  {
    this.host = host;
    this.port = port;
    this.user = user;
    this.pass = pass;
    XMLReader xmlReader = null;

    try
    {
      SAXParserFactory saxfactory=SAXParserFactory.newInstance();
      saxfactory.setValidating(false);
      SAXParser parser=saxfactory.newSAXParser();
      xmlReader=parser.getXMLReader();
      xmlReader.setContentHandler(this);
      xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
      xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
      APIM = APIMStubFactory.getStub(host, port, user, pass);
    }
    catch (Exception e)
    {
      //e.printStackTrace();
      throw new RepositoryConfigurationException("Internal SAX error while "
          + "preparing for Batch Modify Input File parsing: "
          + e.getMessage());
    }
    try
    {
      xmlReader.parse(new InputSource(in));
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new GeneralException("Error parsing Batch Modify Input File" +
          e.getClass().getName() + ": " + e.getMessage());
    }
  }

  public void startDocument() throws SAXException
  {
    nsPrefixMap = new HashMap();
    m_prefixUris = new HashMap();
  }

  public void endDocument() throws SAXException
  {
    nsPrefixMap = null;
    m_prefixUris = null;
  }

  public void startPrefixMapping(String prefix, String uri) throws SAXException
  {
    // save a forward and backward hash of namespace prefix-to-uri
    // mapping ... for the entire object.
    m_prefixUris.put(prefix, uri);
    nsPrefixMap.put(uri, prefix);
    // if we're looking at inline metadata, be sure to save the prefix
    // so we know it's used in that datastream
    if (m_inXMLMetadata)
    {
      if (!m_dsPrefixes.contains(prefix))
      {
        if (!"".equals(prefix))
        {
          m_dsPrefixes.add(prefix);
        }
      }
    }
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
    if (m_inXMLMetadata)
    {
      // since this data is encoded straight back to xml,
      // we need to make sure special characters &, <, >, ", and '
      // are re-converted to the xml-acceptable equivalents.
      StreamUtility.enc(ch, start, length, m_dsXMLBuffer);
    }
  }

  public void startElement(String namespaceURI, String localName, String qName, Attributes attrs)
    throws SAXException
  {

    if (namespaceURI.equalsIgnoreCase(BMFF) && localName.equalsIgnoreCase("modifyDatastream"))
    {

      fedora.server.types.gen.Datastream ds = null;
      isStateChanged = false;

      // eventually use validating schema to enforce this
      if ( attrs.getValue("pid") != null && attrs.getValue("dsID") != null &&
           attrs.getValue("dsControlGroupType") != null &&
           !attrs.getValue("pid").equalsIgnoreCase("") &&
           !attrs.getValue("dsID").equalsIgnoreCase("") &&
           !attrs.getValue("dsControlGroupType").equalsIgnoreCase("") )
      {
        objectPID = attrs.getValue("pid");
        datastreamID = attrs.getValue("dsID");
        dsControlGroupType = attrs.getValue("dsControlGroupType");
      } else
      {
        throw new SAXException("modifyDatastream requires attributes for PID: "
                   + attrs.getValue("pid") + " datastreamID: "
                   + attrs.getValue("dsID") + " dsControlGroupType"
            + attrs.getValue("dsControlGroupType"));
      }

      try {
        ds = (fedora.server.types.gen.Datastream) APIM.getDatastream(objectPID, datastreamID, null);
      } catch (Exception e)
      {
        throw new SAXException("Error in retrieving original datastream via APIM");
      }

      if (dsControlGroupType.equalsIgnoreCase("X"))
      {
        dxm = new DatastreamXMLMetadata();
        dxm.DatastreamID = datastreamID;
        dxm.DSControlGrp = dsControlGroupType;
        if (attrs.getValue("dsLabel") != null && !attrs.getValue("dsLabel").equalsIgnoreCase(""))
        {
          dxm.DSLabel = attrs.getValue("dsLabel");
        } else
        {
          dxm.DSLabel = ds.getLabel();
        }
        if ( attrs.getValue("dsState") != null && !attrs.getValue("dsState").equalsIgnoreCase(""))
        {
          dxm.DSState = attrs.getValue("dsState");
          isStateChanged = true;
        } else
        {
          dsState = ds.getState();
        }
        if ( attrs.getValue("dsLocation") != null && !attrs.getValue("dsLocation").equalsIgnoreCase(""))
        {
          dxm.DSLocation = attrs.getValue("dsLocation");
        } else
        {
          dxm.DSLocation = ds.getLocation();
        }
        if ( attrs.getValue("dsMIME") != null && !attrs.getValue("dsMIME").equalsIgnoreCase(""))
        {
          dxm.DSMIME = attrs.getValue("dsMIME");
        } else
        {
          dxm.DSMIME = ds.getMIMEType();
        }

        if (attrs.getValue("dsSize") != null && !attrs.getValue("dsSize").equalsIgnoreCase(""))
        {
          dxm.DSSize = new Integer(attrs.getValue("dsSize")).intValue();
        } else
        {
          dxm.DSSize = ds.getSize();
        }
        if (attrs.getValue("harvestable") != null && !attrs.getValue("harvestable").equalsIgnoreCase(""))
        {
          dxm.isHarvestable = new Boolean(attrs.getValue("harvestable")).booleanValue();
        } else
        {
          dxm.isHarvestable = ds.isHarvestable();
        }
        System.out.println("modify XMLMetadata Datastream -- SUCCEEDED");
      } else if (dsControlGroupType.equalsIgnoreCase("E") ||
          dsControlGroupType.equalsIgnoreCase("R"))
      {
        drc = new DatastreamReferencedContent();
        objectPID = attrs.getValue("pid");
        drc.DatastreamID = attrs.getValue("dsID");
        drc.DSControlGrp = dsControlGroupType;
        if (attrs.getValue("dsLabel") != null && !attrs.getValue("dsLabel").equalsIgnoreCase(""))
        {
          drc.DSLabel = attrs.getValue("dsLabel");
        } else
        {
          drc.DSLabel = ds.getLabel();
        }
        if ( attrs.getValue("dsState") != null && !attrs.getValue("dsState").equalsIgnoreCase(""))
        {
          drc.DSState = attrs.getValue("dsState");
          isStateChanged = true;
        } else
        {
          dsState = ds.getState();
        }
        if ( attrs.getValue("dsLocation") != null && !attrs.getValue("dsLocation").equalsIgnoreCase(""))
        {
          drc.DSLocation = attrs.getValue("dsLocation");
        } else
        {
          drc.DSLocation = ds.getLocation();
        }
        if ( attrs.getValue("dsMIME") != null && !attrs.getValue("dsMIME").equalsIgnoreCase(""))
        {
          drc.DSMIME = attrs.getValue("dsMIME");
        } else
        {
          drc.DSMIME = ds.getMIMEType();
        }

        if (attrs.getValue("dsSize") != null && !attrs.getValue("dsSize").equalsIgnoreCase(""))
        {
          drc.DSSize = new Integer(attrs.getValue("dsSize")).intValue();
        } else
        {
          drc.DSSize = ds.getSize();
        }
        if (attrs.getValue("harvestable") != null && !attrs.getValue("harvestable").equalsIgnoreCase(""))
        {
          drc.isHarvestable = new Boolean(attrs.getValue("harvestable")).booleanValue();
        } else
        {
          drc.isHarvestable = ds.isHarvestable();
        }
        System.out.println("modify Referenced Content Datastream -- SUCCEEDED");
      } else if (dsControlGroupType.equalsIgnoreCase("M"))
      {
        dmc = new DatastreamManagedContent();
        objectPID = attrs.getValue("pid");
        dmc.DatastreamID = attrs.getValue("dsID");
        dmc.DSControlGrp = dsControlGroupType;
        if (attrs.getValue("dsLabel") != null && !attrs.getValue("dsLabel").equalsIgnoreCase(""))
        {
          dmc.DSLabel = attrs.getValue("dsLabel");
        } else
        {
          dmc.DSLabel = ds.getLabel();
        }
        if ( attrs.getValue("dsState") != null && !attrs.getValue("dsState").equalsIgnoreCase(""))
        {
          dmc.DSState = attrs.getValue("dsState");
          isStateChanged = true;
        } else
        {
          dsState = ds.getState();
        }
        if ( attrs.getValue("dsLocation") != null && !attrs.getValue("dsLocation").equalsIgnoreCase(""))
        {
          dmc.DSLocation = attrs.getValue("dsLocation");
        } else
        {
          dmc.DSLocation = ds.getLocation();
        }
        if ( attrs.getValue("dsMIME") != null && !attrs.getValue("dsMIME").equalsIgnoreCase(""))
        {
          dmc.DSMIME = attrs.getValue("dsMIME");
        } else
        {
          dmc.DSMIME = ds.getMIMEType();
        }

        if (attrs.getValue("dsSize") != null && !attrs.getValue("dsSize").equalsIgnoreCase(""))
        {
          dmc.DSSize = new Integer(attrs.getValue("dsSize")).intValue();
        } else
        {
          dmc.DSSize = ds.getSize();
        }
        if (attrs.getValue("harvestable") != null && !attrs.getValue("harvestable").equalsIgnoreCase(""))
        {
          dmc.isHarvestable = new Boolean(attrs.getValue("harvestable")).booleanValue();
        } else
        {
          dmc.isHarvestable = ds.isHarvestable();
        }
        System.out.println("modify Managed Content Datastream -- SUCCEEDED");
      } else
      {
        throw new SAXException("Invalid Datastream Control Group Type: "+dsControlGroupType);
      }

    } else if (localName.equalsIgnoreCase("xmlData"))
    {
      m_inXMLMetadata = true;
      m_dsXMLBuffer=new StringBuffer();
      m_dsFirstElementBuffer=new StringBuffer();
      m_dsPrefixes=new ArrayList();
      m_xmlDataLevel=0;
      m_inXMLMetadata=true;
      m_firstInlineXMLElement=true;

    } else
    {
      if (m_inXMLMetadata)
      {
        String prefix=(String) nsPrefixMap.get(namespaceURI);
        if (m_firstInlineXMLElement)
        {
          m_firstInlineXMLElement=false;
          m_dsFirstElementBuffer.append('<');
          if (prefix!=null) {
            if (!m_dsPrefixes.contains(prefix))
            {
              if (!"".equals(prefix))
              {
                m_dsPrefixes.add(prefix);
              }
            }
            m_dsFirstElementBuffer.append(prefix);
            m_dsFirstElementBuffer.append(':');
          }
          m_dsFirstElementBuffer.append(localName);
        } else
        {
          m_dsXMLBuffer.append('<');
          if (prefix!=null)
          {
            if (!m_dsPrefixes.contains(prefix))
            {
              if (!"".equals(prefix))
              {
                m_dsPrefixes.add(prefix);
              }
            }
            m_dsXMLBuffer.append(prefix);
            m_dsXMLBuffer.append(':');
          }
          m_dsXMLBuffer.append(localName);

          for (int i=0; i<attrs.getLength(); i++)
          {
            m_dsXMLBuffer.append(' ');
            String aPrefix=(String) nsPrefixMap.get(attrs.getURI(i));
            if (aPrefix!=null)
            {
              if (!m_dsPrefixes.contains(aPrefix))
              {
                if (!"".equals(aPrefix))
                {
                  m_dsPrefixes.add(aPrefix);
                }
              }
              m_dsXMLBuffer.append(aPrefix);
              m_dsXMLBuffer.append(':');
            }
            m_dsXMLBuffer.append(attrs.getLocalName(i));
            m_dsXMLBuffer.append("=\"");
            // re-encode decoded standard entities (&, <, >, ", ')
            m_dsXMLBuffer.append(StreamUtility.enc(attrs.getValue(i)));
            m_dsXMLBuffer.append("\"");
          }
        }
        m_dsXMLBuffer.append('>');
      }
    }
  }

  public void endElement(String namespaceURI, String localName, String qName) throws SAXException
  {
    if (m_inXMLMetadata)
    {
      if (localName.equals("xmlData"))
      {
        // finished all xml metadata for this datastream
        // create the right kind of datastream and add it to m_obj
        String[] prefixes=new String[m_dsPrefixes.size()];
        for (int i=0; i<m_dsPrefixes.size(); i++)
        {
          String pfx=(String) m_dsPrefixes.get(i);
          prefixes[i]=pfx;
          // now finish writing to m_dsFirstElementBuffer, a series of strings like
          // ' xmlns:PREFIX="URI"'
          String pfxUri=(String) m_prefixUris.get(pfx);
          m_dsFirstElementBuffer.append(" xmlns:");
          m_dsFirstElementBuffer.append(pfx);
          m_dsFirstElementBuffer.append("=\"");
          m_dsFirstElementBuffer.append(pfxUri);
          m_dsFirstElementBuffer.append("\"");
        }
        m_inXMLMetadata=false;
        // other stuff is re-initted upon
        // startElement for next xml metadata
        // element
        m_inXMLMetadata = false;
        m_inXMLMetadata=false;
        m_firstInlineXMLElement=false;
        String combined=m_dsFirstElementBuffer.toString() + m_dsXMLBuffer.toString();
        //datastream..dsXMLBuffer.append(combined);
        try
        {
          dxm.xmlContent = combined.getBytes("UTF-8");
        } catch (UnsupportedEncodingException uee)
        {}

      } else
      {
        // finished an element in xml metadata... print end tag,
        // subtracting the level of METS:xmlData elements we're at
        // if needed
        m_dsXMLBuffer.append("</");
        String prefix=(String) nsPrefixMap.get(namespaceURI);
        if (prefix!=null)
        {
          m_dsXMLBuffer.append(prefix);
          m_dsXMLBuffer.append(':');
        }
        m_dsXMLBuffer.append(localName);
        m_dsXMLBuffer.append(">");
      }
    }
    else if (namespaceURI.equalsIgnoreCase(BMFF) && localName.equalsIgnoreCase("modifyDatastream"))
    {
      try
      {
        if (dsControlGroupType.equalsIgnoreCase("X"))
        {
          APIM.modifyDatastreamByValue(objectPID, dxm.DatastreamID, dxm.DSLabel,
              "ModifyDatastreamByValue", dxm.xmlContent, dsState, dxm.isHarvestable);
          if(isStateChanged)
          {
            APIM.setDatastreamState(objectPID, datastreamID, dxm.DSState, "ModifiedState");
            System.out.println("ModifyState -- SUCCEEDED");
          }
        } else if (dsControlGroupType.equalsIgnoreCase("M"))
        {
          APIM.modifyDatastreamByReference(objectPID, dmc.DatastreamID, dmc.DSLabel,
              "ModifyDatastreamByReference", dmc.DSLocation, dsState, dmc.isHarvestable);
          if(isStateChanged)
          {
            APIM.setDatastreamState(objectPID, datastreamID, dmc.DSState, "ModifiedState");
            System.out.println("ModifyState -- SUCCEEDED");
          }
        } else if (dsControlGroupType.equalsIgnoreCase("E") ||
                   dsControlGroupType.equalsIgnoreCase("R"))
        {
          APIM.modifyDatastreamByReference(objectPID, drc.DatastreamID, drc.DSLabel,
              "ModifyDatastreamByReference", drc.DSLocation, dsState, drc.isHarvestable);
          if(isStateChanged)
          {
            APIM.setDatastreamState(objectPID, datastreamID, drc.DSState, "ModifiedState");
            System.out.println("ModifyState -- SUCCEEDED");
          }
        } else
        {
          throw new SAXException("ERROR: Invalid datastream Control Group Type "
              + "encounterd while parsing Batch Modify Input File: "
              + dsControlGroupType);
        }
        isStateChanged = false;
      } catch (Exception e)
      {
        e.printStackTrace();
        throw new SAXException("Error while communicating with API-M");
      }
    }
  }

  public static void main(String[] args)
  {
    String host = "localhost";
    int port = 8080;
    String user = "fedoraAdmin";
    String pass = "fedoraAdmin";
    try
    {
      FileInputStream file = new FileInputStream("c:\\batch3.xml");
      BatchModifyParser bmifp = new BatchModifyParser(host, port, user, pass, file);
      file.close();
    } catch (Exception e)
    {
      System.out.println("ERROR: "+e.getMessage());
      //e.printStackTrace();
    }
  }
}