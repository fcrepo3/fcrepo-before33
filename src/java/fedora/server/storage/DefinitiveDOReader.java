package fedora.server.storage;

/**
 * <p>Title: DefinitiveDOReader.java </p>
 * <p>Description: Digital Object Reader. Uses SAX parser on METS. </p>
 * <p>COMPONENT VERSIONING NOT SUPPORTED AT THIS TIME!! </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import fedora.server.storage.abstraction.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class DefinitiveDOReader implements DOReader
{

  // TEMPORARY: static variables to suppor testing via main()
  protected static boolean debug = true;
  private static Hashtable fakeDORegistry;
  private static final String [][] testObjects =
  {
      {"uva-lib:1225", "image-w.xml"},
      {"uva-lib:1220", "image-test.xml"},
      {"uva-bmech-image-w:112", "photo-w-mech.xml"},
  };
  // TEMPORARY: static method to load the Fake DO Registry
  static
  {
    fakeDORegistry = new Hashtable(testObjects.length);
    for (int i = 0; i < testObjects.length; i++)
    {
        fakeDORegistry.put(testObjects[i][0], testObjects[i][1]);
    }
  }

  private DOReaderSAXErrorHandler doErrorHandler;
  protected XMLReader xmlreader;
  private String PID = null;
  protected Hashtable datastreamTbl = new Hashtable();
  private Hashtable disseminatorTbl = new Hashtable();


  public static void main(String[] args)
  {
    if (args.length == 0)
    {
      System.err.println("provide args: [0]=debug(true/false) [1]=PID");
      System.exit(1);
    }

    debug = (args[0].equalsIgnoreCase("true")) ? true : false;

    // FOR TESTING...
    DefinitiveDOReader doReader = new DefinitiveDOReader(args[1]);
    doReader.GetObjectPID();
    doReader.ListDatastreamIDs("A");
    doReader.ListDisseminatorIDs("A");
    doReader.GetDatastreams(null);
    doReader.GetDatastream("DS1", null);
    doReader.GetDisseminators(null);
    doReader.GetDisseminator("DISS1", null);
    doReader.GetBehaviorDefs(null);

  }

  public DefinitiveDOReader()
  {
  }
  public DefinitiveDOReader(String objectPID)
  {
    // Read the digital object xml from persistent storage

    File doFile = locateObject(objectPID);
    if (debug) System.out.println("object filepath = " + doFile.getPath());

    try
    {
      InputSource doXML = new InputSource(new FileInputStream(doFile));
      doErrorHandler = new DOReaderSAXErrorHandler();
      SAXParserFactory saxfactory = SAXParserFactory.newInstance();
      saxfactory.setValidating(false);
      saxfactory.setNamespaceAware(true);

      SAXParser parser = saxfactory.newSAXParser();
      //parser.parse(doFile, new METSEventHandler());
      //XMLReader xmlreader = parser.getXMLReader();
      xmlreader = parser.getXMLReader();
      xmlreader.setContentHandler(new METSEventHandler());
      xmlreader.setErrorHandler(doErrorHandler);
      xmlreader.parse(doXML);
    }
    catch (Exception e)
    {
      System.err.println("Error: " + e.toString());
      System.exit(1);
    }
  }

    public String GetObjectXML()
    {
      return(null);
    }

    public String ExportObject()
    {
      return(null);
    }

  /**
   * Methods that pertain to getting the digital object components
   */

    public String GetObjectPID()
    {
      if (debug) System.out.println("GetObjectPID = " + PID);
      return(PID);
    }

    public String[] ListDatastreamIDs(String state)
    {
      //FIXIT! Implement the state filter!!
      Set idSet = datastreamTbl.keySet();
      String[] dsIDList = (String[]) idSet.toArray(new String[0]);
      if (debug)
      {
        for (int i = 0; i < dsIDList.length; i++)
        {
          System.out.println("ListDatastreamIDs[" + i + "]=" + dsIDList[i]);
        }
      }
      return(dsIDList);
    }

    public Datastream[] GetDatastreams(Date versDateTime)
    {
      // TODO! dateTime filter not implemented in this release!!
      Collection c = datastreamTbl.values();
      Datastream[] datastreams = (Datastream[]) c.toArray(new Datastream[0]);

      if (debug)
      {
        for (int i = 0; i < datastreams.length; i++)
        {
          System.out.println("GetDatastreams[" + i + "]");
          System.out.println("  dsID[" + i + "]=" + datastreams[i].DatastreamID);
          System.out.println("  versID[" + i + "]=" + datastreams[i].DSVersionID);
          System.out.println("  createDT[" + i + "]=" + datastreams[i].DSCreateDT);
          System.out.println("  label[" + i + "]=" + datastreams[i].DSLabel);
          System.out.println("  location[" + i + "]=" + datastreams[i].DSLocation);
          System.out.println("  MIME[" + i + "]=" + datastreams[i].DSMIME);
          System.out.println("  state[" + i + "]=" + datastreams[i].DSState);
          System.out.println("  controlGrp[" + i + "]=" + datastreams[i].DSControlGrp);
          System.out.println("  infoType[" + i + "]=" + datastreams[i].DSInfoType);

          if (datastreams[i].DSControlGrp == 2)
          {
            System.out.println("  xmldata[" + i + "]=");
            String s = new String(((DatastreamXMLMetadata)datastreams[i]).xmlContent);
            System.out.println(s);
          }
        }
      }
      return(datastreams);
    }

    public Datastream GetDatastream(String datastreamID, Date versDateTime)
    {
      // TODO! dateTime filter not implemented in this release!!
      Datastream datastream = (Datastream) datastreamTbl.get(datastreamID);
      if (debug)
      {
        System.out.println("GetDatastream(" + datastreamID + ")");
        System.out.println("  dsID= " + datastream.DatastreamID);
        System.out.println("  location= " + datastream.DSLocation);
        System.out.println("  controlGrp= " + datastream.DSControlGrp);
        System.out.println("  state= " + datastream.DSState);
        System.out.println("  lable= " + datastream.DSLabel);
      }
      return(datastream);
    }

    public Disseminator[] GetDisseminators(Date versDateTime)
    {
      // TODO! dateTime filter not implemented in this release!!
      Collection c = disseminatorTbl.values();
      Disseminator[] disseminators = (Disseminator[]) c.toArray(new Disseminator[0]);

      if (debug)
      {
        for (int i = 0; i < disseminators.length; i++)
        {
          System.out.println("GetDisseminators[" + i + "]");
          System.out.println("  dissID[" + i + "]=" + disseminators[i].dissID);
          System.out.println("  versID[" + i + "]=" + disseminators[i].dissVersionID);
          System.out.println("  createDT[" + i + "]=" + disseminators[i].dissCreateDT);
          System.out.println("  label[" + i + "]=" + disseminators[i].dissLabel);
          System.out.println("  bDefID[" + i + "]=" + disseminators[i].bDefID);
          System.out.println("  bMechID[" + i + "]=" + disseminators[i].bMechID);

          System.out.println("  dsBindMapID[" + i + "]=" + disseminators[i].dsBindMap.dsBindMapID);
          System.out.println("  dsBindMapLabel[" + i + "]=" + disseminators[i].dsBindMap.dsBindMapLabel);

          for (int j = 0; j < disseminators[i].dsBindMap.dsBindings.length; j++)
          {
            System.out.println("  >>bindKey[" + j + "]= " + disseminators[i].dsBindMap.dsBindings[j].bindKeyName);
            System.out.println("  >>bindLabel[" + j + "]= " + disseminators[i].dsBindMap.dsBindings[j].bindLabel);
            System.out.println("  >>seqNo[" + j + "]= " + disseminators[i].dsBindMap.dsBindings[j].seqNo);
            System.out.println("  >>dsid[" + j + "]= " + disseminators[i].dsBindMap.dsBindings[j].datastreamID);
          }
        }
      }
      return(disseminators);
    }

    public String[] ListDisseminatorIDs(String state)
    {
      // FIXIT!  Implement the state filter!!
      Set idSet = disseminatorTbl.keySet();
      String[] dissIDList = (String[]) idSet.toArray(new String[0]);
      if (debug)
      {
        for (int i = 0; i < dissIDList.length; i++)
        {
          System.out.println("ListDisseminatorIDs[" + i + "]=" + dissIDList[i]);
        }
      }
      return(dissIDList);
    }

    public Disseminator GetDisseminator(String disseminatorID, Date versDateTime)
    {
      // TODO! dateTime filter not implemented in this release!!
      Disseminator disseminator = (Disseminator) disseminatorTbl.get(disseminatorID);
      if (debug)
      {
        System.out.println("GetDisseminator(" + disseminatorID + ")");
        System.out.println("  dissID= " + disseminator.dissID);
        System.out.println("  bDefID= " + disseminator.bDefID);
        System.out.println("  bMechID= " + disseminator.bMechID);
        System.out.println("  dsBindMapID= " + disseminator.dsBindMapID);
      }
      return(disseminator);
    }

  /**
   *  Methods to obtain information stored in the Behavior Definition and
   *  Behavior Mechanism objects to which the digital object's disseminators
   *  refer.
   */

    // Returns PIDs of Behavior Definitions to which object subscribes
    public String[] GetBehaviorDefs(Date versDateTime)
    {
      // TODO! dateTime filter not implemented in this release!!
      Collection c = disseminatorTbl.values();
      String[] bdefIDs = new String[c.size()];
      Iterator it = c.iterator();

      for (int i = 0; i < c.size(); i++)
      {
        Disseminator diss = (Disseminator) it.next();
        bdefIDs[i] = diss.bDefID;
      }
      if (debug)
      {
        for (int i = 0; i < bdefIDs.length; i++)
        {
          System.out.println("GetBehaviorDefs[" + i + "]=" + bdefIDs[i]);
        }
      }
      return(bdefIDs);
    }

    // Returns list of methods that Behavior Mechanism implements for a BDef
    public MethodDef[] GetBMechMethods(String BdefPID, Date versDateTime)
    {
      // TODO! dateTime filter not implemented in this release!!
      return(null);
    }

    // Overloaded method: returns InputStream as alternative
    public InputStream GetBMechMethodsWSDL(String BdefID, Date versDateTime)
    {
      // TODO! dateTime filter not implemented in this release!!
      return(null);
    }


    // private methods

    private File locateObject(String PID) {
      // FIXIT! insert code to locate object using the real digital object
      // registory when it exists!

      String filePath = (String)DefinitiveDOReader.fakeDORegistry.get(PID);
      return(new File(filePath));
    }

    private Date convertDate(String xmlDateTime) {

      //2002-05-20T06:32:00
      Date date = null;
      try
      {
      DateFormat df = (DateFormat)new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
      date = (Date)df.parse(xmlDateTime);
      //date = (Date)df.parse("2002-05-20T06:32:00");
      //System.out.println("TEST DATE: " + date.toString());
      }
      catch (ParseException e)
      {
        System.err.println("Error: " + e);
      }
      return(date);
    }

    class METSEventHandler extends DefaultHandler
    {
      private boolean getAsStream = false;
      private boolean isXMLDatastream = false;

      private boolean inMETS = false;
      private boolean inDMDSec = false;
      private boolean inAMDSec = false;
      private boolean inMDSec = false;
      private boolean inMDWrap = false;
      private boolean inXMLData = false;
      private boolean inFileSec = false;
      private boolean inFileGrpRoot = false;
      private boolean inFileGrp = false;
      private boolean inFile = false;
      private boolean inFLocat = false;
      private boolean inStructMap = false;
      private boolean inBindDivRoot = false;
      private boolean inBindDiv = false;
      private boolean inFilePtr = false;
      private boolean inBehaviorSec = false;
      private boolean inInterfaceDef = false;
      private boolean inMechanism = false;

      private String h_PID;
      private Vector h_vDatastream;
      private Vector h_vDisseminator;
      private Vector h_vDsBinding;
      private Datastream h_datastream;
      private DSBinding h_dsBinding;
      private Hashtable h_dsBindMapTbl;
      private Disseminator h_diss;
      private DSBindingMap h_dsBindMap;
      private String h_xmlData;
      private StringWriter h_xmlstream;


      public void startDocument()
      {
        //initialize the event handler variables

        h_vDatastream = new Vector();
        h_vDisseminator = new Vector();
        h_dsBindMapTbl = new Hashtable();
      }

      public void endDocument()
      {
          // Set the main class variables from the event handler variables

          // OBJECT PID
          PID = h_PID;

          // DATASTREAMS
          int dsCount = h_vDatastream.size();
          for (int i = 0; i < dsCount; i++)
          {
              Datastream ds = (Datastream) h_vDatastream.get(i);
              datastreamTbl.put(ds.DatastreamID, ds);
          }

          //DISSEMINATORS
          // match up datastream binding maps to their disseminators
          int dissCount = h_vDisseminator.size();
          for (int i = 0; i < dissCount; i++)
          {
              Disseminator diss = (Disseminator) h_vDisseminator.get(i);
              diss.dsBindMap = (DSBindingMap)h_dsBindMapTbl.get(diss.dsBindMapID);
              //disseminators[i] = diss;
              disseminatorTbl.put(diss.dissID, diss);
          }

          h_vDatastream = null;
          h_vDisseminator = null;
          h_dsBindMapTbl = null;
      }

      public void characters(char ch[], int start, int length)
      {
        if (isXMLDatastream && getAsStream)
        {
          h_xmlstream.write(ch, start, length);
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

      public void startElement(String namespaceURI, String localName, String qName, Attributes attrs)
        throws SAXException
      {

        // FIXIT! PROBLEM WHEN ATTRIBUTE VALUE HAS ENTITY REF LIKE &amp; &lt; !!!!!
        if (isXMLDatastream && getAsStream)
        {
          h_xmlstream.write("<" + qName);

          int attrCount = attrs.getLength();
          for (int i = 0; i < attrCount; i++)
          {
            String s = attrs.getValue(i);
            h_xmlstream.write(" " + attrs.getLocalName(i) + "=\"" + s + "\"");
          }
          h_xmlstream.write(">");
        }

        if (qName.equalsIgnoreCase("METS:mets"))
        {
          inMETS = true;
          h_PID = attrs.getValue("OBJID");
          if (debug) System.out.println("the OBJID attr = " + h_PID);
        }
        else if (qName.equalsIgnoreCase("METS:dmdSec"))
        {
          inDMDSec = true;
          isXMLDatastream = true;
          h_datastream = new DatastreamXMLMetadata();
          h_datastream.DSInfoType = localName;
          h_datastream.DSControlGrp = 2;
          h_datastream.DSCreateDT = convertDate(attrs.getValue("CREATED"));
          h_datastream.DSState = attrs.getValue("STATUS");

          // FIXIT! This does not properly deal with picking up
          // datastreamID vs. datastream version ID (dmdSec issue)
          h_datastream.DatastreamID = attrs.getValue("GROUPID");
        }
        else if (qName.equalsIgnoreCase("METS:amdSec"))
        {
          inAMDSec = true;
          int attrCount = attrs.getLength();
          for (int i = 0; i < attrCount; i++)
          {
            String dsid;
            if (!((dsid = attrs.getValue("ID")).equalsIgnoreCase("FEDORA-AUDITTRAIL")))
            {
                isXMLDatastream = true;
                h_datastream = new DatastreamXMLMetadata();
                h_datastream.DatastreamID = dsid;
                h_datastream.DSControlGrp = 2;
            }
          }
        }
        else if (qName.equalsIgnoreCase("METS:techMD") ||
                 qName.equalsIgnoreCase("METS:rightsMD") ||
                 qName.equalsIgnoreCase("METS:sourceMD") ||
                 qName.equalsIgnoreCase("METS:digiprovMD"))
        {
          inMDSec = true;

          if (isXMLDatastream)
          {
            h_datastream.DSInfoType = localName;
            h_datastream.DSCreateDT = convertDate(attrs.getValue("CREATED"));
            h_datastream.DSState = attrs.getValue("STATUS");
          }
        }
        else if (qName.equalsIgnoreCase("METS:mdWrap"))
        {
          inMDWrap = true;

          if (isXMLDatastream)
          {
            h_datastream.DSMIME = "text/xml";
            h_datastream.DSLabel = attrs.getValue("LABEL");
          }
        }
        else if (qName.equalsIgnoreCase("METS:xmlData"))
        {
          inXMLData = true;

          if (isXMLDatastream)
          {
            h_xmlstream = new StringWriter();
            getAsStream = true;
          }
        }
        else if (qName.equalsIgnoreCase("METS:fileSec"))
        {
          inFileSec = true;
        }
        else if (qName.equalsIgnoreCase("METS:fileGrp"))
        {
          inFileGrp = true;

          // Get the datastreamIDs off of ID attribute, excepting root fileGrp
          // ISSUE: we must enforce the ID value for root METS:fileGrp to be
          // "DATASTREAMS" for this to work reliably.
          String dsid;
          if (!(dsid = attrs.getValue("ID")).equalsIgnoreCase("DATASTREAMS"))
          {
            h_datastream = new Datastream();
            h_datastream.DatastreamID = dsid;
          }
        }
        else if (qName.equalsIgnoreCase("METS:file"))
        {
          inFile = true;
          if (inFileGrp)
          {
            h_datastream.DSInfoType = "DATA";
            h_datastream.DSMIME = attrs.getValue("MIMETYPE");
            h_datastream.DSVersionID = attrs.getValue("ID");
            h_datastream.DSCreateDT = convertDate(attrs.getValue("CREATED"));
            h_datastream.DSSize = attrs.getValue("SIZE");
            h_datastream.DSState = attrs.getValue("STATUS");

            if (attrs.getValue("OWNERID").equalsIgnoreCase("E"))
              h_datastream.DSControlGrp = 3;
            else if (attrs.getValue("OWNERID").equalsIgnoreCase("I"))
              h_datastream.DSControlGrp = 1;
            else
            {
              // throw error
            }
          }
        }
        else if (qName.equalsIgnoreCase("METS:FLocat"))
        {
          inFLocat = true;
          if (inFile)
          {
            h_datastream.DSLocation = attrs.getValue("xlink:href");
            h_datastream.DSLabel = attrs.getValue("xlink:title");
          }
        }
        else if (qName.equalsIgnoreCase("METS:structMap"))
        {
          inStructMap = true;
          h_dsBindMap = new DSBindingMap();
          h_vDsBinding = new Vector();

          // ISSUE: we must enforce the value of TYPE attribute on METS:structMap
          // to be "fedora:dsBindingMap" for this to work reliably.
          // Ignore structMap if it's not a Fedora datastream binding map
          // TODO:  test when non-fedora structmaps exist

          if ((attrs.getValue("TYPE")).equalsIgnoreCase("fedora:dsBindingMap"))
          {
            h_dsBindMap.dsBindMapID = attrs.getValue("ID");
            h_dsBindMap.state = attrs.getValue("STATUS");
          }
          else
          {
            h_dsBindMap = null;
          }
        }
        else if (qName.equalsIgnoreCase("METS:div") && !inBindDivRoot && !inBindDiv)
        {
          inBindDivRoot = true;
          h_dsBindMap.dsBindMechanismPID = attrs.getValue("TYPE");
          h_dsBindMap.dsBindMapLabel = attrs.getValue("LABEL");
        }
        else if (qName.equalsIgnoreCase("METS:div") && inBindDivRoot && !inBindDiv)
        {
          inBindDiv = true;
          h_dsBinding = new DSBinding();
          h_dsBinding.bindKeyName = attrs.getValue("TYPE");
          h_dsBinding.seqNo = attrs.getValue("ORDER");
          h_dsBinding.bindLabel = attrs.getValue("LABEL");
        }
        else if (qName.equalsIgnoreCase("METS:fptr") && inBindDivRoot && inBindDiv && !inFilePtr)
        {
          inFilePtr = true;

          int attrCount = attrs.getLength();
          for (int i = 0; i < attrCount; i++)
          {
            if (attrs.getLocalName(i).equalsIgnoreCase("FILEID"))
            {
              h_dsBinding.datastreamID = attrs.getValue(i);
            }
          }
        }
        else if (qName.equalsIgnoreCase("METS:behaviorSec"))
        {
          inBehaviorSec = true;
          h_diss = new Disseminator();
          h_diss.dissID = attrs.getValue("GROUPID");
          h_diss.dsBindMapID = attrs.getValue("STRUCTID");
          h_diss.dissCreateDT = convertDate(attrs.getValue("CREATED"));
          h_diss.dissLabel = attrs.getValue("LABEL");
          h_diss.dissVersionID = attrs.getValue("ID");
        }
        else if (qName.equalsIgnoreCase("METS:interfaceDef"))
        {
          inInterfaceDef = true;
          if (inBehaviorSec)
          {
            h_diss.bDefID = attrs.getValue("xlink:href");
          }
        }
        else if (qName.equalsIgnoreCase("METS:mechanism"))
        {
          inMechanism = true;
          if (inBehaviorSec)
          {
            h_diss.bMechID = attrs.getValue("xlink:href");
          }
        }
      }

      public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException
      {
        if (isXMLDatastream && getAsStream && !qName.equalsIgnoreCase("METS:xmlData"))
        {
          h_xmlstream.write("</" + qName + ">");
        }

        if (qName.equalsIgnoreCase("METS:mets") && inMETS)
        {
          inMETS = false;
        }
        else if (qName.equalsIgnoreCase("METS:dmdSec") && inDMDSec)
        {
          inDMDSec = false;
          if (isXMLDatastream)
            isXMLDatastream = false;
        }
        else if (qName.equalsIgnoreCase("METS:amdSec") && inAMDSec)
        {
          inAMDSec = false;
          if (isXMLDatastream)
            isXMLDatastream = false;
        }
        else if (qName.equalsIgnoreCase("METS:techMD") ||
                 qName.equalsIgnoreCase("METS:rightsMD") ||
                 qName.equalsIgnoreCase("METS:sourceMD") ||
                 qName.equalsIgnoreCase("METS:digiprovMD"))
        {
          inMDSec = false;
        }
        else if (qName.equalsIgnoreCase("METS:xmlData") && inXMLData)
        {
          inXMLData = false;

          // FIXIT!  Get rid of redundancy??
          if (isXMLDatastream && getAsStream)
          {
            getAsStream = false;

            ((DatastreamXMLMetadata)h_datastream).xmlContent = h_xmlstream.toString().getBytes();
            h_vDatastream.addElement(h_datastream);
            h_datastream = null;
            h_xmlstream.flush();
          }
        }
        else if (qName.equalsIgnoreCase("METS:mdWrap") && inMDWrap)
        {
          inMDWrap = false;
        }
        else if (qName.equalsIgnoreCase("METS:fileSec") && inFileSec)
        {
          inFileSec = false;
        }
        else if (qName.equalsIgnoreCase("METS:fileGrp") && inFileGrp)
        {
          inFileGrp = false;
        }
        else if (qName.equalsIgnoreCase("METS:file") && inFile)
        {
          inFile = false;
          h_vDatastream.addElement(h_datastream);
          h_datastream = null;
        }
        else if (qName.equalsIgnoreCase("METS:FLocat") && inFLocat)
        {
          inFLocat = false;
        }
        else if (qName.equalsIgnoreCase("METS:structMap") && inStructMap)
        {
          inStructMap = false;
          h_dsBindMap.dsBindings = (DSBinding[]) h_vDsBinding.toArray(new DSBinding[0]);
          h_vDsBinding = null;

          // put the complete datastream binding map in hash table so it can
          // later be matched up with the disseminator it's associated with
          h_dsBindMapTbl.put(h_dsBindMap.dsBindMapID, h_dsBindMap);
          h_dsBindMap = null;
        }
        else if (qName.equalsIgnoreCase("METS:div") && inStructMap && inBindDivRoot && !inBindDiv && !inFilePtr)
        {
          inBindDivRoot = false;
        }
        else if (qName.equalsIgnoreCase("METS:div") && inStructMap && inBindDivRoot && inBindDiv && !inFilePtr)
        {
          inBindDiv = false;
          h_vDsBinding.addElement(h_dsBinding);
          h_dsBinding = null;
        }
        else if (qName.equalsIgnoreCase("METS:fptr") && inFilePtr)
        {
          inFilePtr = false;
        }
        else if (qName.equalsIgnoreCase("METS:behaviorSec")  && inBehaviorSec)
        {
          inBehaviorSec = false;
          h_vDisseminator.addElement(h_diss);
          h_diss = null;
        }
        else if (qName.equalsIgnoreCase("METS:interfaceDef") && inInterfaceDef)
        {
          inInterfaceDef = false;
        }
        else if (qName.equalsIgnoreCase("METS:mechanism") && inMechanism)
        {
          inMechanism = false;
        }
      }
    }
}