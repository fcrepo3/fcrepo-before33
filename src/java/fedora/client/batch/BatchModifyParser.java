package fedora.client.batch;

import fedora.client.Uploader;
import fedora.client.batch.types.Datastream;
import fedora.client.batch.types.DigitalObject;
import fedora.server.management.FedoraAPIM;
import fedora.server.access.FedoraAPIA;
import fedora.client.batch.types.Disseminator;
import fedora.client.utility.ingest.AutoIngestor;
import fedora.server.types.gen.DatastreamBinding;
import fedora.server.types.gen.DatastreamBindingMap;
import fedora.server.utilities.StreamUtility;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


import org.apache.axis.types.NonNegativeInteger;

import fedora.server.types.gen.ComparisonOperator;
import fedora.server.types.gen.Condition;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ObjectFields;


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
 * at <a href="http:www.mozilla.org/MPL">http:www.mozilla.org/MPL/.</a></p>
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
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class BatchModifyParser extends DefaultHandler
{

    /** Instance of Uploader */
    private static Uploader UPLOADER;

    /** Instance of FedoraAPIM */
    private static FedoraAPIM APIM;

    private static FedoraAPIA APIA;

    /** Log file print stream. */
    private static PrintStream out;

    /** The namespaces we know we will encounter */
    private final static String FBM = "http://www.fedora.info/definitions/";

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
    private boolean m_inXMLMetadata;
    private boolean m_firstInlineXMLElement;
    private boolean addObject = false;
    private boolean modifyObject = false;
    private boolean purgeObject = false;
    private boolean addDatastream = false;
    private boolean modifyDatastream = false;
    private boolean purgeDatastream = false;
    private boolean setDatastreamState = false;
    private boolean addDisseminator = false;
    private boolean purgeDisseminator = false;
    private boolean modifyDisseminator = false;
    private boolean setDisseminatorState = false;
    private Datastream m_ds;
    private Disseminator m_diss;
    private DigitalObject m_obj;
    private DatastreamBindingMap m_dsBindingMap;
    private DatastreamBinding m_dsBinding;
    private DatastreamBinding[] m_origBinding;
    private HashMap m_dsBindings;

    /**
     * <p>Constructor allows this class to initiate the parsing.</p>
     *
     * @param UPLOADER - An instance of Uploader.
     * @param APIM - An instance of FedoraAPIM.
     * @param APIA - An instance of Fedora APIA.
     * @param in - An input stream containing the xml to be parsed.
     * @param out - A print stream used for writing log info.
     */
    public BatchModifyParser(Uploader UPLOADER, FedoraAPIM APIM, FedoraAPIA APIA, InputStream in, PrintStream out)
    {
        BatchModifyParser.out = out;
        BatchModifyParser.APIM = APIM;
        BatchModifyParser.APIA = APIA;
        BatchModifyParser.UPLOADER = UPLOADER;
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
            throws SAXException {

        if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("addObject")) {
            addObject = false;
            m_obj = new DigitalObject();

            // Get required attributes
            m_obj.pid = attrs.getValue("pid");
            m_obj.label = attrs.getValue("label");
            m_obj.cModel = attrs.getValue("contentModel");
            m_obj.logMessage = attrs.getValue("logMessage");

            try {
                if ( m_obj.label.equals("") ) {
                    failedCount++;
                    logFailedDirective(m_obj.pid, localName, null,
                        	"Object Label must be non-empty.");
                    return;
                }
                if ( !m_obj.pid.equals("") ) {
                    if (m_obj.pid.indexOf(":")<1) {
                        failedCount++;
                        logFailedDirective(m_obj.pid, localName, null,
                                "Custom PID should be of the form \"namespace:1234\"");
                        return;
                    }
                }
                addObject = true;

            } catch (Exception e) {
                failedCount++;
                logFailedDirective(m_obj.pid, localName, e, "");
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("modifyObject")) {
            modifyObject = false;
            m_obj = new DigitalObject();

            // Get required attributes
            m_obj.pid = attrs.getValue("pid");
            m_obj.logMessage = attrs.getValue("logMessage");            
            
            try {       
                if ( !m_obj.pid.equals("") ) {
                    if (m_obj.pid.indexOf(":")<1) {
                        failedCount++;
                        logFailedDirective(m_obj.pid, localName, null,
                                "Custom PID should be of the form \"namespace:1234\"");
                        return;
                    }
                }
                
                // Get optional attributes
                if ( attrs.getValue("label") != null && !attrs.getValue("label").equals(""))
                    m_obj.label = attrs.getValue("label");
                else {
                    m_obj.label = null;
                }
                if ( attrs.getValue("state") != null && !attrs.getValue("state").equals(""))
                    m_obj.state = attrs.getValue("state");
                else {
                    m_obj.state = null;
                }                
                modifyObject = true;

            } catch (Exception e) {
                failedCount++;
                logFailedDirective(m_obj.pid, localName, e, "");
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("purgeObject")) {
            purgeObject = false;
            m_obj = new DigitalObject();

            // Get required attributes
            m_obj.pid = attrs.getValue("pid");
            m_obj.logMessage = attrs.getValue("logMessage");            

            try {
                if ( !m_obj.pid.equals("") ) {
                    if (m_obj.pid.indexOf(":")<1) {
                        failedCount++;
                        logFailedDirective(m_obj.pid, localName, null,
                                "Custom PID should be of the form \"namespace:1234\"");
                        return;
                    }
                }
                
                // Get optional attributes
                if ( attrs.getValue("force") != null && !attrs.getValue("force").equals(""))
                    m_obj.force = new Boolean(attrs.getValue("force")).booleanValue();
                else {
                    m_obj.force = false;
                }                
                purgeObject = true;

            } catch (Exception e) {
                failedCount++;
                logFailedDirective(m_obj.pid, localName, e, "");
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("addDatastream")) {
            try {
                addDatastream = false;
                m_ds = new Datastream();

                // Get required attributes
                m_ds.objectPID = attrs.getValue("pid");
                m_ds.dsControlGrp = attrs.getValue("dsControlGroupType");
                m_ds.dsLabel = attrs.getValue("dsLabel");
                m_ds.dsState = attrs.getValue("dsState");
                m_ds.dsMIME = attrs.getValue("dsMIME");
                m_ds.logMessage = attrs.getValue("logMessage");                

                // Check for optional attributes
                if ( attrs.getValue("dsID") != null && !attrs.getValue("dsID").equals("")) {
                    m_ds.dsID = attrs.getValue("dsID");                
                } else {
                	m_ds.dsID = null;
                }
                if ( attrs.getValue("dsLocation") != null && !attrs.getValue("dsLocation").equals(""))
                    m_ds.dsLocation = attrs.getValue("dsLocation");
                if ( attrs.getValue("formatURI") != null && !attrs.getValue("formatURI").equals(""))
                    m_ds.formatURI = attrs.getValue("formatURI");
                if ( attrs.getValue("versionable") != null && !attrs.getValue("versionable").equals(""))
                    m_ds.versionable = new Boolean(attrs.getValue("versionable")).booleanValue();
                if ( attrs.getValue("altIDs") != null && !attrs.getValue("altIDs").equals(""))
                    m_ds.altIDs = attrs.getValue("altIDs").split(" ");             
                // Check that MIME type is text/xml if datastream is XMLMetadata datastream
                if (m_ds.dsControlGrp.equalsIgnoreCase("X") &&
                        !m_ds.dsMIME.equalsIgnoreCase("text/xml") ) {
                    failedCount++;
                    logFailedDirective(m_ds.objectPID, localName, null,
                            "Datastream dsMIME attribute must be \"text/xml\" when"
                            + " adding datastreams of type \"X\". dsMIME type is: "
                            + " \""+m_ds.dsMIME+"\".");
                    return;
                }

                addDatastream = true;                

            } catch (Exception e) {
                e.printStackTrace();
                failedCount++;
                logFailedDirective(m_ds.objectPID, localName, e, "");
                return;
            }            
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("purgeDatastream")) {

            try {
                purgeDatastream = false;
                m_ds = new Datastream();

                // Get required attributes
                m_ds.objectPID = attrs.getValue("pid");
                m_ds.dsID = attrs.getValue("dsID");
                m_ds.logMessage = attrs.getValue("logMessage");

                // Get optional attributes. If asOfDate attribute is missing
                // or empty its value is null and indicates that all versions
                // of the datastream are to be purged.
                if (attrs.getValue("asOfDate")!=null && !attrs.getValue("asOfDate").equals(""))
                    m_ds.asOfDate = attrs.getValue("asOfDate");
                if ( attrs.getValue("force") != null && !attrs.getValue("force").equals(""))
                    m_ds.force = new Boolean(attrs.getValue("force")).booleanValue();

                purgeDatastream = true;

            } catch (Exception e) {
                failedCount++;
                logFailedDirective(m_ds.objectPID, localName, e, "");
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("modifyDatastream")) {

            try {
                fedora.server.types.gen.Datastream dsOrig = null;
                modifyDatastream = false;

                // Get required attributes
                m_ds = new Datastream();
                m_ds.objectPID = attrs.getValue("pid");
                m_ds.dsID = attrs.getValue("dsID");
                m_ds.dsControlGrp = attrs.getValue("dsControlGroupType");
                m_ds.logMessage = attrs.getValue("logMessage");

                try {
                    dsOrig = APIM.getDatastream(m_ds.objectPID, m_ds.dsID, null);
                } catch (Exception e) {
                    failedCount++;
                    logFailedDirective(m_ds.objectPID, localName, null,
                            "Datastream ID: "+m_ds.dsID+" does not exist"
                            + " in the object: "+m_ds.objectPID+" .\n    Unable to modify"
                            + "datastream.");
                    return;
                }

                // Check that datastream control group type matches that of the
                // original datastream being modified. This would get caught
                // later by the server, but may as well detect this now and
                // flag as an error in directives file.
                if (dsOrig.getControlGroup().getValue().equalsIgnoreCase(m_ds.dsControlGrp)) {

                    // Check for optional atributes. Missing or empty optional
                    // attributes indicate that those fields remain unchanged so
                    // get values from original datastream.
                    if (attrs.getValue("dsLabel") != null && !attrs.getValue("dsLabel").equals("")) {
                        m_ds.dsLabel = attrs.getValue("dsLabel");
                    } else {
                        m_ds.dsLabel = dsOrig.getLabel();
                    }
                    if ( attrs.getValue("dsState") != null && !attrs.getValue("dsState").equals("")) {
                        m_ds.dsState = attrs.getValue("dsState");
                    } else {
                        m_ds.dsState = dsOrig.getState();
                    }
                    if ( attrs.getValue("dsLocation") != null && !attrs.getValue("dsLocation").equals("")) {
                        m_ds.dsLocation = attrs.getValue("dsLocation");
                    } else {
                        m_ds.dsLocation = dsOrig.getLocation();
                    }
                    if ( attrs.getValue("dsMIME") != null && !attrs.getValue("dsMIME").equals("")) {
                        m_ds.dsMIME = attrs.getValue("dsMIME");
                    } else {
                        m_ds.dsMIME = dsOrig.getMIMEType();
                    }
                    if ( attrs.getValue("force") != null && !attrs.getValue("force").equals("")) {
                        m_ds.force = new Boolean(attrs.getValue("force")).booleanValue();
                    } else {
                        m_ds.force = false;
                    }                    
                    if ( attrs.getValue("versionable") != null && !attrs.getValue("versionable").equals("")) {
                        m_ds.versionable = new Boolean(attrs.getValue("versionable")).booleanValue();
                    } else {
                        m_ds.versionable = dsOrig.isVersionable();
                    }     
                    if ( attrs.getValue("altIDs") != null && !attrs.getValue("altIDs").equals("")) {
                        m_ds.altIDs = attrs.getValue("altIDs").split(" ");
                    } else {
                        m_ds.altIDs = dsOrig.getAltIDs();
                    }
                    if ( attrs.getValue("formatURI") != null && !attrs.getValue("formatURI").equals("")) {
                        m_ds.formatURI = attrs.getValue("formatURI");
                    } else {
                        m_ds.formatURI = dsOrig.getFormatURI();
                    }                    

                    modifyDatastream = true;

                } else {
                    failedCount++;
                    logFailedDirective(m_ds.objectPID, localName, null,
                            " Datastream Control Group Type of: "
                            + m_ds.dsControlGrp
                            + " in directives file does not match control group"
                            + " type in original datastream: "
                            + dsOrig.getControlGroup().getValue());
                }
            } catch (Exception e) {
                failedCount++;
                logFailedDirective(m_ds.objectPID, localName, e, "");
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("setDatastreamState")) {

            try {
                m_ds = new Datastream();
                setDatastreamState = false;

                // Get require attributes
                m_ds.objectPID = attrs.getValue("pid");
                m_ds.dsID = attrs.getValue("dsID");
                m_ds.dsState = attrs.getValue("dsState");
                m_ds.logMessage = attrs.getValue("logMessage");
                setDatastreamState = true;

            } catch (Exception e) {
                failedCount++;
                logFailedDirective(m_ds.objectPID, localName, e, "");
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("xmlData")) {
            m_inXMLMetadata = true;
            m_dsXMLBuffer=new StringBuffer();
            m_dsFirstElementBuffer=new StringBuffer();
            m_dsPrefixes=new ArrayList();
            m_firstInlineXMLElement=true;
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("addDatastreamBinding")) {

            try {
                m_dsBindingMap = new DatastreamBindingMap();
                m_dsBinding = new DatastreamBinding();

                // Get require attributes
                m_dsBinding.setBindKeyName(attrs.getValue("dsBindKeyName"));
                m_dsBinding.setDatastreamID(attrs.getValue("dsID"));

                // Get optional attributes. Missing or empty attribute for
                // binding label indicates that a label is to be generated
                // based on the label for the datastream. Missing or empty
                // attribute for sequence number sets sequence number to
                // deault value of zero.
                if (attrs.getValue("dsBindLabel") != null && !attrs.getValue("dsBindLabel").equals("")) {
                    m_dsBinding.setBindLabel(attrs.getValue("dsBindLabel"));
                } else {
                    try {
                        fedora.server.types.gen.Datastream ds = new fedora.server.types.gen.Datastream();
                        ds = APIM.getDatastream(m_diss.parentPID, m_dsBinding.getDatastreamID(), null);
                        m_dsBinding.setBindLabel("Binding for "+ds.getLabel());
                    } catch (Exception e) {
                        failedCount++;
                        if (addDisseminator) {
                            addDisseminator = false;
                        		logFailedDirective(m_diss.parentPID, "addDisseminator", null,
                        		        "Datastream ID: "+m_dsBinding.getDatastreamID()
                        		        + " does not exist in the object: "+m_diss.parentPID
                        		        + " .\n    Unable to add Datastream Binding for this datastream");
                        }
                        if (modifyDisseminator) {
                            modifyDisseminator = false;
                        		logFailedDirective(m_diss.parentPID, "modifyDisseminator", null,
                        		        "Datastream ID: "+m_dsBinding.getDatastreamID()
                        		        + " does not exist in the object: "+m_diss.parentPID
                        		        + " .\n    Unable to add Datastream Binding for this datastream");
                        }
                        return;
                    }
                }
                if (attrs.getValue("seqNo") != null && !attrs.getValue("seqNo").equals("")) {
                    m_dsBinding.setSeqNo(attrs.getValue("seqNo"));
                } else {
                    m_dsBinding.setSeqNo("0");
                }

                if(addDisseminator) {
                    
                    // If adding new disseminator, just go ahead and add binding
                    m_dsBindings.put(m_dsBinding.getDatastreamID(), m_dsBinding);
                    
                } else {

                    // If modifying disseminator, check that specified binding key name matches that in original disseminator
                    boolean bindKeyExists = false;
                    for (int i=0; i<m_origBinding.length; i++) {
                        if (m_origBinding[i].getBindKeyName().equalsIgnoreCase(m_dsBinding.getBindKeyName())) {
                            bindKeyExists = true;
                        }
                    }
                    
                    // Add datastream binding to hash of bindings
                    if (bindKeyExists) {
                        m_dsBindings.put(m_dsBinding.getDatastreamID(), m_dsBinding);
                    } else {
                        failedCount++;
                        modifyDisseminator = false;
                        logFailedDirective(m_diss.parentPID, "modifyDisseminator",
                                null, "Specified datastream Binding Key Name: "
                                + m_dsBinding.getBindKeyName()
                                +" does not exist in disseminator: "+m_diss.dissID
                                +"/n    Unable to add datastream binding for datastream: "
                                +m_dsBinding.getDatastreamID()+" .");
                    }                    
                }

            } catch (Exception e) {
                failedCount++;
                if(addDisseminator) {
                    addDisseminator = false;
                    logFailedDirective(m_diss.parentPID, "addDisseminator", e, "");
                }
                if(modifyDisseminator) {
                    modifyDisseminator = false;
                    logFailedDirective(m_diss.parentPID, "modifyDisseminator", e, "");
                }
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("removeDatastreamBinding")) {

            try {
                m_dsBindingMap = new DatastreamBindingMap();
                m_dsBinding = new DatastreamBinding();

                // Get required attributes
                m_dsBinding.setDatastreamID(attrs.getValue("dsID"));

                // Remove datastream binding if it exists; error otherwise
                if(m_dsBindings.containsKey(m_dsBinding.getDatastreamID())) {
                    m_dsBindings.remove(m_dsBinding.getDatastreamID());
                } else {
                    failedCount++;
                    modifyDisseminator = false;
                    logFailedDirective(m_diss.parentPID, "modifyDisseminator", null, "No binding found "
                            + "for datastreamID: "+m_dsBinding.getDatastreamID()
                            +" .\n    Datastream "
                            + "binding left unchanged.");
                }
            } catch (Exception e) {
                failedCount++;
                modifyDisseminator = false;
                logFailedDirective(m_diss.parentPID, "modifyDisseminator", e, "");
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("addDisseminator")) {

            try {
                addDisseminator = false;
                m_dsBindings = new HashMap();
                m_diss = new Disseminator();

                // Get required attributes
                m_diss.parentPID = attrs.getValue("pid");
                m_diss.bDefID = attrs.getValue("bDefPid");
                m_diss.bMechID = attrs.getValue("bMechPid");
                m_diss.dissLabel = attrs.getValue("dissLabel");
                m_diss.logMessage = attrs.getValue("logMessage");

                // Get original labels for bDef and bMech object for this disseminator
                Map m_bDefLabels = new HashMap();
                Map m_bMechLabels = new HashMap();
                m_bDefLabels = getBDefLabelMap();
                m_bMechLabels = getBMechLabelMap(m_diss.bDefID);

                // Get optional attrributes. Missing or empty attributes indicate
                // that default values are to be used.
                if ( attrs.getValue("dissState") != null && !attrs.getValue("dissState").equals("")) {
                    m_diss.dissState = attrs.getValue("dissState");
                } else {
                    m_diss.dissState = "A";
                }
                if ( attrs.getValue("bDefLabel") != null && !attrs.getValue("bDefLabel").equals("")) {
                    m_diss.bDefLabel = attrs.getValue("bDefLabel");
                } else {
                    m_diss.bDefLabel =  (String) m_bDefLabels.get(m_diss.bDefID);
                }
                if ( attrs.getValue("bMechLabel") != null && !attrs.getValue("bMechLabel").equals("")) {
                    m_diss.bMechLabel = attrs.getValue("bMechLabel");
                } else {
                    m_diss.bMechLabel =  (String) m_bMechLabels.get(m_diss.bMechID);
                }

                addDisseminator = true;

            } catch (Exception e) {
                failedCount++;
                logFailedDirective(m_diss.parentPID, localName, e, "");
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("modifyDisseminator")) {

            try {
                modifyDisseminator = false;
                m_dsBindings = new HashMap();
                m_diss = new Disseminator();
                fedora.server.types.gen.Disseminator origDiss = new fedora.server.types.gen.Disseminator();

                // Get require attributes
                m_diss.parentPID = attrs.getValue("pid");
                m_diss.bMechID = attrs.getValue("bMechPid");
                m_diss.dissID = attrs.getValue("dissID");    
                m_diss.logMessage = attrs.getValue("logMessage");

                try {
                    origDiss = APIM.getDisseminator(m_diss.parentPID, m_diss.dissID, null);
                } catch (Exception e) {
                    failedCount++;
                    logFailedDirective(m_diss.parentPID, localName, null,
                            "Disseminator ID: "+m_diss.dissID+" does not exist in"
                            + " the object: "+m_diss.parentPID+" .\n    Unable to modify"
                            + " this disseminator.");
                    return;

                }
                m_origBinding = new DatastreamBinding[origDiss.getDsBindMap().getDsBindings().length];
                m_origBinding = origDiss.getDsBindMap().getDsBindings();

                // Add any existing dsBindings for this disseminator to hash of bindings
                for (int i=0; i<m_origBinding.length; i++) {
                    m_dsBindings.put(m_origBinding[i].getDatastreamID(),m_origBinding[i]);
                }

                // Get optional attributes. Missing or empty attributes indicate
                // that these values are to remain unchanged so retrieve original
                // values from disseminator.
                if (attrs.getValue("dissLabel") != null && !attrs.getValue("dissLabel").equals("")) {
                    m_diss.dissLabel = attrs.getValue("dissLabel");
                } else {
                    m_diss.dissLabel = origDiss.getLabel();
                }
                if ( attrs.getValue("dissState") != null && !attrs.getValue("dissState").equals("")) {
                    m_diss.dissState = attrs.getValue("dissState");
                } else {
                    m_diss.dissState = origDiss.getState();
                }
                if ( attrs.getValue("bDefLabel") != null && !attrs.getValue("bDefLabel").equals("")) {
                    m_diss.bDefLabel = attrs.getValue("bDefLabel");
                } else {
                    m_diss.bDefLabel =  origDiss.getBDefLabel();
                }
                if ( attrs.getValue("bMechLabel") != null && !attrs.getValue("bMechLabel").equals("")) {
                    m_diss.bMechLabel = attrs.getValue("bMechLabel");
                } else {
                    m_diss.bMechLabel =  origDiss.getBMechLabel();
                }
                if ( attrs.getValue("force") != null && !attrs.getValue("force").equals("")) {
                    m_diss.force = new Boolean(attrs.getValue("force")).booleanValue();
                } else {
                    m_diss.force = false;
                }                              

                modifyDisseminator = true;

            } catch (Exception e) {
                failedCount++;
                logFailedDirective(m_diss.parentPID, localName, e, "");
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("purgeDisseminator")) {

            try {
                purgeDisseminator = false;
                m_diss = new Disseminator();
                m_diss.logMessage = attrs.getValue("logMessage");

                // Get require attributes
                m_diss.parentPID = attrs.getValue("pid");
                m_diss.dissID = attrs.getValue("dissID");

                // Get optional attributes. If asOfDate attribute ismissing
                // or empty its value will be null indicates that all versions
                // of the disseminator are to be removed.
                if (attrs.getValue("asOfDate")!=null && !attrs.getValue("asOfDate").equals(""))
                    m_diss.asOfDate = attrs.getValue("asOfDate");
                if ( attrs.getValue("force") != null && !attrs.getValue("force").equals("")) {
                    m_diss.force = new Boolean(attrs.getValue("force")).booleanValue();
                } else {
                    m_diss.force = false;
                }     

                purgeDisseminator = true;

            } catch (Exception e) {
                failedCount++;
                logFailedDirective(m_diss.parentPID, localName, e, "");
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("setDisseminatorState")) {

            try {
                setDisseminatorState = false;
                m_diss = new Disseminator();

                // Get required attributes
                m_diss.parentPID = attrs.getValue("pid");
                m_diss.dissID = attrs.getValue("dissID");
                m_diss.dissState = attrs.getValue("dissState");
                m_diss.logMessage = attrs.getValue("logMessage");
                setDisseminatorState = true;

            } catch (Exception e) {
                failedCount++;
                logFailedDirective(m_diss.parentPID, localName, e, "");
            }
        } else {
            if (m_inXMLMetadata) {
                String prefix=(String) nsPrefixMap.get(namespaceURI);
                if (m_firstInlineXMLElement) {
                    m_firstInlineXMLElement=false;
                    m_dsFirstElementBuffer.append('<');
                    if (prefix!=null) {
                        if (!m_dsPrefixes.contains(prefix)) {
                            if (!"".equals(prefix)) {
                                m_dsPrefixes.add(prefix);
                            }
                        }
                        m_dsFirstElementBuffer.append(prefix);
                        m_dsFirstElementBuffer.append(':');
                    }
                    m_dsFirstElementBuffer.append(localName);
                } else {
                    m_dsXMLBuffer.append('<');
                    if (prefix!=null) {
                        if (!m_dsPrefixes.contains(prefix)) {
                            if (!"".equals(prefix)) {
                                m_dsPrefixes.add(prefix);
                            }
                        }
                        m_dsXMLBuffer.append(prefix);
                        m_dsXMLBuffer.append(':');
                    }
                    m_dsXMLBuffer.append(localName);

                    for (int i=0; i<attrs.getLength(); i++) {
                        m_dsXMLBuffer.append(' ');
                        String aPrefix=(String) nsPrefixMap.get(attrs.getURI(i));
                        if (aPrefix!=null) {
                            if (!m_dsPrefixes.contains(aPrefix)) {
                                if (!"".equals(aPrefix)) {
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


    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

        if (m_inXMLMetadata) {
            if (namespaceURI.equalsIgnoreCase(FBM) && localName.equals("xmlData")) {
                // finished all xml metadata for this datastream
                // create the right kind of datastream and add it to m_obj
                String[] prefixes=new String[m_dsPrefixes.size()];
                for (int i=0; i<m_dsPrefixes.size(); i++) {
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
                try {
                    if(m_ds!=null && combined !=null)
                        m_ds.xmlContent = combined.getBytes("UTF-8");
                } catch (UnsupportedEncodingException uee) {
                    // ignore
                }

            } else {
                // finished an element in xml metadata... print end tag,
                // subtracting the level of METS:xmlData elements we're at
                // if needed
                m_dsXMLBuffer.append("</");
                String prefix=(String) nsPrefixMap.get(namespaceURI);
                if (prefix!=null) {
                    m_dsXMLBuffer.append(prefix);
                    m_dsXMLBuffer.append(':');
                }
                m_dsXMLBuffer.append(localName);
                m_dsXMLBuffer.append(">");
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("addObject")) {

            try {

                if(addObject) {
				    				StringBuffer xml=new StringBuffer();
				    				xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				    				xml.append("<foxml:digitalObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
				    				xml.append("           xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\"\n");
				    				xml.append("           xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-0.xsd\"\n");
				    				if (!m_obj.pid.equals("")) {
				    				    xml.append("           PID=\"" + StreamUtility.enc(m_obj.pid) + "\">\n");
				    				} else {
				    				    xml.append(">\n");
				    				}
				    				xml.append("  <foxml:objectProperties>\n");
				    				xml.append("    <foxml:property NAME=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\" VALUE=\"DataObject\"/>\n");
				    				xml.append("    <foxml:property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\"" + StreamUtility.enc(m_obj.label) + "\"/>\n");
				    				xml.append("    <foxml:property NAME=\"info:fedora/fedora-system:def/model#contentModel\" VALUE=\"" + StreamUtility.enc(m_obj.cModel) + "\"/>\n");			
				    				xml.append("  </foxml:objectProperties>\n");
				    				xml.append("</foxml:digitalObject>");
				    				String objXML=xml.toString();
                	
                    ByteArrayInputStream in=new ByteArrayInputStream(
                            objXML.getBytes("UTF-8"));
                    String newPID=AutoIngestor.ingestAndCommit(
                            APIA,
                            APIM,
                            in,
                            "foxml1.0",
                            "Created with BatchModify Utility \"addObject\" directive");
                    succeededCount++;
                    logSucceededDirective(newPID, localName,
                            " Added new object with PID: "+newPID);
                }
            } catch (Exception e) {
                if (addObject) {
                    failedCount++;
                    logFailedDirective(m_obj.pid, localName, e, "");
                }
            } finally {
                addObject = false;
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("modifyObject")) {

            try {

                // Process modifyObject only if no previous errors encountered
                if(modifyObject) {
                        APIM.modifyObject(m_obj.pid, m_obj.state, m_obj.label,
                                "ModifyObject");
                }
                succeededCount++;
                logSucceededDirective(m_obj.pid, localName,
                        "Object PID: " + m_obj.pid + " modified");

            } catch (Exception e) {
                if(modifyObject) {
                    failedCount++;
                    logFailedDirective(m_obj.pid, localName, e, null);
                }
            } finally {
                modifyObject = false;
            }    
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("purgeObject")) {

            try {

                // Process purgeDatastream only if no previous errors encountered
                if (purgeObject) {
                    String purgedPid = null;
                    purgedPid = APIM.purgeObject(m_obj.pid, "PurgeObject", m_obj.force);
                    if (purgedPid != null) {
                        succeededCount++;
                        logSucceededDirective(m_obj.pid, localName,
                                "Purged PID: " + m_obj.pid);
                    } else {
                        failedCount++;
                        logFailedDirective(m_ds.objectPID, localName, null,
                                "Unable to purge object with PID: " + m_obj.pid);
                    }
                }
            } catch (Exception e) {
                if(purgeObject) {
                    failedCount++;
                    logFailedDirective(m_obj.pid, localName, e, "");
      
                }
            } finally {
                purgeObject = false;
            }            
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("addDatastream")) {
            try {

                // Process addDatastream only if no previous errors encountered
                if(addDatastream) {
                    String datastreamID = null;
                    if (m_ds.dsControlGrp.equalsIgnoreCase("X")) {
                        InputStream xmlMetadata = new ByteArrayInputStream(m_ds.xmlContent);
                        m_ds.dsLocation=UPLOADER.upload(xmlMetadata);
                        datastreamID = APIM.addDatastream(m_ds.objectPID, 
                                m_ds.dsID, 
                                m_ds.altIDs,
                                m_ds.dsLabel, 
                                m_ds.versionable,
                                m_ds.dsMIME, 
                                m_ds.formatURI,
                                m_ds.dsLocation, 
                                m_ds.dsControlGrp, 
                                m_ds.dsState, 
                                m_ds.logMessage);
                    } else if (m_ds.dsControlGrp.equalsIgnoreCase("E") ||
                            	 m_ds.dsControlGrp.equalsIgnoreCase("M") ||
                            	 m_ds.dsControlGrp.equalsIgnoreCase("R")) {
                        datastreamID = APIM.addDatastream(m_ds.objectPID, 
                                m_ds.dsID, 
                                m_ds.altIDs,
                                m_ds.dsLabel,
                                m_ds.versionable,
                                m_ds.dsMIME, 
                                m_ds.formatURI,
                                m_ds.dsLocation, 
                                m_ds.dsControlGrp, 
                                m_ds.dsState, 
                                m_ds.logMessage);
                    }
                    if (datastreamID!=null) {
                        succeededCount++;
                        logSucceededDirective(m_ds.objectPID, localName,
                                "datastreamID: " + datastreamID + " added");
                    } else {
                        failedCount++;
                        logFailedDirective(m_ds.objectPID, localName, null,
                        "Unable to add datastream");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if(addDatastream) {
                    failedCount++;
                    logFailedDirective(m_ds.objectPID, localName, e, "");
                }
            } finally {
                addDatastream = false;
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("purgeDatastream")) {

            try {

                // Process purgeDatastream only if no previous errors encountered
                if (purgeDatastream) {
                    String[] versionsPurged = null;
                    versionsPurged = APIM.purgeDatastream(m_ds.objectPID,
                        m_ds.dsID, m_ds.asOfDate, m_ds.logMessage, m_ds.force);
                    if (versionsPurged.length > 0) {
                        succeededCount++;
                        if (m_ds.asOfDate!=null) {
                            logSucceededDirective(m_ds.objectPID,
                                localName,
                                "datastreamID: " + m_ds.dsID
                                    + "\n    Purged all versions prior to: "
                                    + m_ds.asOfDate
                                    + "\n    Versions purged: "+versionsPurged.length);
                        } else {
                            logSucceededDirective(m_ds.objectPID,
                                localName,
                                "datastreamID: " + m_ds.dsID
                                    + "\n    Purged all versions. "
                                    + "\n    Versions purged: "+versionsPurged.length);
                        }
                    } else {
                        failedCount++;
                        logFailedDirective(m_ds.objectPID, localName, null,
                            "Unable to purge datastream; verify datastream ID and/or asOfDate");
                    }
                }
            } catch (Exception e) {
                if(purgeDatastream) {
                    failedCount++;
                    logFailedDirective(m_ds.objectPID, localName, e, "");
                }
            } finally {
                purgeDatastream = false;
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("modifyDatastream")) {

            try {

                // Process modifyDatastream only if no previous errors encountered
                if(modifyDatastream) {
                    if (m_ds.dsControlGrp.equalsIgnoreCase("X")) {
                        APIM.modifyDatastreamByValue(m_ds.objectPID, m_ds.dsID, 
                                m_ds.altIDs, m_ds.dsLabel,
                                m_ds.versionable,
                                m_ds.dsMIME,
                                m_ds.formatURI,
                                m_ds.xmlContent, 
                                m_ds.dsState,
                                m_ds.logMessage, 
                                m_ds.force);
                    } else if (m_ds.dsControlGrp.equalsIgnoreCase("E") ||
                               m_ds.dsControlGrp.equalsIgnoreCase("M") ||
                               m_ds.dsControlGrp.equalsIgnoreCase("R") ) {
                        APIM.modifyDatastreamByReference(m_ds.objectPID, m_ds.dsID, 
                                m_ds.altIDs, 
                                m_ds.dsLabel,
                                m_ds.versionable,
                                m_ds.dsMIME,
                                m_ds.formatURI,
                                m_ds.dsLocation, 
                                m_ds.dsState,
                                m_ds.logMessage,
                                m_ds.force);
                    }
                    succeededCount++;
                    logSucceededDirective(m_ds.objectPID, localName,
                        "DatastreamID: " + m_ds.dsID + " modified");
                }

            } catch (Exception e) {
                if(modifyDatastream) {
                    failedCount++;
                    logFailedDirective(m_ds.objectPID, localName, e, null);
                }
            } finally {
                modifyDatastream = false;
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("setDatastreamState")) {

            try {

                // Process setDatastreamState only if no previous errors encountered
                if(setDatastreamState) {
                    APIM.setDatastreamState(m_ds.objectPID, m_ds.dsID,
                        m_ds.dsState, "SetDatastreamState");
                    succeededCount++;
                    logSucceededDirective(m_ds.objectPID, localName,
                        "datastream: " + m_ds.dsID
                            + "\n    Set dsState: " + m_ds.dsState);
                }

            } catch (Exception e) {
                if (setDatastreamState) {
                    failedCount++;
                    logFailedDirective(m_ds.objectPID, localName, e, null);
                }
            } finally {
                setDatastreamState = false;
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("addDatastreamBinding")) {

        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("removeDatastreamBinding")) {

        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("addDisseminator")) {

            try {

                // Process addDisseminator only if no previous errors encountered
                if (addDisseminator) {
                    String dissID = null;
                    Iterator iter = m_dsBindings.values().iterator();
                    DatastreamBinding[] bindings = new DatastreamBinding[m_dsBindings.size()];
                    int i = 0;
                    while (iter.hasNext()) {
                        bindings[i] = (DatastreamBinding) iter.next();
                        i++;
                    }

                    m_dsBindingMap.setDsBindMapID("hopefully this is set by the server!"); // unnecessary
                    m_dsBindingMap.setDsBindMechanismPID(m_diss.bMechID);
                    m_dsBindingMap.setDsBindMapLabel("Binding map for bMech object: "
                            + m_diss.bMechID);
                    m_dsBindingMap.setState("A");  // unnecessary...
                    m_dsBindingMap.setDsBindings(bindings);
                    dissID = APIM.addDisseminator(m_diss.parentPID, m_diss.bDefID,
                            m_diss.bMechID, m_diss.dissLabel, m_diss.bDefLabel,
                            m_diss.bMechLabel, m_dsBindingMap, m_diss.dissState,
                            m_diss.logMessage);
                    if (dissID!=null) {
                        succeededCount++;
                        logSucceededDirective(m_diss.parentPID, localName,
                            "disseminatorID: " + dissID + " Created.");
                    } else {
                        failedCount++;
                        logFailedDirective(m_diss.parentPID, localName, null,
                            "Unable to create disseminator...");
                    }
                }

            } catch (Exception e) {
                if(addDisseminator) {
                    failedCount++;
                    logFailedDirective(m_diss.parentPID, localName, e, "");
                    addDisseminator = false;
                }
            } finally {
                addDisseminator = false;
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("modifyDisseminator")) {

            try {

                // Process modifyDisseminator only if no previous errors encountered
                if (modifyDisseminator) {
                    Iterator iter = m_dsBindings.values().iterator();
                    DatastreamBinding[] bindings = new DatastreamBinding[m_dsBindings.size()];
                    int i = 0;
                    while (iter.hasNext()) {
                        bindings[i] = (DatastreamBinding) iter.next();
                        i++;
                    }


                    m_dsBindingMap.setDsBindMapID("hopefully this is set by the server!"); // unnecessary
                    m_dsBindingMap.setDsBindMechanismPID(m_diss.bMechID);
                    m_dsBindingMap.setDsBindMapLabel("Binding map for bMech object: "
                            + m_diss.bMechID);
                    m_dsBindingMap.setState("A");  // unnecessary...
                    m_dsBindingMap.setDsBindings(bindings);
                    APIM.modifyDisseminator(m_diss.parentPID, m_diss.dissID,
                            m_diss.bMechID, m_diss.dissLabel, m_diss.bDefLabel,
                            m_diss.bMechLabel, m_dsBindingMap,
                            m_diss.dissState, "ModifyDisseminator", m_diss.force);
                    succeededCount++;
                    logSucceededDirective(m_diss.parentPID, localName,
                        "disseminatorID: " + m_diss.dissID + " Modified.");
                }

            } catch (Exception e)
            {
                if(modifyDisseminator) {
                    failedCount++;
                    logFailedDirective(m_diss.parentPID, localName, e, "");
                }
            } finally {
                modifyDisseminator = false;
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("purgeDisseminator")) {

            try {

                // Process purgeDisseminator only if no previous errors encountered
                if (purgeDisseminator) {
                    String[] versionsPurged = null;
                    versionsPurged = APIM.purgeDisseminator(m_diss.parentPID,
                        m_diss.dissID, m_diss.asOfDate, m_diss.logMessage);
                    if (versionsPurged.length > 0) {
                        succeededCount++;
                        if (m_diss.asOfDate!= null) {
                            logSucceededDirective(m_diss.parentPID, localName,
                                "disseminatorID: " + m_diss.dissID
                                    + "\n    Purged all versions prior to: "
                                    + m_diss.asOfDate
                                    +"\n    Versions purged: " + versionsPurged.length);
                        } else {
                            logSucceededDirective(m_diss.parentPID, localName,
                                "disseminatorID: " + m_diss.dissID
                                    + "\n    Purged all versions. "
                                    +"\n    Versions purged: " + versionsPurged.length);
                        }
                    } else {
                        failedCount++;
                        logFailedDirective(m_diss.parentPID, localName, null,
                            "Unable to purge disseminator; verify disseminator ID and/or asOfDate");
                    }
                }
            } catch (Exception e) {
                if(purgeDisseminator) {
                    failedCount++;
                    logFailedDirective(m_diss.parentPID, localName, e, "");
                }
            } finally {
                purgeDisseminator = false;
            }
        } else if (namespaceURI.equalsIgnoreCase(FBM) && localName.equalsIgnoreCase("setDisseminatorState")) {

            try {

                // Process setDisseminatorState only if no previous errors encountered
                if (setDisseminatorState) {
                    APIM.setDisseminatorState(m_diss.parentPID, m_diss.dissID,
                        m_diss.dissState, "SetDisseminatorState");
                    succeededCount++;
                    logSucceededDirective(m_diss.parentPID, localName,
                        "disseminator: " + m_diss.dissID
                            + "\n    Set dissState: " + m_diss.dissState);
                }

            } catch (Exception e) {
                if (setDisseminatorState) {
                    failedCount++;
                    logFailedDirective(m_diss.parentPID, localName, e, null);
                }
            } finally {
                setDisseminatorState = false;
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
            Exception e, String msg) {
        out.println("  <failed directive=\"" + directive + "\" sourcePID=\"" + sourcePID + "\">");
        if (e!=null) {
            String message=e.getMessage();
            if (message==null)
                message=e.getClass().getName();
            out.println("    " + StreamUtility.enc(message));
        } else {
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
    private static void logParserError( Exception e, String msg) {
        out.println("  <parserError>");
        if (e!=null) {
            String message=e.getMessage();
            if (message==null)
                message=e.getClass().getName();
            out.println("    " + StreamUtility.enc(message));
        } else {
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

    public static Map getBDefLabelMap()
            throws IOException {
        try {
            HashMap labelMap=new HashMap();
            FieldSearchQuery query=new FieldSearchQuery();
            Condition[] conditions=new Condition[1];
            conditions[0]=new Condition();
            conditions[0].setProperty("fType");
            conditions[0].setOperator(ComparisonOperator.fromValue("eq"));
            conditions[0].setValue("D");
            query.setConditions(conditions);
            String[] fields=new String[] {"pid", "label"};
            FieldSearchResult result=APIA.findObjects(
                    fields, new NonNegativeInteger("50"), query);
            while (result!=null) {
                ObjectFields[] resultList=result.getResultList();
                for (int i=0; i<resultList.length; i++) {
                    labelMap.put(resultList[i].getPid(), resultList[i].getLabel());
                }
                if (result.getListSession()!=null) {
                    result=APIA.resumeFindObjects(
                            result.getListSession().getToken());
                } else {
                    result=null;
                }
            }
            return labelMap;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Get a map of pid-to-label of behavior mechanisms that implement
     * the behavior defined by the indicated bdef.
     * 
     * @param bDefPID PID of the associated behavior defintion object.
     * 
     * @return A list of the behavior mechanism labels.
     * 
     * @throws IOException If an error occurs in retrieving the list of labels.
     */
    public static Map getBMechLabelMap(String bDefPID)
            throws IOException {
        try {
            HashMap labelMap=new HashMap();
            FieldSearchQuery query=new FieldSearchQuery();
            Condition[] conditions=new Condition[2];
            conditions[0]=new Condition();
            conditions[0].setProperty("fType");
            conditions[0].setOperator(ComparisonOperator.fromValue("eq"));
            conditions[0].setValue("M");
            conditions[1]=new Condition();
            conditions[1].setProperty("bDef");
            conditions[1].setOperator(ComparisonOperator.fromValue("has"));
            conditions[1].setValue(bDefPID);
            query.setConditions(conditions);
            String[] fields=new String[] {"pid", "label"};
            FieldSearchResult result=APIA.findObjects(
                    fields, new NonNegativeInteger("50"), query);
            while (result!=null) {
                ObjectFields[] resultList=result.getResultList();
                for (int i=0; i<resultList.length; i++) {
                    labelMap.put(resultList[i].getPid(), resultList[i].getLabel());
                }
                if (result.getListSession()!=null) {
                    result=APIA.resumeFindObjects(
                            result.getListSession().getToken());
                } else {
                    result=null;
                }
            }
            return labelMap;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }


    public static void main(String[] args)
    {
        String host = "localhost";
        int port = 8080;
        String user = "fedoraAdmin";
        String pass = "fedoraAdmin";
        PrintStream logFile;
        FedoraAPIM APIM;
        FedoraAPIA APIA;

        try {
            UPLOADER=new Uploader(host, port, user, pass);
            logFile = new PrintStream(new FileOutputStream("C:\\zlogfile.txt"));
            APIM = fedora.client.APIMStubFactory.getStub(host, port, user, pass);
            APIA = fedora.client.APIAStubFactory.getStub(host, port, user, pass);
            InputStream file = new FileInputStream("c:\\fedora\\mellon\\dist\\client\\demo\\batch-demo\\modify-batch-directives-valid.xml");
            BatchModifyParser bmp = new BatchModifyParser(UPLOADER, APIM, APIA, file, logFile);
            file.close();
            logFile.close();
        } catch (Exception e) {
            System.out.println("ERROR: "+e.getClass().getName()
                               + " - " + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
        }
    }

}
