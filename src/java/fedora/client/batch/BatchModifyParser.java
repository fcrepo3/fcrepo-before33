package fedora.client.batch;

//import fedora.client.APIMStubFactory;
//import fedora.client.Administrator;
import fedora.client.Uploader;
import fedora.client.batch.types.Datastream;
import fedora.server.errors.GeneralException;
import fedora.server.errors.RepositoryConfigurationException;
import fedora.server.management.FedoraAPIM;
//import fedora.server.storage.types.DatastreamManagedContent;
//import fedora.server.storage.types.DatastreamReferencedContent;
//import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.utilities.StreamUtility;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
//import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.text.SimpleDateFormat;

//import javax.xml.parsers.FactoryConfigurationError;
//import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


/**
 *
 * <p><b>Title:</b> BatchModifyParser.java</p>
 * <p><b>Description:</b> A class for parsing the xml modify directives in the
 * Batch Modify input file. The parsing is configured to parse directives in the
 * file sequentially. Logs are written for each successful and failed directive
 * that is processed. Recoverable(non-fatal) errors are written to the log file
 * and processing continues. Catastrophic errors will cause parsing to halt and
 * set the count of failed directives to -1 indicating that parsing was halted
 * prior to the end of the file. In this case the logs will contain all directives
 * processed up to the point of failure.</p>
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
public class BatchModifyParser extends DefaultHandler
{

  /** Instance of Uploader */
  private static Uploader UPLOADER;

  /** Instance of FedoraAPIM */
  private static FedoraAPIM APIM;

  /** Log file print stream. */
  private static PrintStream out;

  /** The namespaces we know we will encounter */
  private final static String BMFF = "http://www.fedora.info/definitions/";

  /** Date formatter. */
  private static SimpleDateFormat s_formatter=
      new SimpleDateFormat("yyyy-MM-dd' at 'HH:mm:ss");

  /** Count of directives that succeeded. */
  private int succeededCount = 0;

  /** Count of directives that failed. */
  private int failedCount = 0;

  /**
   * URI-to-namespace prefix mapping info from SAX2 startPrefixMapping events.
   */
  private HashMap nsPrefixMap;
  private HashMap m_prefixUris;

  /** Variables for keeping state during SAX parse. */
  private StringBuffer m_dsXMLBuffer;
  private StringBuffer m_dsFirstElementBuffer;
  private ArrayList m_dsPrefixes;
  private int m_xmlDataLevel;
  private boolean m_inXMLMetadata;
  private boolean m_firstInlineXMLElement;
  private boolean addDatastream = false;
  private boolean modifyDatastream = false;
  private boolean purgeDatastream = false;
  private boolean setDatastreamState = false;
  private boolean setDatastreamHarvestable = false;
  //private boolean addDisseminator = false;
  //private boolean purgeDisseminator = false;
  //private boolean modifyDisseminator = false;
  //private boolean setDisseminatorState = false;
  private Datastream ds;

  /**
   * <p>Constructor allows this class to initiate the parsing.</p>
   *
   * @param UPLOADER - An instance of Uploader.
   * @param APIM - An instance of FedoraAPIM.
   * @param in - An input stream containing the xml to be parsed.
   * @param out - A print stream used for writing log info.
   * @throws RepositoryConfigurationException - If an error occurs in configuring
   *                                            the SAX parser.
   */
  public BatchModifyParser(Uploader UPLOADER, FedoraAPIM APIM, InputStream in, PrintStream out)
  {
    this.out = out;
    this.APIM = APIM;
    this.UPLOADER = UPLOADER;
    XMLReader xmlReader = null;

    // Configure the SAX parser.
    try
    {
      SAXParserFactory saxfactory=SAXParserFactory.newInstance();
      saxfactory.setValidating(true);
      SAXParser parser=saxfactory.newSAXParser();
      xmlReader=parser.getXMLReader();
      xmlReader.setContentHandler(this);
      xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
      xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
      xmlReader.setFeature("http://apache.org/xml/features/validation/schema", true);
      xmlReader.setErrorHandler(new BatchModifyXMLErrorHandler());
    }
    catch (Exception e)
    {
      // An Exception indicates a fatal error and parsing was halted. Set
      // failedCount to -1 to indicate to the calling class that parsing failed.
      // Throwing an Exception would make class variables for succeededCount
      // and failedCount unavailable.
      logParserError(e, null);
      failedCount = -1;
      //e.printStackTrace();
    }

    // Parse the file.
    try
    {
      xmlReader.parse(new InputSource(in));
    }
    catch (Exception e)
    {
      // An Exception indicates a fatal error and parsing was halted. Set
      // failedCount to -1 to indicate to the calling class that parsing failed.
      // Throwing an Exception would make class variables for succeededCount
      // and failedCount unavailable.
      logParserError(e, null);
      failedCount = -1;
      //e.printStackTrace();
    }
  }

  /**
   * <p>Get the count of failed directives. Note that a failed count value of
   * -1 indicates that a fatal parsing error occurred before all directives
   * could be parsed and the number of unprocessed directives is indeterminate.
   * The log file will contain details on how many directives were successfully
   * processed before the fatal error was encountered.</p>
   *
   * @return The count of failed directives.
   */
  public int getFailedCount()
  {
    return failedCount;
  }


  /**
   * <p>Get the count of successful directives.</p>
   *
   * @return The count of successful directives.
   */
  public int getSucceededCount()
  {
    return succeededCount;
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
    if (namespaceURI.equalsIgnoreCase(BMFF) && localName.equalsIgnoreCase("addDatastream"))
    {
      addDatastream = true;
      ds = new Datastream();

      ds.objectPID = attrs.getValue("pid");
      ds.DSControlGrp = attrs.getValue("dsControlGroupType");
      ds.DatastreamID = attrs.getValue("datstreamID");
      if (attrs.getValue("dsLabel") != null && !attrs.getValue("dsLabel").equalsIgnoreCase(""))
      {
        ds.DSLabel = attrs.getValue("dsLabel");
      }
      if ( attrs.getValue("dsState") != null && !attrs.getValue("dsState").equalsIgnoreCase(""))
      {
        ds.DSState = attrs.getValue("dsState");
      }
      if ( attrs.getValue("dsLocation") != null && !attrs.getValue("dsLocation").equalsIgnoreCase(""))
      {
        ds.DSLocation = attrs.getValue("dsLocation");
      }
      if ( attrs.getValue("dsMIME") != null && !attrs.getValue("dsMIME").equalsIgnoreCase(""))
      {
        ds.DSMIME = attrs.getValue("dsMIME");
      }

      if (attrs.getValue("dsMdClass") != null && !attrs.getValue("dsMdClass").equalsIgnoreCase(""))
      {
        ds.mdClass = attrs.getValue("dsMdClass");
      }
      if (attrs.getValue("dsMdType") != null && !attrs.getValue("dsMdType").equalsIgnoreCase(""))
      {
        ds.mdType = attrs.getValue("dsMdType");
      }
      if (attrs.getValue("harvestable") != null && !attrs.getValue("harvestable").equalsIgnoreCase(""))
      {
        ds.isHarvestable = new Boolean(attrs.getValue("harvestable")).booleanValue();
      }
      if (attrs.getValue("formatURI") != null && !attrs.getValue("formatURI").equalsIgnoreCase(""))
      {
        ds.DSFormatURI = attrs.getValue("formatURI");
      }
      } else if (namespaceURI.equalsIgnoreCase(BMFF) && localName.equalsIgnoreCase("purgeDatastream"))
      {
        ds = new Datastream();
        ds.objectPID = attrs.getValue("pid");
        ds.DatastreamID = attrs.getValue("dsID");
        purgeDatastream = true;
        try
        {
        if (attrs.getValue("asOfDate")!=null && !attrs.getValue("asOfDate").equalsIgnoreCase(""))
        {
          ds.asOfDate = fedora.server.utilities.DateUtility.convertStringToCalendar(attrs.getValue("asOfDate"));
        }
        } catch (Exception e)
        {
          failedCount++;
          purgeDatastream = false;
          logFailedDirective(ds.objectPID, localName, e, "");
        }
    } else if (namespaceURI.equalsIgnoreCase(BMFF) && localName.equalsIgnoreCase("modifyDatastream"))
    {

      fedora.server.types.gen.Datastream dsOrig = null;
      ds = new Datastream();

      ds.objectPID = attrs.getValue("pid");
      ds.DatastreamID = attrs.getValue("dsID");
      ds.DSControlGrp = attrs.getValue("dsControlGroupType");
      modifyDatastream = true;

      try {
        dsOrig = APIM.getDatastream(ds.objectPID, ds.DatastreamID, null);
      } catch (Exception e)
      {
        failedCount++;
        logFailedDirective(ds.objectPID, localName, e, "");
        modifyDatastream = false;
        throw new SAXException("Unable to get list of existing datastreams");
      }

      if (attrs.getValue("dsLabel") != null && !attrs.getValue("dsLabel").equalsIgnoreCase(""))
      {
        ds.DSLabel = attrs.getValue("dsLabel");
      } else
      {
        ds.DSLabel = dsOrig.getLabel();
      }
      if ( attrs.getValue("dsState") != null && !attrs.getValue("dsState").equalsIgnoreCase(""))
      {
        ds.DSState = attrs.getValue("dsState");
      } else
      {
        ds.DSState = dsOrig.getState();
      }
      if ( attrs.getValue("dsLocation") != null && !attrs.getValue("dsLocation").equalsIgnoreCase(""))
      {
        ds.DSLocation = attrs.getValue("dsLocation");
      } else
      {
        ds.DSLocation = dsOrig.getLocation();
      }
      if (attrs.getValue("harvestable") != null && !attrs.getValue("harvestable").equalsIgnoreCase(""))
      {
        ds.isHarvestable = new Boolean(attrs.getValue("harvestable")).booleanValue();
      } else
      {
        ds.isHarvestable = dsOrig.isHarvestable();
      }
    } else if (namespaceURI.equalsIgnoreCase(BMFF) && localName.equalsIgnoreCase("setDatastreamState"))
    {
      ds = new Datastream();
      ds.objectPID = attrs.getValue("pid");
      ds.DatastreamID = attrs.getValue("dsID");
      ds.DSState = attrs.getValue("dsState");

    } else if (namespaceURI.equalsIgnoreCase(BMFF) && localName.equalsIgnoreCase("setDatastreamHarvestable"))
    {
      ds = new Datastream();
      ds.objectPID = attrs.getValue("pid");
      ds.DatastreamID = attrs.getValue("dsID");
      ds.isHarvestable = new Boolean(attrs.getValue("harvestable")).booleanValue();

    } else if (namespaceURI.equalsIgnoreCase(BMFF) && localName.equalsIgnoreCase("xmlData"))
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
      if (namespaceURI.equalsIgnoreCase(BMFF) && localName.equals("xmlData"))
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
          if(ds!=null && combined !=null)
            ds.xmlContent = combined.getBytes("UTF-8");
        } catch (UnsupportedEncodingException uee)
        {
          uee.printStackTrace();
        }

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
    } else if (namespaceURI.equalsIgnoreCase(BMFF) && localName.equalsIgnoreCase("addDatastream"))
    {
      try
      {
        String datastreamID = null;
        if (ds.DSControlGrp.equalsIgnoreCase("X"))
        {
          InputStream xmlMetadata = new ByteArrayInputStream(ds.xmlContent);
          ds.DSLocation=UPLOADER.upload(xmlMetadata);
          datastreamID = APIM.addDatastream(ds.objectPID, ds.DSLabel, ds.DSMIME, ds.DSFormatURI,
              ds.DSLocation, ds.DSControlGrp, ds.mdClass, ds.mdType, ds.DSState,
              ds.isHarvestable);
        } else if (ds.DSControlGrp.equalsIgnoreCase("M"))
        {
          datastreamID = APIM.addDatastream(ds.objectPID, ds.DSLabel, ds.DSMIME, ds.DSFormatURI,
              ds.DSLocation, ds.DSControlGrp, ds.mdClass, ds.mdType, ds.DSState,
              ds.isHarvestable);
        } else if (ds.DSControlGrp.equalsIgnoreCase("E") ||
                   ds.DSControlGrp.equalsIgnoreCase("R"))
        {
          datastreamID = APIM.addDatastream(ds.objectPID, ds.DSLabel, ds.DSMIME, ds.DSFormatURI,
              ds.DSLocation, ds.DSControlGrp, ds.mdClass, ds.mdType, ds.DSState,
              ds.isHarvestable);
        }
        if (datastreamID!=null)
        {
            succeededCount++;
            logSucceededDirective(ds.objectPID, localName,"datastreamID: "+datastreamID+" added");
        } else
        {
          failedCount++;
          logFailedDirective(ds.objectPID, localName, null, "Unable to add datastream");
        }
      } catch (Exception e)
      {
        e.printStackTrace();
        if(!addDatastream)
        {
          failedCount++;
          logFailedDirective(ds.objectPID, localName, e, "");
        }
      }
    } else if (namespaceURI.equalsIgnoreCase(BMFF) && localName.equalsIgnoreCase("purgeDatastream"))
    {
      try
      {
        Calendar[] versionsPurged = null;
        versionsPurged = APIM.purgeDatastream(ds.objectPID, ds.DatastreamID, ds.asOfDate);
        if (versionsPurged.length > 0)
        {
          succeededCount++;
          logSucceededDirective(ds.objectPID, localName, "datastreamID: "+ds.DatastreamID+" Versions purged: "+versionsPurged.length);
        } else
        {
          failedCount++;
          logFailedDirective(ds.objectPID, localName,null, "Unable to purge datastream; verify datastream ID and/or asOfDate");
        }
      } catch (Exception e)
      {
        if(!purgeDatastream)
        {
          failedCount++;
          logFailedDirective(ds.objectPID, localName, e, "");
        }
      }
    } else if (namespaceURI.equalsIgnoreCase(BMFF) && localName.equalsIgnoreCase("modifyDatastream"))
    {
      try
      {
        if (ds.DSControlGrp.equalsIgnoreCase("X"))
        {
          APIM.modifyDatastreamByValue(ds.objectPID, ds.DatastreamID, ds.DSLabel,
              "ModifyDatastreamByValue", ds.xmlContent, ds.DSState, ds.isHarvestable);
        } else if (ds.DSControlGrp.equalsIgnoreCase("M"))
        {
          APIM.modifyDatastreamByReference(ds.objectPID, ds.DatastreamID, ds.DSLabel,
              "ModifyDatastreamByReference", ds.DSLocation, ds.DSState, ds.isHarvestable);
        } else if (ds.DSControlGrp.equalsIgnoreCase("E") ||
                   ds.DSControlGrp.equalsIgnoreCase("R"))
        {
          APIM.modifyDatastreamByReference(ds.objectPID, ds.DatastreamID, ds.DSLabel,
              "ModifyDatastreamByReference", ds.DSLocation, ds.DSState, ds.isHarvestable);
        }
        succeededCount++;
        logSucceededDirective(ds.objectPID, localName, "DatastreamID: "+ds.DatastreamID+" modified");
      } catch (Exception e)
      {
        if(!modifyDatastream)
        {
          failedCount++;
          logFailedDirective(ds.objectPID, localName, e, null);
        }
      }
    } else if (namespaceURI.equalsIgnoreCase(BMFF) && localName.equalsIgnoreCase("setDatastreamState"))
    {
      try
      {
        APIM.setDatastreamState(ds.objectPID, ds.DatastreamID, ds.DSState, "ModifyDatastreamByReference");
        succeededCount++;
        logSucceededDirective(ds.objectPID, localName, "datastream: "+ds.DatastreamID+" Set dsState: "+ds.DSState);
      } catch (Exception e)
      {
        if (!setDatastreamState)
        {
          failedCount++;
          logFailedDirective(ds.objectPID, localName, e, null);
        }
      }
    } else if (namespaceURI.equalsIgnoreCase(BMFF) && localName.equalsIgnoreCase("setDatastreamHarvestable"))
    {
      try
      {
        APIM.setDatastreamHarvestable(ds.objectPID, ds.DatastreamID, ds.isHarvestable, "ModifyDatastreamByReference");
        succeededCount++;
        logSucceededDirective(ds.objectPID, localName, "datastream: "+ds.DatastreamID+" Set isHarvestable: "+ds.isHarvestable);
      } catch (Exception e)
      {
        failedCount++;
        logFailedDirective(ds.objectPID, localName, e, null);
      }
    }
  }

  /**
   * <p>Write a log of what happened when a directive fails.<p>
   *
   * @param sourcePID - The PID of the object being processed.
   * @param directive - The name of the directive being processed.
   * @param e - The Exception that was thrown.
   * @param msg - A message providing additional info if no Exception was thrown.
   */
  private static void logFailedDirective(String sourcePID, String directive,
      Exception e, String msg)
  {
    out.println("  <failed directive=\"" + directive + "\" sourcePID=\"" + sourcePID + "\">");
    if (e!=null)
    {
      String message=e.getMessage();
      if (message==null)
        message=e.getClass().getName();
      out.println("    " + StreamUtility.enc(message));
    } else
    {
      out.println("    " + StreamUtility.enc(msg));
    }
    out.println("  </failed>");
  }

  /**
   * <p>Write a log of what happened when there is a parsing error.</p>
   *
   * @param e - The Exception that was thrown.
   * @param msg - A message indicating additional info if no Exception was thrown.
   */
  private static void logParserError( Exception e, String msg)
  {
    out.println("  <parserError>");
    if (e!=null)
    {
      String message=e.getMessage();
      if (message==null)
        message=e.getClass().getName();
      out.println("    " + StreamUtility.enc(message));
    } else
    {
      out.println("    " + StreamUtility.enc(msg));
    }
    out.println("  </parserError>");
  }

  /**
   * <p>Write a log when a directive is successfully processed.</p>
   *
   * @param sourcePID - The PID of the object processed.
   * @param directive - The name of the directive processed.
   * @param msg - A message.
   */
  private static void logSucceededDirective(String sourcePID, String directive, String msg) {
        out.println("  <succeeded directive=\"" + directive + "\" sourcePID=\"" + sourcePID + "\">");
        out.println("    " + StreamUtility.enc(msg));
        out.println("  </succeeded>");
    }

  public static void main(String[] args)
  {
    String host = "localhost";
    int port = 8080;
    String user = "fedoraAdmin";
    String pass = "fedoraAdmin";
    PrintStream logFile;
    FedoraAPIM APIM;

    try
    {
      UPLOADER=new Uploader(host, port, user, pass);
      logFile = new PrintStream(new FileOutputStream("C:\\batchModifyLog.txt"));
      APIM = fedora.client.APIMStubFactory.getStub(host, port, user, pass);
      InputStream file = new FileInputStream("c:\\fedora\\mellon\\dist\\client\\demo\\local-server-demos\\simple-image-demo\\batch3.xml");
      BatchModifyParser bmp = new BatchModifyParser(UPLOADER, APIM, file, logFile);
      file.close();
      logFile.close();
    } catch (Exception e)
    {
      System.out.println("ERROR: "+e.getClass().getName()
                + " - " + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
      e.printStackTrace();
    }
  }

}