package fedora.server.storage;

/**
 * <p>Title: DefinitiveDOReader.java </p>
 * <p>Description: Digital Object Reader. Uses SAX parser on METS.
 * COMPONENT VERSIONING NOT SUPPORTED IN THIS READER!!
 * ASSUMES THERE IS NO VERSIONING IS IN THE OBJECT XML.
 * MUST FIX THIS TO LOOK FOR CURRENT VERSIONS OF COMPONENTS. </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette, payette@cs.cornell.edu
 * @version 1.0
 */

import fedora.server.storage.types.*;
import fedora.server.storage.lowlevel.*;
import fedora.server.errors.*;
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
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ext.LexicalHandler;
import java.util.regex.*;

public class DefinitiveDOReader implements DOReader
{
  // TEMPORARY: static variables to suppor testing via main()
  protected static boolean debug = true;
  private static Hashtable fakeDORegistry;
  private static final String [][] testObjects =
  {
      {"uva-lib:1225", "image-w.xml"},
      {"uva-lib:1220", "image-test.xml"},
      {"uva-bdef-image-w:101", "photo-w-bdef.xml"},
      {"uva-bmech-image-w:112", "photo-w-mech.xml"},
      {"uva-bdef-stdimg:8", "std-img-bdef.xml"},
      {"uva-bmech-stdimg:10", "std-img-mech.xml"},
      {"uva-bmech-test:00", "testmech.xml"},
      {"uva-lib:99", "testobj.xml"},
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

  protected DOReaderSAXErrorHandler doErrorHandler;
  protected XMLReader xmlreader;
  private String PID = null;
  private String doLabel = null;
  private String doState = null;
  protected Hashtable datastreamTbl = new Hashtable();
  private Hashtable disseminatorTbl = new Hashtable();
  private Hashtable dissbDefTobMechTbl = new Hashtable();


  public static void main(String[] args)
  {
    if (args.length == 0)
    {
      System.err.println("provide args: [0]=debug(true/false) [1]=PID");
      System.exit(1);
    }

    debug = (args[0].equalsIgnoreCase("true")) ? true : false;

    if (debug)
    {
      try
      {
        // FOR TESTING...
        DefinitiveDOReader doReader = new DefinitiveDOReader(args[1]);
        doReader.GetObjectPID();
        doReader.GetObjectLabel();
        doReader.GetObjectState();
        doReader.ListDatastreamIDs("A");
        doReader.ListDisseminatorIDs("A");
        Datastream[] dsArray = doReader.GetDatastreams(null);
        doReader.GetDatastream(dsArray[0].DatastreamID, null);
        doReader.GetDisseminators(null);
        String[] bdefArray = doReader.GetBehaviorDefs(null);
        doReader.GetBMechMethods(bdefArray[0], null);
        doReader.GetDSBindingMaps(null);
      }
      catch (ServerException e)
      {
        System.err.println("FEDORA EXCEPTION:)" + e.getMessage());
      }
      catch (Exception e)
      {
        System.err.println("NON-FEDORA EXCEPTION:)" + e.toString());
      }
    }
  }

  public DefinitiveDOReader() throws ServerException
  {
  }
  public DefinitiveDOReader(String objectPID) throws ServerException
  {
    InputSource doXML = null;

    // FOR TESTING ONLY:
    // Read the digital object xml from test storage using fake registry
    /*
    try
    {
      File doFile = locateObject(objectPID);
      if (debug) System.out.println("object filepath = " + doFile.getPath());
      doXML = new InputSource(new FileInputStream(doFile));
    }
    catch (Exception e)
    {
      System.out.println("ERROR in Constructor: " + e);
    }
    */

    try
    {
      // LLSTORE: call to low level storage layer to retrieve object
      doXML = new InputSource(FileSystemLowlevelStorage.getPermanentStore().retrieve(objectPID));
    }
    catch(LowlevelStorageException e)
    {
      throw new StreamIOException("ERROR_LLSTORE_RETRIEVE: " + e.getMessage());
    }
    catch (Exception e)
    {
      throw new GeneralException("ERROR_MISC: " + e.getMessage());
    }

    try
    {
      doErrorHandler = new DOReaderSAXErrorHandler();
      SAXParserFactory saxfactory = SAXParserFactory.newInstance();
      saxfactory.setValidating(false);
      saxfactory.setNamespaceAware(true);
      SAXParser parser = saxfactory.newSAXParser();
      xmlreader = parser.getXMLReader();
      xmlreader.setContentHandler(new METSEventHandler());
      xmlreader.setErrorHandler(doErrorHandler);
      long startparseTime = System.currentTimeMillis();
      xmlreader.parse(doXML);
      long endparseTime = System.currentTimeMillis();
      long runTime = endparseTime - startparseTime;
      System.out.println("PARSE RUN TIME (in millisec): " + runTime);
    }
    catch (SAXException e)
    {
      System.out.println("ERROR in SAX parsing: " + e.getMessage());
      throw new ObjectIntegrityException("ERROR_DESERIALIZING_OBJECT: " + e.getMessage());
    }
    catch (Exception e)
    {
      throw new GeneralException("ERROR_MISC: " + e.getMessage());
    }
  }

   /**
     * Gets the content of the entire digital object as XML.  The object will
     * be returned exactly as it is stored in the repository.
     *
     * @throws StreamIOException If there was a failure in accessing the object
     *         for any IO reason during retrieval of the object from low-level storage.
     *         Extends ServerException.
     * @throws GeneralException If there was any misc exception that we want to
     *         catch and re-throw as a Fedora exception. Extends ServerException.
     */

    public InputStream GetObjectXML() throws StreamIOException, GeneralException
    {
      // LLSTORE: call to low level storage layer to retrieve object
      InputStream doIn = null;
      try
      {
        doIn = FileSystemLowlevelStorage.getPermanentStore().retrieve(PID);
      }
      catch(LowlevelStorageException e)
      {
        throw new StreamIOException("ERROR_LLSTORE_RETRIEVE: " + e.getMessage());
      }
      catch (Exception e)
      {
        throw new GeneralException("ERROR_MISC: " + e.getMessage());
      }
      return(doIn);
    }

    /**
     * Gets the content of the entire digital object as XML, with datastream
     * content included for those datastreams that are under the custodianship
     * of the repository.  The XML will contain XMLMetadata (inline XML) and
     * Managed Content datastreams (base64 encoded).  The content of
     * External Referenced datastreams will not be included inline, but the URL
     * for those datastreams will be returned.
     * <p></p>
     * The intent of this method is to return the digital object along with
     * its datastream content, except in cases where the datastream content is
     * not actually stored within the repository system.
     *
     * @throws StreamIOException If there was a failure in accessing the object
     *         for any IO reason during retrieval of the object from low-level storage.
     *         Extends ServerException.
     * @throws GeneralException If there was any misc exception that we want to
     *         catch and re-throw as a Fedora exception. Extends ServerException.
     */
    public InputStream ExportObject() throws StreamIOException, GeneralException
    {
      return(null);
    }

    /**
     * Gets the PID of the digital object.
     *
     * @throws GeneralException If there was any misc exception that we want to
     *         catch and re-throw as a Fedora exception. Extends ServerException.
     */
    public String GetObjectPID() throws GeneralException
    {
      if (debug) System.out.println("GetObjectPID = " + PID);
      return(PID);
    }

    /**
     * Gets the label of the digital object.
     *
     * @throws GeneralException If there was any misc exception that we want to
     *         catch and re-throw as a Fedora exception. Extends ServerException.
     */
    public String GetObjectLabel() throws GeneralException
    {
      if (debug) System.out.println("GetObjectLabel = " + doLabel);
      return(doLabel);
    }

    /**
     * Gets the state of the digital object.  The state indicates the status
     * of the digital object at any point in time.  Valid states are:
     * A=Active, W=Withdrawn, C=Marked for Deletion, D=Pending Deletion.
     * New states may be defined in the future.
     *
     * @throws GeneralException If there was any misc exception that we want to
     *         catch and re-throw as a Fedora exception. Extends ServerException.
     */
    public String GetObjectState() throws GeneralException
    {
      if (debug) System.out.println("GetObjectState = " + doState);
      return(doState);
    }

    /**
     * Gets a list of Datastream identifiers for all Datastreams in the digital
     * object.  Will take a state parameter to specify that only Datastreams
     * that are in a particular state should be listed (e.g., only active
     * Datastreams with a state value of "A").
     *
     * @throws GeneralException If there was any misc exception that we want to
     *         catch and re-throw as a Fedora exception. Extends ServerException.
     */
    public String[] ListDatastreamIDs(String state) throws GeneralException
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

    /**
     * Gets the Datastreams in the digital object.  This method will take a date/time
     * parameter to be able to retrieve only particular versions of Datastreams
     * (i.e., gets the version of the Datastreams as of the specified date/time).
     *
     * @param state The date-time stamp to get appropriate Datastream versions
     * @throws GeneralException If there was any misc exception that we want to
     *         catch and re-throw as a Fedora exception. Extends ServerException.
     */
    public Datastream[] GetDatastreams(Date versDateTime) throws GeneralException
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

    /**
     * Gets a particular Datastream in the digital object.  This method will
     * take a date/time parameter to be able to retrieve only a particular
     * version of the Datastream.
     * (i.e., gets the version of the Datastream as of the specified date/time).
     *
     * @param datastreamID The Datastream identifier
     * @param state The date-time stamp to get appropriate Datastream version
     * @throws GeneralException If there was any misc exception that we want to
     *         catch and re-throw as a Fedora exception. Extends ServerException.
     */
    public Datastream GetDatastream(String datastreamID, Date versDateTime)
      throws GeneralException
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

   /**
     * Gets the Disseminators in the digital object.  This method will take a date/time
     * parameter to be able to retrieve only particular versions of Disseminators
     * (i.e., gets the version of the Disseminators as of the specified date/time).
     *
     * @param state The date-time stamp to get appropriate Disseminator version
     * @throws GeneralException If there was any misc exception that we want to
     *         catch and re-throw as a Fedora exception. Extends ServerException.
     */
    public Disseminator[] GetDisseminators(Date versDateTime)
      throws GeneralException
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
          System.out.println("  state[" + i + "]=" + disseminators[i].dissState);
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

   /**
     * Gets a list of Disseminator identifiers for all Disseminators in the digital
     * object.  Will take a state parameter to specify that only Disseminators
     * that are in a particular state should be listed (e.g., only active
     * Disseminators with a state value of "A").
     *
     * @param state The state of the Disseminators to be listed.
     * @throws GeneralException If there was any misc exception that we want to
     *         catch and re-throw as a Fedora exception. Extends ServerException.
     */
    public String[] ListDisseminatorIDs(String state) throws GeneralException
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

   /**
     * Gets a particular Disseminator in the digital object.  This method will
     * take a date/time parameter to be able to retrieve only a particular
     * version of the Disseminator.
     * (i.e., gets the version of the Disseminator as of the specified date/time).
     *
     * @param disseminatorID The Disseminator identifier
     * @param state The date-time stamp to get appropriate Disseminator version
     * @throws GeneralException If there was any misc exception that we want to
     *         catch and re-throw as a Fedora exception. Extends ServerException.
     */
    public Disseminator GetDisseminator(String disseminatorID, Date versDateTime)
      throws GeneralException
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
     * Gets PIDs of Behavior Definitions to which object subscribes.  This is
     * done by looking at all the Disseminators for the object, and reflecting
     * on what Behavior Definitions objects the Disseminators refer to.
     *
     * @param versDateTime The date-time stamp to get appropriate version
     * @throws GeneralException If there was any misc exception that we want to
     *         catch and re-throw as a Fedora exception. Extends ServerException.
     */
    public String[] GetBehaviorDefs(Date versDateTime) throws GeneralException
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

  /**
     * Gets list of method definitions that are available on a particular
     * Disseminator. This is done by reflecting on the Disseminator
     * that subscribes to the Behavior Definition that is specified in the
     * method input parameter.  Then, by reflecting on that Disseminator,
     * the PID of the Behavior Mechanism object can be obtained.
     * Finally, method implementation information can be found in the
     * Behavior Mechanism object to which that Disseminator refers.
     *
     * @param bDefPID The PID of a Behavior Definition to which the object subscribes
     * @param versDateTime The date-time stamp to get appropriate version
     * @throws GeneralException If there was any misc exception that we want to
     *         catch and re-throw as a Fedora exception. Extends ServerException.
     */
    public MethodDef[] GetBMechMethods(String bDefPID, Date versDateTime)
      throws GeneralException, ServerException
    {
      // TODO! dateTime filter not implemented in this release!!

      // FIXIT! Put some code in to report default methods of the internal fedora
      // bootstrap mechanism which is not stored as digital object!  The bootstrap
      // mechanism enables a client to get disseminations of the actual contents
      // of a behavior mechanism object (e.g., get WSDL, get programmer guides).
      if (bDefPID.equalsIgnoreCase("uva-bdef-bootstrap:1"))
      {
        System.out.println("Suppressing report of methods for bootstrap mechanism!");
        return null;
      }

      if (debug)
      {
        System.out.println("Behavior Methods for BDEF: " + bDefPID);
      }
      DefinitiveBMechReader mechRead = new DefinitiveBMechReader((String)dissbDefTobMechTbl.get(bDefPID));
      MethodDef[] methods = mechRead.GetBehaviorMethods(null);
      return(methods);
    }

    /**
     * Gets list of method definitions that are available on a particular
     * Disseminator. Works like method GetBMechMethods, except the method
     * definitions are returned as XML in accordance with the WSDL schema.
     *
     * @param bDefPID The PID of a Behavior Definition to which the object subscribes
     * @param versDateTime The date-time stamp to get appropriate version
     * @throws GeneralException If there was any misc exception that we want to
     *         catch and re-throw as a Fedora exception. Extends ServerException.
     */
    public InputStream GetBMechMethodsWSDL(String bDefPID, Date versDateTime)
      throws GeneralException, ServerException
    {
      if (bDefPID.equalsIgnoreCase("uva-bdef-bootstrap:1"))
      {
        System.out.println("Suppressing report of WSDL for bootstrap mechanism!");
        return null;
      }
      // TODO! dateTime filter not implemented in this release!!
      DefinitiveBMechReader mechRead = new DefinitiveBMechReader((String)dissbDefTobMechTbl.get(bDefPID));
      InputStream instream = mechRead.GetBehaviorMethodsWSDL(null);
      return(instream);
    }

    public DSBindingMapAugmented[] GetDSBindingMaps(Date versDateTime)
          throws GeneralException
    {
      Collection disseminators = disseminatorTbl.values();
      DSBindingMapAugmented[] allBindingMaps = new DSBindingMapAugmented[disseminators.size()];

      Iterator it = disseminators.iterator();
      for (int i = 0; i < disseminators.size(); i++)
      {
        DSBindingMapAugmented map = new DSBindingMapAugmented();
        Disseminator diss = (Disseminator) it.next();
        map.dsBindMapID = diss.dsBindMap.dsBindMapID;
        map.dsBindMapLabel = diss.dsBindMap.dsBindMapLabel;
        map.dsBindMechanismPID = diss.dsBindMap.dsBindMechanismPID;

        // Denormalize the relationship between DSBinding and the Datastream
        // to which it refers.  Retrieve the associated Datastream object
        // via the datastreamID that's recorded in the DSBinding object.
        // Instantiate an augmented DSBinding object (DSBindingAugmented).

        DSBinding[] dsbindings = diss.dsBindMap.dsBindings;
        DSBindingAugmented[] augmentedBindings = new DSBindingAugmented[dsbindings.length];
        for (int j=0; j < dsbindings.length; j++)
        {
          DSBindingAugmented augmentedBinding = new DSBindingAugmented();
          augmentedBinding.bindKeyName = dsbindings[j].bindKeyName;
          augmentedBinding.bindLabel = dsbindings[j].bindLabel;
          augmentedBinding.datastreamID = dsbindings[j].datastreamID;
          augmentedBinding.seqNo = dsbindings[j].seqNo;
          // variables from the Datastream itself...
          Datastream ds = (Datastream)datastreamTbl.get(dsbindings[j].datastreamID);
          augmentedBinding.DSLabel = ds.DSLabel;
          augmentedBinding.DSMIME = ds.DSMIME;
          augmentedBinding.DSLocation = ds.DSLocation;
          augmentedBindings[j] = augmentedBinding;
        }
        map.dsBindingsAugmented = augmentedBindings;
        allBindingMaps[i] = map;
      }

      if (debug)
      {
          System.out.println("AUGMENTED BINDING MAPS:");
          for (int k = 0; k < allBindingMaps.length; k++)
          {
            System.out.println("  >>dsBindMapID[" + k + "]= " + allBindingMaps[k].dsBindMapID);
            System.out.println("  >>dsBindMapLabel[" + k + "]= " + allBindingMaps[k].dsBindMapLabel);
            System.out.println("  >>dsBindMechanismPID[" + k + "]= " + allBindingMaps[k].dsBindMechanismPID);

            for (int m = 0; m < allBindingMaps[k].dsBindingsAugmented.length; m++)
            {
              System.out.println("  >>>>>>>dsbinding-dsid[" + m + "]= "
                + allBindingMaps[k].dsBindingsAugmented[m].datastreamID);
              System.out.println("  >>>>>>>dsbinding-mime[" + m + "]= "
                + allBindingMaps[k].dsBindingsAugmented[m].DSMIME);
              System.out.println("  >>>>>>>dsbinding-location[" + m + "]= "
                + allBindingMaps[k].dsBindingsAugmented[m].DSLocation);
            }
          }
      }
      return allBindingMaps;
    }

    // private methods

    private File locateObject(String PID) {
      // FIXIT! insert code to locate object using data store layer when it's ready

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
      private boolean inMETSHDR = false;
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
      private boolean isBindingMap = false;
      private boolean inBindDivRoot = false;
      private boolean inBindDiv = false;
      private boolean inFilePtr = false;
      private boolean inBehaviorSec = false;
      private boolean inInterfaceDef = false;
      private boolean inMechanism = false;

      private String h_PID;
      private String h_doLabel;
      private String h_doState;
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

      private final Pattern ampRegexp = Pattern.compile("&");
      private final Pattern ltRegexp = Pattern.compile("<");
      private final Pattern gtRegexp = Pattern.compile(">");
      private final Pattern quotRegexp = Pattern.compile("\"");
      private final Pattern aposRegexp = Pattern.compile("'");

      public void startDocument() throws SAXException
      {
        //initialize the event handler variables

        h_vDatastream = new Vector();
        h_vDisseminator = new Vector();
        h_dsBindMapTbl = new Hashtable();
      }

      public void endDocument() throws SAXException
      {
          // Set the main class variables from the event handler variables

          // OBJECT PID
          PID = h_PID;
          doLabel = h_doLabel;
          doState = h_doState;

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
              dissbDefTobMechTbl.put(diss.bDefID, diss.bMechID);
              disseminatorTbl.put(diss.dissID, diss);
          }

          h_vDatastream = null;
          h_vDisseminator = null;
          h_dsBindMapTbl = null;
      }

      public void characters(char ch[], int start, int length) throws SAXException
      {
        if (isXMLDatastream && getAsStream)
        {
          // FIXIT! DO THE STRING REPLACEMENT THING FOR ENTITY REFS LIKE &amp; &lt; !!!!!
          // The parser will resolve these, and we want to write them out unresolved.

          String s = new String(ch, start, length);
          s = stringReplace(s, ampRegexp, "&amp;");
          s = stringReplace(s, ltRegexp, "&lt;");
          s = stringReplace(s, gtRegexp, "&gt;");
          s = stringReplace(s, quotRegexp, "&quot;");
          s = stringReplace(s, aposRegexp, "&apos;");
          h_xmlstream.write(s);

         // h_xmlstream.write(ch, start, length);
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

        // We want hold on to blocks of XML as datastream content.  These blocks
        // of XML must be valid XML.  The SAX parser will resolve internal entity
        // references such as &amp; &lt; &gt; &quot; &apos; that it encounters in
        // the XML block.  For the XML block to remain valid, we must convert these
        // back to their internal entity reference syntax.  There is no SAX config
        // variable that tells parser not to resolve these.  Thus, I have created
        // a string replacement function to convert them back.

        if (isXMLDatastream && getAsStream)
        {
          h_xmlstream.write("<" + qName);

          // Do the string replace thing on the attribute values
          int attrCount = attrs.getLength();
          for (int i = 0; i < attrCount; i++)
          {
            String s = attrs.getValue(i);
            s = stringReplace(s, ampRegexp, "&amp;");
            s = stringReplace(s, ltRegexp, "&lt;");
            s = stringReplace(s, gtRegexp, "&gt;");
            s = stringReplace(s, quotRegexp, "&quot;");
            s = stringReplace(s, aposRegexp, "&apos;");
            h_xmlstream.write(" " + attrs.getLocalName(i) + "=\"" + s + "\"");
          }
          h_xmlstream.write(">");
        }

        if (qName.equalsIgnoreCase("METS:mets"))
        {
          inMETS = true;
          h_PID = attrs.getValue("OBJID");
          h_doLabel = attrs.getValue("LABEL");
        }
        else if (qName.equalsIgnoreCase("METS:metsHdr"))
        {
          inMETSHDR = true;
          h_doState = attrs.getValue("RECORDSTATUS");
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
          h_datastream.DatastreamID = attrs.getValue("ID");
        }
        else if (qName.equalsIgnoreCase("METS:amdSec"))
        {
          inAMDSec = true;
          int attrCount = attrs.getLength();
          for (int i = 0; i < attrCount; i++)
          {
            // Get all EXCEPT the Fedora Audit Trail metadata
            String dsid;
            if (!((dsid = attrs.getValue("ID")).equalsIgnoreCase("FEDORA-AUDITTRAIL")))
            {
                isXMLDatastream = true;
                h_datastream = new DatastreamXMLMetadata();
                h_datastream.DatastreamID = dsid;
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
            h_datastream.DSControlGrp = 2;
            h_datastream.DSVersionID = attrs.getValue("ID");
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
          // NOTE: the validatioin module will enforce that the ID value for root
          // METS:fileGrp is "DATASTREAMS".
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
            try {
              if (attrs.getValue("SIZE") != null)
              {
                h_datastream.DSSize = Long.parseLong(attrs.getValue("SIZE"));
              }
            } catch (NumberFormatException nfe) {
                throw new SAXException("If specified, a datastream's SIZE "
                        + "attribute must be an xsd:long value.");
            }
            h_datastream.DSState = attrs.getValue("STATUS");

            String owner = attrs.getValue("OWNERID");
            if (owner != null && owner.equalsIgnoreCase("E"))
              h_datastream.DSControlGrp = 3;
            else if (owner != null && owner.equalsIgnoreCase("I"))
              h_datastream.DSControlGrp = 1;
            else
            {
              // technically, this is an invalid fedora mets object
              h_datastream.DSControlGrp = 0;
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

          // NOTE: the validation module will enforce the value of TYPE attribute
          // on METS:structMap to be "fedora:dsBindingMap".

          // NOTE: We ignore the structMap if it's not a Fedora datastream binding map

          if ((attrs.getValue("TYPE")).equalsIgnoreCase("fedora:dsBindingMap"))
          {
            isBindingMap = true;
            h_dsBindMap = new DSBindingMap();
            h_vDsBinding = new Vector();
            h_dsBindMap.dsBindMapID = attrs.getValue("ID");
            h_dsBindMap.state = attrs.getValue("STATUS");
          }
        }
        else if (qName.equalsIgnoreCase("METS:div") && isBindingMap && !inBindDivRoot && !inBindDiv)
        {
          inBindDivRoot = true;
          h_dsBindMap.dsBindMechanismPID = attrs.getValue("TYPE");
          h_dsBindMap.dsBindMapLabel = attrs.getValue("LABEL");
        }
        else if (qName.equalsIgnoreCase("METS:div") && isBindingMap && inBindDivRoot && !inBindDiv)
        {
          inBindDiv = true;
          h_dsBinding = new DSBinding();
          h_dsBinding.bindKeyName = attrs.getValue("TYPE");
          h_dsBinding.seqNo = attrs.getValue("ORDER");
          h_dsBinding.bindLabel = attrs.getValue("LABEL");
        }
        else if (qName.equalsIgnoreCase("METS:fptr") && isBindingMap && inBindDivRoot && inBindDiv && !inFilePtr)
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
          h_diss.dissState = attrs.getValue("STATUS");
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
        else if (qName.equalsIgnoreCase("METS:metsHdr") && inMETSHDR)
        {
          inMETSHDR = false;
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

          if (isBindingMap)
          {
            isBindingMap = false;
            h_dsBindMap.dsBindings = (DSBinding[]) h_vDsBinding.toArray(new DSBinding[0]);
            h_vDsBinding = null;

          // put the complete datastream binding map in hash table so it can
          // later be matched up with the disseminator it's associated with
            h_dsBindMapTbl.put(h_dsBindMap.dsBindMapID, h_dsBindMap);
            h_dsBindMap = null;
          }
        }
        else if (qName.equalsIgnoreCase("METS:div") && isBindingMap && inBindDivRoot && !inBindDiv && !inFilePtr)
        {
          inBindDivRoot = false;
        }
        else if (qName.equalsIgnoreCase("METS:div") && isBindingMap && inBindDivRoot && inBindDiv && !inFilePtr)
        {
          inBindDiv = false;
          h_vDsBinding.addElement(h_dsBinding);
          h_dsBinding = null;
        }
        else if (qName.equalsIgnoreCase("METS:fptr") && isBindingMap && inFilePtr)
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

      private String stringReplace(String input, Pattern p, String replacement)
      {
        // Take regexp pattern (Pattern) and create a matcher with the input string
        Matcher m = p.matcher(input);
        StringBuffer sb = new StringBuffer();

        // Loop through and create a new String with the replacements
        boolean result = m.find();
        while(result)
        {
           m.appendReplacement(sb, replacement);
           result = m.find();
        }
        // Add the last segment of input to the new String
        m.appendTail(sb);
        return(sb.toString());
      }
    }
}