package fedora.server.storage;

/**
 * <p>Title: FastDOReader.java</p>
 * <p>Description: Digital Object Reader. Reads objects in SQL database</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */

// java imports
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

// fedora imports
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.Disseminator;
import fedora.server.storage.types.Dissemination;
import fedora.server.storage.types.DSBindingMapAugmented;
import fedora.server.storage.types.BMechDSBindRule;
import fedora.server.storage.types.BMechDSBindSpec;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.errors.MethodParmNotFoundException;
import fedora.server.utilities.DateUtility;

public class FastDOReader implements DisseminatingDOReader
{

protected static ConnectionPool connectionPool = null;
protected static boolean debug = true;
protected DefinitiveDOReader doReader = null;
protected DefinitiveBMechReader bMechReader = null;
protected String PID = null;
protected String doLabel = null;
//FIXME!! need to decide where to locate the db.properties file
private static final String dbPropsFile = "db.properties";

  public FastDOReader()
  {
  }

  public FastDOReader(String objectPID) throws ObjectNotFoundException
  {
    //initialize database connection
    initDB();
    try
    {
      this.doLabel = locatePID(objectPID);
      this.PID = objectPID;
    } catch (ObjectNotFoundException onfe)
    {
     throw onfe;
    }
  }

  public String locatePID(String PID) throws ObjectNotFoundException
  {
    String queryResults = null;
    ResultSet rs = null;
    String  query =
        "SELECT "+
        "DigitalObject.DO_PID,"+
        "DigitalObject.DO_Label "+
        "FROM "+
        "DigitalObject "+
        "WHERE "+
        "DigitalObject.DO_PID=\'" + PID + "\';";
    if (debug) System.out.println("ObjectQuery: "+query);

    try
    {
      Connection connection = connectionPool.getConnection();
      Statement statement = connection.createStatement();
      rs = statement.executeQuery(query);
      while (rs.next())
      {
        queryResults = rs.getString(1);
        doLabel = rs.getString(2);
      }
      connectionPool.free(connection);
      connection.close();
      statement.close();
    } catch (SQLException sqle)
    {
      // Problem with the SQL database or query
      ObjectNotFoundException onfe = new ObjectNotFoundException("");
      onfe.initCause(sqle);
      throw onfe;
    }
    if (queryResults == null || queryResults.equalsIgnoreCase(""))
    {
      // Empty result means that the digital object could not be found in the
      // SQL database. This could be due to incorrectly specified parameters
      // for PID OR the method is not in the SQL database. If not in the SQL
      // database, attempt to find the object in the Definitive XML storage
      // area.
      try
      {
        // Try to find object in the Definitive XML storage area.
        // FIXME - until xml storage code is implemented, the call below
        // will throw a FileNotFound exception unless the object is one of the sample
        // objects in DefinitiveDOReader
        if (doReader == null) doReader = new DefinitiveDOReader(PID);
        queryResults = doReader.GetObjectPID();
        doLabel = doReader.GetObjectLabel();
        // FIXME - need to catch appropriate Exception thrown by
        // DefinitiveDOReader if the PID cannot be found. For now,
        // just catch any exception.
      } catch (Exception e)
      {
        // If object cannot be found in the Definitive XML storage area,
        // then the object request contains errors or the object does NOT
        // exist in the repository. In either case, this is a nonfatal
        // error that is passed back up the line.
        String message = "OBJECT NOT FOUND --\n PID: "+PID+"\n";
        throw new ObjectNotFoundException(message);
      }
    }
    return(doLabel);
  }

  /**
   * A method that resolves a dissemination request by attempting to
   * locate the necessary information in the SQL database. If not found
   * there, it will then attempt to locate the information in the
   * Definitive XML storage area.
   *
   * @param PID Persistent idenitfier for the digital object
   * @param bDefPID Persistent identifier for the Behavior Definition object
   * @param methodName Name of the method to be executed
   * @param versDateTime Versioning datetime stamp
   * @return MIMETypedStream containing the dissemination result
   * @throws ObjectNotFoundException
   */
  public Dissemination[] getDissemination(String PID, String bDefPID, String methodName,
                                 Date versDateTime)
      throws ObjectNotFoundException
  {
    Dissemination dissemination = null;
    Dissemination[] disseminations = null;
    Vector queryResults = new Vector();
    String query = "SELECT DISTINCT "+
        "DigitalObject.DO_PID,"+
        "BehaviorDefinition.BDEF_PID,"+
        "Method.METH_Name,"+
        "MechanismImpl.MECHImpl_Address_Location,"+
        "MechanismImpl.MECHImpl_Operation_Location,"+
        "MechanismImpl.MECHImpl_Protocol_Type,"+
        "DataStreamBinding.DSBinding_DS_Location, "+
        "DataStreamBindingSpec.DSBindingSpec_Name "+
        " FROM "+
        "DigitalObject,"+
        "BehaviorDefinition,"+
        "BehaviorMechanism,"+
        "DataStreamBinding,"+
        "Disseminator,"+
        "DigitalObjectDissAssoc,"+
        "MechanismImpl,"+
        "Method,"+
        "DataStreamBindingSpec "+
 	" WHERE "+
        "DigitalObject.DO_DBID=DigitalObjectDissAssoc.DO_DBID AND "+
	"DigitalObjectDissAssoc.DISS_DBID=Disseminator.DISS_DBID AND " +
	"Disseminator.BDEF_DBID = BehaviorDefinition.BDEF_DBID AND " +
	"Disseminator.BMECH_DBID = BehaviorMechanism.BMECH_DBID AND " +
	"DataStreamBinding.DO_DBID = DigitalObject.DO_DBID AND " +
	"BehaviorMechanism.BMECH_DBID = MechanismImpl.BMECH_DBID AND " +
	"MechanismImpl.DSBindingKey_DBID = DataStreamBinding.DSBindingKey_DBID AND " +
        "DataStreamBindingSpec.DSBindingKey_DBID = MechanismImpl.DSBindingKey_DBID AND "+
	"MechanismImpl.METH_DBID = Method.METH_DBID AND " +
	"DigitalObject.DO_PID='" + PID + "' AND " +
	" BehaviorDefinition.BDEF_PID='" + bDefPID + "' AND " +
	" Method.METH_Name='"  + methodName + "' "+
        " ORDER BY DataStreamBindingSpec.DSBindingSpec_Name";
    if(debug) System.out.println("DissemQuery="+query+"\n");
    try
    {
      // execute database query and retrieve results
      Connection connection = connectionPool.getConnection();
      if(debug) System.out.println("connectionPool = "+connectionPool);
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(query);
      ResultSetMetaData rsMeta = rs.getMetaData();
      String[] results = null;
      int cols = rsMeta.getColumnCount();
      int rowCount = 0;
      // Note: When more than one datastream matches the DSBindingKey
      // or there are multiple DSBindingKeys associated with the method
      // in the dissemination query, multiple rows are returned.
      while (rs.next())
      {
        results = new String[cols];
        dissemination = new Dissemination();
        for (int i=1; i<=cols; i++)
        {
          results[i-1] = rs.getString(i);
        }
        dissemination.AddressLocation = results[3];
        dissemination.OperationLocation = results[4];
        dissemination.ProtocolType = results[5];
        dissemination.DSLocation = results[6];
        dissemination.DSBindKey = results[7];
        try
        {
          dissemination.methodParms = this.GetBMechMethodParm(results[1], results[2], versDateTime);
        } catch (MethodParmNotFoundException mpnfe)
        {
          dissemination.methodParms = null;
        }
        // Add each row of returned data
        queryResults.addElement(dissemination);
        rowCount++;
      }
      disseminations = new Dissemination[rowCount];
      rowCount = 0;
      Enumeration e = queryResults.elements();
      while (e.hasMoreElements())
      {
        disseminations[rowCount] = (Dissemination)e.nextElement();
        rowCount++;
      }
      connectionPool.free(connection);
      connection.close();
      statement.close();
    } catch (SQLException sqle)
    {
      // Problem with the SQL database or query
      ObjectNotFoundException onfe = new ObjectNotFoundException("");
      onfe.initCause(sqle);
      throw onfe;

    }

    if (queryResults.isEmpty())
    {
      // Empty result means that object could not be found in the
      // SQL database. This could be due to incorrectly specified
      // parameters for PID, bDefPID, methodName, or asOfDate OR the
      // object is not in the SQL database. If not in the SQL
      // database, attempt to find the object in the Definitive XML
      // storage.
      try
      {
        // Try to find object in the Definitive storage.
        if (doReader == null) doReader = new DefinitiveDOReader(PID);
        // FIXME - code to perform disseminations directly from the
        // XML objects NOT implemented in this release.
        return disseminations;
        // FIXME - need to catch appropriate Exception thrown by
        // DefinitiveDOReader if the PID cannot be found. For now,
        // just catch any Exception.
      } catch (Exception e)
      {
        // If object cannot be found in the Definitive storage, then
        // the dissemination request contains errors or the object does
        // NOT exist in the repository. In either case, this is a
        // nonfatal error that is passed back up the line.
        String message = "OBJECT NOT FOUND --\nPID: "+PID+"\n bDefPID: "+bDefPID+
                         "\n methodName: "+methodName+"\n asOfDate: "+
                         DateUtility.convertDateToString(versDateTime)+"\n";
        throw new ObjectNotFoundException(message);
      }
    }
    return(disseminations);
  }

  /**
   * A method that gets a list methods and any associated method parameters
   * by attempting to read from the SQL database. If the information is not
   * found there, it will attempt to find the information in the Definitive
   * XML storage area.
   *
   * @param bDefPID Persistent identifier for the Behavior Mechanism object
   * @param methodName Name of the method
   * @param versDateTime Versioning datetime stamp
   * @return Vector containing rows from the SQL query
   * @throws MethodParmNotFoundException
   */
  /*
  public MethodParmDef[] getMethodParms(String bDefPID, String methodName,
                               Date versDateTime)
      throws MethodParmNotFoundException
  {
    MethodParmDef methodParm = null;
    MethodParmDef[] methodParms = null;
    Vector queryResults = new Vector();

    // Note that the query retrieves the list of available methods
    // based on Behavior Mechanism object and NOT the Behavior
    // Definition object. This is done to insure that only methods
    // that have been implemented in the mechanism are returned.
    // This distinction is only important when versioning is enabled
    // in a later release. When versioning is enabled, it is possible
    // that a given Behavior Definition may have methods that have not
    // yet been implemented by all of its associated Behavior Mechanisms.
    // In such a case, only those methods implemented in the mechanism
    // will be returned.
    String query = "SELECT "+
            "PARM_Name,"+
            "PARM_Default_Value,"+
            "PARM_Required_Flag,"+
            "PARM_Label "+
            " FROM "+
            "BehaviorDefinition,"+
            "BehaviorMechanism,"+
            "MechanismImpl,"+
            "Method,"+
            "Parameter "+
            " WHERE "+
            "BehaviorMechanism.BDEF_DBID=Parameter.BDEF_DBID AND "+
            "Method.BDEF_DBID=Parameter.BDEF_DBID AND "+
            "Method.METH_DBID=Parameter.METH_DBID AND "+
            "BehaviorMechanism.BDEF_DBID=Method.BDEF_DBID AND "+
            "MechanismImpl.METH_DBID=Method.METH_DBID AND " +
            "BehaviorMechanism.BDEF_DBID=BehaviorDefinition.BDEF_DBID AND "+
            "BehaviorDefinition.BDEF_PID='" + bDefPID + "' AND "+
            "Method.METH_Name='"  + methodName + "' ";

    if(debug) System.out.println("MethodParmQuery="+query+"\n");
    try
    {
      Connection connection = connectionPool.getConnection();
      if(debug) System.out.println("connectionPool = "+connectionPool);
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(query);
      ResultSetMetaData rsMeta = rs.getMetaData();
      int rowCount = 0;
      String[] results =  null;
      int cols = rsMeta.getColumnCount();
      // Note: a row is returned for each method parameter
      while (rs.next())
      {
        results = new String[cols];
        for (int i=1; i<=cols; i++)
        {
          results[i-1] = rs.getString(i);
        }
        methodParm.parmName = results[0];
        methodParm.parmDefaultValue = results[1];
        Boolean B1 = new Boolean(results[2]);
        methodParm.parmRequired = B1.booleanValue();
        methodParm.parmLabel = results[3];
        // Add each row of results to vector
        queryResults.addElement(methodParm);
        methodParms = new MethodParmDef[rowCount];
        Enumeration e = queryResults.elements();
        while (e.hasMoreElements())
        {
          methodParms[rowCount] = (MethodParmDef)e.nextElement();
          rowCount++;
        }
      }
      connectionPool.free(connection);
      connection.close();
      statement.close();
    } catch (SQLException sqle)
    {
      // Problem with the SQL database or query
      MethodParmNotFoundException mpnfe = new MethodParmNotFoundException("");
      mpnfe.initCause(sqle);
      throw mpnfe;
    }

    if (queryResults.isEmpty())
    {
      // Empty result means that method(Behavior Mechanism object) could
      // not be found in the SQL database. This could be due to incorrectly
      // specified parameters for bDefPID and/or method OR the method is not
      // not in the SQL database. If not in the SQL database, attempt
      // to find the object in the Definitive storage.
      try
      {
        // Try to find method parameters in the Definitive storage.
        // FIXME - until xml storage code is implemented, the call below
        // will throw a FileNotFound exception unless the object is one of the sample
        // objects in DefinitiveBMechReader
        if (bMechReader == null) bMechReader = new DefinitiveBMechReader(bDefPID);
        return(bMechReader
        // FIXME - need to catch appropriate Exception thrown by
        // DefinitiveDOReader if the PID cannot be found. For now,
        // just catch any exception.
      } catch (Exception e)
      {
        // If method cannot be found in the Definitive storage, then
        // the method parameter request contains errors or the method
        // does NOT exist in the repository. In either case, this is a
        // nonfatal error that is passed back up the line.
        String message = "METHOD PARM NOT FOUND --\n bDefPID: "+bDefPID+
                         "\n methodName: "+methodName+"\n asOfDate: "+
                         DateUtility.convertDateToString(versDateTime)+"\n";
        throw new MethodParmNotFoundException(message);
      }
    } else
    {
      // Request for method parameters successful; return results.
      return queryResults;
    }
  }
*/
  /**
   * A method that retrieves information from the SQL database about a digital
   * object that includes: Disseminators, Behavior Definitions, Behavior
   * Mechanisms, and their methods. If the object is not found in the SQL
   * database, the method attempts to find the object in the Definitive XML
   * storage area.
   *
   * @param PID Persistent identifier for the digital object
   * @return Vector containing rows from SQL query
   * @throws ObjectNotFoundException
   */
  public Vector getObject(String PID, Date versDateTime) throws ObjectNotFoundException
  {
    Vector queryResults = new Vector();
    String  query =
        "SELECT DISTINCT "+
        "DigitalObject.DO_PID,"+
        "Disseminator.DISS_ID,"+
        "BehaviorDefinition.BDEF_PID,"+
        "BehaviorMechanism.BMECH_PID,"+
        "Method.METH_Name "+
        "FROM "+
        "BehaviorDefinition,"+
        "Disseminator,"+
        "Method,"+
        "DigitalObject,"+
        "DigitalObjectDissAssoc,"+
        "BehaviorMechanism,"+
        "MechanismImpl "+
        "WHERE "+
        "DigitalObject.DO_DBID = DigitalObjectDissAssoc.DO_DBID AND "+
        "DigitalObjectDissAssoc.DISS_DBID = Disseminator.DISS_DBID AND "+
        "BehaviorDefinition.BDEF_DBID = Disseminator.BDEF_DBID AND "+
        "BehaviorMechanism.BMECH_DBID = Disseminator.BMECH_DBID AND "+
        "BehaviorMechanism.BMECH_DBID = MechanismImpl.BMECH_DBID AND "+
        "BehaviorDefinition.BDEF_DBID = MechanismImpl.BDEF_DBID AND "+
        "Method.METH_DBID = MechanismImpl.METH_DBID AND "+
        "DigitalObject.DO_PID=\'" + PID + "\';";

    if (debug) System.out.println("ObjectQuery: "+query);
    ResultSet rs = null;
    String[] results = null;
    try
    {
      Connection connection = connectionPool.getConnection();
      Statement statement = connection.createStatement();
      rs = statement.executeQuery(query);
      ResultSetMetaData rsMeta = rs.getMetaData();
      int cols = rsMeta.getColumnCount();
      while (rs.next())
      {
        results = new String[cols];
        for (int i=1; i<=cols; i++)
        {
          results[i-1] = rs.getString(i);
        }
        queryResults.add(results);
      }
      connectionPool.free(connection);
      connection.close();
      statement.close();
    } catch (SQLException sqle)
    {
      // Problem with the SQL database or query
      ObjectNotFoundException onfe = new ObjectNotFoundException("");
      onfe.initCause(sqle);
      throw onfe;
    }
    if (queryResults.isEmpty())
    {
      // Empty result means that the digital object could not be found in the
      // SQL database. This could be due to incorrectly specified parameters
      // for PID OR the method is not in the SQL database. If not in the SQL
      // database, attempt to find the object in the Definitive XML storage
      // area.
      try
      {
        // Try to find object in the Definitive XML storage area.
        // FIXME - until xml storage code is implemented, the call below
        // will throw a FileNotFound exception unless the object is one of the sample
        // objects in DefinitiveBMechReader
        if (doReader == null) doReader = new DefinitiveDOReader(PID);
        // FIXME - need to add code to get object info from XML objects
        return queryResults;
        // FIXME - need to catch appropriate Exception thrown by
        // DefinitiveDOReader if the PID cannot be found. For now,
        // just catch any exception.
      } catch (Exception e)
      {
        // If object cannot be found in the Definitive XML storage area,
        // then the object request contains errors or the object does NOT
        // exist in the repository. In either case, this is a nonfatal
        // error that is passed back up the line.
        String message = "OBJECT NOT FOUND --\n PID: "+PID+"\n asOfDate: "+
                         DateUtility.convertDateToString(versDateTime)+"\n";
        throw new ObjectNotFoundException(message);
      }
    } else
    {
      // Request for object successful; return results.
      return queryResults;
    }
  }

  public static void initDB() throws ObjectNotFoundException
  {
    try
    {
      // read database properties file and init connection pool
      FileInputStream fis = new FileInputStream(dbPropsFile);
      Properties dbProps = new Properties();
      dbProps.load(fis);
      String driver = dbProps.getProperty("drivers");
      String username = dbProps.getProperty("username");
      String password = dbProps.getProperty("password");
      String url = dbProps.getProperty("url");
      Integer i1 = new Integer(dbProps.getProperty("initConnections"));
      int initConnections = i1.intValue();
      Integer i2 = new Integer(dbProps.getProperty("maxConnections"));
      int maxConnections = i2.intValue();
      // FIXME above section of code to be replaced with the following
      // section when Server.java is functional
      /*
      Server serverInstance =
      Server.getInstance(System.getProperty(Server.HOME_PROPERTY));
      String driver = serverInstance.getDatastoreConfig("drivers");
      String username = serverInstance.getDatastoreConfig("username");
      String password = serverInstance.getDatastoreConfig("password");
      String url = serverInstance.getDatastoreConfig("url");
      Integer i1 = new Integer(serverInstance.getDatastoreConfig("minConnections"););
      int initConnections = i1.intValue();
      Integer i2 = new Integer(serverInstance.getDatastoreConfig("maxConnections"););
      int maxConnections = i2.intValue();
      */
      if(debug) System.out.println("\nurl = "+url);

      // initialize connection pool
      //ConnectionPool connectionPool = null;
      connectionPool = new ConnectionPool(driver, url, username, password,
          initConnections, maxConnections, true);
    } catch (SQLException sqle)
    {
      // Problem with connection pool and/or database
      System.out.println("Unable to create connection pool: "+sqle);
      ConnectionPool connectionPool = null;
      connectionPool = null;
      ObjectNotFoundException onfe = new ObjectNotFoundException("");
      onfe.initCause(sqle);
      throw onfe;
    } catch (FileNotFoundException fnfe)
    {
      System.out.println("Unable to read the properties file: " +
          dbPropsFile);
      ObjectNotFoundException onfe = new ObjectNotFoundException("");
      onfe.initCause(fnfe);
      throw onfe;
    } catch (IOException ioe)
    {
      System.out.println(ioe);
      ObjectNotFoundException onfe = new ObjectNotFoundException("");
      onfe.initCause(ioe);
      throw onfe;
    }

  }

  // Methods required by DOReader
  public String GetObjectXML()
  {
    if (doReader == null) doReader = new DefinitiveDOReader(PID);
    return(doReader.GetObjectXML());
  }

  public String ExportObject()
  {
    if (doReader == null) doReader = new DefinitiveDOReader(PID);
    return(doReader.ExportObject());
  }

  public String GetObjectPID()
  {
    return this.PID;
  }

  public String GetObjectLabel()
  {
    return doLabel;
  }

  public String[] ListDatastreamIDs(String state)
  {
    Vector queryResults = new Vector();
    String[] datastreamIDs = null;
    Datastream datastream = null;
    String  query =
        "SELECT DISTINCT "+
        "DataStreamBinding.DSBinding_DS_ID "+
        "FROM "+
        "DigitalObject,"+
        "DataStreamBinding "+
        "WHERE "+
        "DigitalObject.DO_DBID = DataStreamBinding.DO_DBID AND "+
        "DigitalObject.DO_PID=\'" + PID + "\';";

    if (debug) System.out.println("ObjectQuery: "+query);
    ResultSet rs = null;
    String[] results = null;
    try
    {
      Connection connection = connectionPool.getConnection();
      Statement statement = connection.createStatement();
      rs = statement.executeQuery(query);
      ResultSetMetaData rsMeta = rs.getMetaData();
      int cols = rsMeta.getColumnCount();
      int rowCount = 0;
      while (rs.next())
      {
        results = new String[cols];
        for (int i=1; i<=cols; i++)
        {
          results[i-1] = rs.getString(i);
        }
        datastream = new Datastream();
        datastream.DatastreamID = results[0];
        queryResults.addElement(datastream);
        rowCount++;
      }
      Enumeration e = queryResults.elements();
      datastreamIDs = new String[rowCount];
      rowCount = 0;
      while (e.hasMoreElements())
      {
        datastreamIDs[rowCount] = (String)e.nextElement();
        rowCount++;
      }
      connectionPool.free(connection);
      connection.close();
      statement.close();
    } catch (SQLException sqle)
    {
      // Problem with the SQL database or query
      ObjectNotFoundException onfe = new ObjectNotFoundException("");
      onfe.initCause(sqle);
      //throw onfe;
    }
    if (datastreamIDs == null)
    {
      // Empty result means that the digital object could not be found in the
      // SQL database. This could be due to incorrectly specified parameters
      // for PID OR the method is not in the SQL database. If not in the SQL
      // database, attempt to find the object in the Definitive XML storage
      // area.
      try
      {
        // Try to find object in the Definitive XML storage area.
        // FIXME - until xml storage code is implemented, the call below
        // will throw a FileNotFound exception unless the object is one of the sample
        // objects in DefinitiveBMechReader
        if (doReader == null) doReader = new DefinitiveDOReader(PID);
        datastreamIDs = doReader.ListDatastreamIDs("");
        // FIXME - need to catch appropriate Exception thrown by
        // DefinitiveDOReader if the PID cannot be found. For now,
        // just catch any exception.
      } catch (Exception e)
      {
        // If object cannot be found in the Definitive XML storage area,
        // then the object request contains errors or the object does NOT
        // exist in the repository. In either case, this is a nonfatal
        // error that is passed back up the line.
        String message = "OBJECT NOT FOUND --\n PID: "+PID+"\n";
        //throw new ObjectNotFoundException(message);
      }
    }
    return(datastreamIDs);
  }

  public Datastream[] GetDatastreams(Date versDateTime)
  {
    Vector queryResults = new Vector();
    Datastream[] datastreams = null;
    Datastream datastream = null;
    String  query =
        "SELECT DISTINCT "+
        "DataStreamBinding.DSBinding_DS_Label,"+
        "DataStreamBinding.DSBinding_DS_MIME,"+
        "DataStreamBinding.DSBinding_DS_Location "+
        "FROM "+
        "DigitalObject,"+
        "DataStreamBinding "+
        "WHERE "+
        "DigitalObject.DO_DBID = DataStreamBinding.DO_DBID AND "+
        "DigitalObject.DO_PID=\'" + PID + "\';";

    if (debug) System.out.println("ObjectQuery: "+query);
    ResultSet rs = null;
    String[] results = null;
    try
    {
      Connection connection = connectionPool.getConnection();
      Statement statement = connection.createStatement();
      rs = statement.executeQuery(query);
      ResultSetMetaData rsMeta = rs.getMetaData();
      int cols = rsMeta.getColumnCount();
      int rowCount = 0;
      while (rs.next())
      {
        results = new String[cols];
        for (int i=1; i<=cols; i++)
        {
          results[i-1] = rs.getString(i);
        }
        datastream = new Datastream();
        datastream.DSLabel = results[0];
        datastream.DSMIME = results[1];
        datastream.DSLocation = results[2];
        queryResults.addElement(datastream);
        rowCount++;
      }
      Enumeration e = queryResults.elements();
      datastreams = new Datastream[rowCount];
      rowCount = 0;
      while (e.hasMoreElements())
      {
        datastreams[rowCount] = (Datastream)e.nextElement();
        rowCount++;
      }
      connectionPool.free(connection);
      connection.close();
      statement.close();
    } catch (SQLException sqle)
    {
      // Problem with the SQL database or query
      ObjectNotFoundException onfe = new ObjectNotFoundException("");
      onfe.initCause(sqle);
      //throw onfe;
    }
    if (datastreams == null)
    {
      // Empty result means that the digital object could not be found in the
      // SQL database. This could be due to incorrectly specified parameters
      // for PID OR the method is not in the SQL database. If not in the SQL
      // database, attempt to find the object in the Definitive XML storage
      // area.
      try
      {
        // Try to find object in the Definitive XML storage area.
        // FIXME - until xml storage code is implemented, the call below
        // will throw a FileNotFound exception unless the object is one of the sample
        // objects in DefinitiveBMechReader
        if (doReader == null) doReader = new DefinitiveDOReader(PID);
        datastreams = doReader.GetDatastreams(versDateTime);
        // FIXME - need to catch appropriate Exception thrown by
        // DefinitiveDOReader if the PID cannot be found. For now,
        // just catch any exception.
      } catch (Exception e)
      {
        // If object cannot be found in the Definitive XML storage area,
        // then the object request contains errors or the object does NOT
        // exist in the repository. In either case, this is a nonfatal
        // error that is passed back up the line.
        String message = "OBJECT NOT FOUND --\n PID: "+PID+"\n asOfDate: "+
                         DateUtility.convertDateToString(versDateTime)+"\n";
        //throw new ObjectNotFoundException(message);
      }
    }
    return(datastreams);
  }

  public Datastream GetDatastream(String datastreamID, Date versDateTime)
  {
    Vector queryResults = new Vector();
    Datastream[] datastreams = null;
    Datastream datastream = null;
    String  query =
        "SELECT DISTINCT "+
        "DataStreamBinding.DSBinding_DS_Label,"+
        "DataStreamBinding.DSBinding_DS_MIME,"+
        "DataStreamBinding.DSBinding_DS_Location "+
        "FROM "+
        "DigitalObject,"+
        "DataStreamBinding "+
        "WHERE "+
        "DigitalObject.DO_DBID = DataStreamBinding.DO_DBID AND "+
        "DataStreamBinding.DSBinding_DS_ID=\'" + datastreamID +"\' AND "+
        "DigitalObject.DO_PID=\'" + PID + "\';";

    if (debug) System.out.println("ObjectQuery: "+query);
    ResultSet rs = null;
    String[] results = null;
    try
    {
      Connection connection = connectionPool.getConnection();
      Statement statement = connection.createStatement();
      rs = statement.executeQuery(query);
      ResultSetMetaData rsMeta = rs.getMetaData();
      int cols = rsMeta.getColumnCount();
      int rowCount = 0;
      while (rs.next())
      {
        results = new String[cols];
        for (int i=1; i<=cols; i++)
        {
          results[i-1] = rs.getString(i);
        }
        datastream = new Datastream();
        datastream.DSLabel = results[0];
        datastream.DSMIME = results[1];
        datastream.DSLocation = results[2];
        queryResults.addElement(datastream);
        rowCount++;
      }
      Enumeration e = queryResults.elements();
      datastreams = new Datastream[rowCount];
      if (rowCount > 1 )
      {
        String message = "Duplicate Entries for PID: "+PID+" DatastreamID: "+datastreamID;
        //throw new ObjectNotFoundException(message);
      }
      while (e.hasMoreElements())
      {
        datastream = (Datastream)e.nextElement();
      }
      connectionPool.free(connection);
      connection.close();
      statement.close();
    } catch (SQLException sqle)
    {
      // Problem with the SQL database or query
      ObjectNotFoundException onfe = new ObjectNotFoundException("");
      onfe.initCause(sqle);
      //throw onfe;
    }
    if (datastream == null)
    {
      // Empty result means that the digital object could not be found in the
      // SQL database. This could be due to incorrectly specified parameters
      // for PID OR the method is not in the SQL database. If not in the SQL
      // database, attempt to find the object in the Definitive XML storage
      // area.
      try
      {
        // Try to find object in the Definitive XML storage area.
        // FIXME - until xml storage code is implemented, the call below
        // will throw a FileNotFound exception unless the object is one of the sample
        // objects in DefinitiveBMechReader
        if (doReader == null) doReader = new DefinitiveDOReader(PID);
        datastream = doReader.GetDatastream(datastreamID, versDateTime);
        // FIXME - need to catch appropriate Exception thrown by
        // DefinitiveDOReader if the PID cannot be found. For now,
        // just catch any exception.
      } catch (Exception e)
      {
        // If object cannot be found in the Definitive XML storage area,
        // then the object request contains errors or the object does NOT
        // exist in the repository. In either case, this is a nonfatal
        // error that is passed back up the line.
        String message = "OBJECT NOT FOUND --\n PID: "+PID+"\n asOfDate: "+
                         DateUtility.convertDateToString(versDateTime)+"\n";
        //throw new ObjectNotFoundException(message);
      }
    }
    return(datastream);
  }

  public Disseminator[] GetDisseminators(Date versDateTime)
  {
    Vector queryResults = new Vector();
    Disseminator[] disseminators = null;
    Disseminator disseminator = null;
    String  query =
        "SELECT DISTINCT "+
        "Disseminator.DISS_ID,"+
        "BehaviorDefinition.BDEF_PID,"+
        "BehaviorMechanism.BMECH_PID,"+
        "DataStreamBindingMap.DSBindingMap_ID "+
        "FROM "+
        "BehaviorDefinition,"+
        "Disseminator,"+
        "DataStreamBindingMap,"+
        "DigitalObject,"+
        "DigitalObjectDissAssoc,"+
        "BehaviorMechanism "+
        "WHERE "+
        "DigitalObject.DO_DBID = DigitalObjectDissAssoc.DO_DBID AND "+
        "DigitalObjectDissAssoc.DISS_DBID = Disseminator.DISS_DBID AND "+
        "BehaviorDefinition.BDEF_DBID = Disseminator.BDEF_DBID AND "+
        "BehaviorMechanism.BMECH_DBID = Disseminator.BMECH_DBID AND "+
        "DataStreamBindingMap.BMECH_DBID=BehaviorMechanism.BMECH_DBID AND "+
        "DigitalObject.DO_PID=\'" + PID + "\';";

    if (debug) System.out.println("ObjectQuery: "+query);
    ResultSet rs = null;
    String[] results = null;
    try
    {
      Connection connection = connectionPool.getConnection();
      Statement statement = connection.createStatement();
      rs = statement.executeQuery(query);
      ResultSetMetaData rsMeta = rs.getMetaData();
      int cols = rsMeta.getColumnCount();
      int rowCount = 0;
      while (rs.next())
      {
        results = new String[cols];
        for (int i=1; i<=cols; i++)
        {
          results[i-1] = rs.getString(i);
        }
        disseminator = new Disseminator();
        disseminator.dissID = results[0];
        disseminator.bDefID = results[1];
        disseminator.bMechID = results[2];
        disseminator.dsBindMapID = results[3];
        queryResults.addElement(disseminator);
        rowCount++;
      }
      disseminators = new Disseminator[rowCount];
      Enumeration e = queryResults.elements();
      rowCount = 0;
      while (e.hasMoreElements())
      {
        disseminators[rowCount] = (Disseminator)e.nextElement();
        rowCount++;
      }
      connectionPool.free(connection);
      connection.close();
      statement.close();
    } catch (SQLException sqle)
    {
      // Problem with the SQL database or query
      ObjectNotFoundException onfe = new ObjectNotFoundException("");
      onfe.initCause(sqle);
      //throw onfe;
    }
    if (disseminators == null)
    {
      // Empty result means that the digital object could not be found in the
      // SQL database. This could be due to incorrectly specified parameters
      // for PID OR the method is not in the SQL database. If not in the SQL
      // database, attempt to find the object in the Definitive XML storage
      // area.
      try
      {
        // Try to find object in the Definitive XML storage area.
        // FIXME - until xml storage code is implemented, the call below
        // will throw a FileNotFound exception unless the object is one of the sample
        // objects in DefinitiveBMechReader
        if (doReader == null) doReader = new DefinitiveDOReader(PID);
        disseminators = doReader.GetDisseminators(versDateTime);
        // FIXME - need to catch appropriate Exception thrown by
        // DefinitiveDOReader if the PID cannot be found. For now,
        // just catch any exception.
      } catch (Exception e)
      {
        // If object cannot be found in the Definitive XML storage area,
        // then the object request contains errors or the object does NOT
        // exist in the repository. In either case, this is a nonfatal
        // error that is passed back up the line.
        String message = "OBJECT NOT FOUND --\n PID: "+PID+"\n asOfDate: "+
                         DateUtility.convertDateToString(versDateTime)+"\n";
        //throw new ObjectNotFoundException(message);
      }
    }
    return(disseminators);
  }

  public String[] ListDisseminatorIDs(String state)
  {
    Vector queryResults = new Vector();
    Disseminator disseminator = null;
    String[] disseminatorIDs = null;
    String  query =
        "SELECT DISTINCT "+
        "Disseminator.DISS_ID "+
        "FROM "+
        "Disseminator,"+
        "DigitalObject,"+
        "DigitalObjectDissAssoc "+
        "WHERE "+
        "DigitalObject.DO_DBID = DigitalObjectDissAssoc.DO_DBID AND "+
        "DigitalObjectDissAssoc.DISS_DBID = Disseminator.DISS_DBID AND "+
        "DigitalObject.DO_PID=\'" + PID + "\';";

    if (debug) System.out.println("ObjectQuery: "+query);
    ResultSet rs = null;
    String[] results = null;
    try
    {
      Connection connection = connectionPool.getConnection();
      Statement statement = connection.createStatement();
      rs = statement.executeQuery(query);
      ResultSetMetaData rsMeta = rs.getMetaData();
      int cols = rsMeta.getColumnCount();
      int rowCount = 0;
      while (rs.next())
      {
        results = new String[cols];
        for (int i=1; i<=cols; i++)
        {
          results[i-1] = rs.getString(i);
        }
        disseminator = new Disseminator();
        disseminator.dissID = results[0];
        queryResults.addElement(disseminator);
        rowCount++;
      }
      disseminatorIDs = new String[rowCount];
      Enumeration e = queryResults.elements();
      rowCount = 0;
      while (e.hasMoreElements())
      {
        disseminator = (Disseminator)e.nextElement();
        disseminatorIDs[rowCount] = disseminator.dissID;
        rowCount++;
      }
      connectionPool.free(connection);
      connection.close();
      statement.close();
    } catch (SQLException sqle)
    {
      // Problem with the SQL database or query
      ObjectNotFoundException onfe = new ObjectNotFoundException("");
      onfe.initCause(sqle);
      //throw onfe;
    }
    if (disseminatorIDs == null)
    {
      // Empty result means that the digital object could not be found in the
      // SQL database. This could be due to incorrectly specified parameters
      // for PID OR the method is not in the SQL database. If not in the SQL
      // database, attempt to find the object in the Definitive XML storage
      // area.
      try
      {
        // Try to find object in the Definitive XML storage area.
        // FIXME - until xml storage code is implemented, the call below
        // will throw a FileNotFound exception unless the object is one of the sample
        // objects in DefinitiveBMechReader
        if (doReader == null) doReader = new DefinitiveDOReader(PID);
        disseminatorIDs = doReader.ListDisseminatorIDs("A");
        // FIXME - need to catch appropriate Exception thrown by
        // DefinitiveDOReader if the PID cannot be found. For now,
        // just catch any exception.
      } catch (Exception e)
      {
        // If object cannot be found in the Definitive XML storage area,
        // then the object request contains errors or the object does NOT
        // exist in the repository. In either case, this is a nonfatal
        // error that is passed back up the line.
        String message = "OBJECT NOT FOUND --\n PID: "+PID+"\n";
        //throw new ObjectNotFoundException(message);
      }
    }
    return(disseminatorIDs);
  }

  public Disseminator GetDisseminator(String disseminatorID, Date versDateTime)
  {
    Disseminator disseminator = null;
    String  query =
        "SELECT DISTINCT "+
        "Disseminator.DISS_ID,"+
        "BehaviorDefinition.BDEF_PID,"+
        "BehaviorMechanism.BMECH_PID,"+
        "DataStreamBindingMap.DSBindingMap_ID "+
        "FROM "+
        "BehaviorDefinition,"+
        "Disseminator,"+
        "DataStreamBindingMap,"+
        "DigitalObject,"+
        "DigitalObjectDissAssoc,"+
        "BehaviorMechanism "+
        "WHERE "+
        "DigitalObject.DO_DBID = DigitalObjectDissAssoc.DO_DBID AND "+
        "DigitalObjectDissAssoc.DISS_DBID = Disseminator.DISS_DBID AND "+
        "BehaviorDefinition.BDEF_DBID = Disseminator.BDEF_DBID AND "+
        "BehaviorMechanism.BMECH_DBID = Disseminator.BMECH_DBID AND "+
        "DataStreamBindingMap.BMECH_DBID=BehaviorMechanism.BMECH_DBID AND "+
        "Disseminator.DISS_ID=\'" + disseminatorID +"\' AND "+
        "DigitalObject.DO_PID=\'" + PID + "\';";

    if (debug) System.out.println("ObjectQuery: "+query);
    ResultSet rs = null;
    String[] results = null;
    try
    {
      Connection connection = connectionPool.getConnection();
      Statement statement = connection.createStatement();
      rs = statement.executeQuery(query);
      ResultSetMetaData rsMeta = rs.getMetaData();
      int cols = rsMeta.getColumnCount();
      while (rs.next())
      {
        results = new String[cols];
        for (int i=1; i<=cols; i++)
        {
          results[i-1] = rs.getString(i);
        }
        disseminator = new Disseminator();
        disseminator.dissID = results[0];
        disseminator.bDefID = results[1];
        disseminator.bMechID = results[2];
        disseminator.dsBindMapID = results[3];
      }
      connectionPool.free(connection);
      connection.close();
      statement.close();
    } catch (SQLException sqle)
    {
      // Problem with the SQL database or query
      ObjectNotFoundException onfe = new ObjectNotFoundException("");
      onfe.initCause(sqle);
      //throw onfe;
    }
    if (disseminator == null)
    {
      // Empty result means that the digital object could not be found in the
      // SQL database. This could be due to incorrectly specified parameters
      // for PID OR the method is not in the SQL database. If not in the SQL
      // database, attempt to find the object in the Definitive XML storage
      // area.
      try
      {
        // Try to find object in the Definitive XML storage area.
        // FIXME - until xml storage code is implemented, the call below
        // will throw a FileNotFound exception unless the object is one of the sample
        // objects in DefinitiveBMechReader
        if (doReader == null) doReader = new DefinitiveDOReader(PID);
        disseminator = doReader.GetDisseminator(disseminatorID, versDateTime);
        // FIXME - need to catch appropriate Exception thrown by
        // DefinitiveDOReader if the PID cannot be found. For now,
        // just catch any exception.
      } catch (Exception e)
      {
        // If object cannot be found in the Definitive XML storage area,
        // then the object request contains errors or the object does NOT
        // exist in the repository. In either case, this is a nonfatal
        // error that is passed back up the line.
        String message = "OBJECT NOT FOUND --\n PID: "+PID+"\n asOfDate: "+
                         DateUtility.convertDateToString(versDateTime)+"\n";
        //throw new ObjectNotFoundException(message);
      }
    }
    return(disseminator);
  }

  public String[] GetBehaviorDefs(Date versDateTime)
  {
    Vector queryResults = new Vector();
    String[] bDefs = null;
    String  query =
        "SELECT DISTINCT "+
        "BehaviorDefinition.BDEF_PID "+
        "FROM "+
        "BehaviorDefinition,"+
        "Disseminator,"+
        "DigitalObject,"+
        "DigitalObjectDissAssoc "+
        "WHERE "+
        "DigitalObject.DO_DBID = DigitalObjectDissAssoc.DO_DBID AND "+
        "DigitalObjectDissAssoc.DISS_DBID = Disseminator.DISS_DBID AND "+
        "BehaviorDefinition.BDEF_DBID = Disseminator.BDEF_DBID AND "+
        "DigitalObject.DO_PID=\'" + PID + "\';";

    if (debug) System.out.println("ObjectQuery: "+query);
    ResultSet rs = null;
    String results = null;
    try
    {
      Connection connection = connectionPool.getConnection();
      Statement statement = connection.createStatement();
      rs = statement.executeQuery(query);
      ResultSetMetaData rsMeta = rs.getMetaData();
      int cols = rsMeta.getColumnCount();
      while (rs.next())
      {
        for (int i=1; i<=cols; i++)
        {
          results = new String(rs.getString(i));
        }
        queryResults.add(results);
      }
      connectionPool.free(connection);
      connection.close();
      statement.close();
    } catch (SQLException sqle)
    {
      // Problem with the SQL database or query
      ObjectNotFoundException onfe = new ObjectNotFoundException("");
      onfe.initCause(sqle);
      //throw onfe;
    }
    if (queryResults.isEmpty())
    {
      // Empty result means that the digital object could not be found in the
      // SQL database. This could be due to incorrectly specified parameters
      // for PID OR the method is not in the SQL database. If not in the SQL
      // database, attempt to find the object in the Definitive XML storage
      // area.
      try
      {
        // Try to find object in the Definitive XML storage area.
        // FIXME - until xml storage code is implemented, the call below
        // will throw a FileNotFound exception unless the object is one of the sample
        // objects in DefinitiveBMechReader
        if (doReader == null) doReader = new DefinitiveDOReader(PID);
        bDefs = doReader.GetBehaviorDefs(versDateTime);
        // FIXME - need to catch appropriate Exception thrown by
        // DefinitiveDOReader if the PID cannot be found. For now,
        // just catch any exception.
      } catch (Exception e)
      {
        // If object cannot be found in the Definitive XML storage area,
        // then the object request contains errors or the object does NOT
        // exist in the repository. In either case, this is a nonfatal
        // error that is passed back up the line.
        String message = "OBJECT NOT FOUND --\n PID: "+PID+"\n asOfDate: "+
                         DateUtility.convertDateToString(versDateTime)+"\n";
        //throw new ObjectNotFoundException(message);
      }
    } else
    {
      // Request for object successful; return results.
      Enumeration e = queryResults.elements();
      bDefs = new String[queryResults.size()];
      int count = 0;
      while (e.hasMoreElements())
      {
        bDefs[count] = (String)e.nextElement();
        count++;
      }
    }
    return(bDefs);
  }

  public MethodDef[] GetBMechMethods(String bDefPID, Date versDateTime)
  {
    System.out.println("MethodPIDMech: "+PID);
    MethodDef[] methodDefs = null;
    MethodDef methodDef = null;
    Vector queryResults = new Vector();
    String  query =
        "SELECT DISTINCT "+
        "Method.METH_Name,"+
        "Method.METH_Label,"+
        "MechanismImpl.MECHImpl_Address_Location,"+
        "MechanismImpl.MECHImpl_Operation_Location "+
        "FROM "+
        "BehaviorDefinition,"+
        "Disseminator,"+
        "Method,"+
        "DigitalObject,"+
        "DigitalObjectDissAssoc,"+
        "BehaviorMechanism,"+
        "MechanismImpl "+
        "WHERE "+
        "DigitalObject.DO_DBID = DigitalObjectDissAssoc.DO_DBID AND "+
        "DigitalObjectDissAssoc.DISS_DBID = Disseminator.DISS_DBID AND "+
        "BehaviorDefinition.BDEF_DBID = Disseminator.BDEF_DBID AND "+
        "BehaviorMechanism.BMECH_DBID = Disseminator.BMECH_DBID AND "+
        "BehaviorMechanism.BMECH_DBID = MechanismImpl.BMECH_DBID AND "+
        "BehaviorDefinition.BDEF_DBID = MechanismImpl.BDEF_DBID AND "+
        "Method.METH_DBID = MechanismImpl.METH_DBID AND "+
        "Method.BDEF_DBID = BehaviorDefinition.BDEF_DBID AND "+
        "BehaviorDefinition.BDEF_PID = \'" + bDefPID + "\' AND "+
        "DigitalObject.DO_PID=\'" + PID + "\';";

    if (debug) System.out.println("ObjectQuery: "+query);
    ResultSet rs = null;
    String[] results = null;
    try
    {
      Connection connection = connectionPool.getConnection();
      Statement statement = connection.createStatement();
      rs = statement.executeQuery(query);
      ResultSetMetaData rsMeta = rs.getMetaData();
      int cols = rsMeta.getColumnCount();
      int rowCount = 0;
      while (rs.next())
      {
        results = new String[cols];
        methodDef = new MethodDef();
        for (int i=1; i<=cols; i++)
        {
          results[i-1] = rs.getString(i);
          System.out.println("method[+"+i+"] = "+results[i-1]);

        }
        methodDef.methodName = results[0];
        methodDef.methodLabel = results[1];
        methodDef.httpBindingURL = null;
        methodDef.httpBindingAddress = results[2];
        methodDef.httpBindingOperationLocation = results[3];
        try
        {
          methodDef.methodParms = this.GetBMechMethodParm(bDefPID, methodDef.methodName, versDateTime);
        } catch (MethodParmNotFoundException mpnfe)
        {
          //throw mpnfe;
          //System.out.println("MethodParmNotFoundException");
        }
        queryResults.add(methodDef);
        rowCount++;
      }
      methodDefs = new MethodDef[rowCount];
      rowCount = 0;
      Enumeration e = queryResults.elements();
      while (e.hasMoreElements())
      {
        methodDefs[rowCount] = (MethodDef)e.nextElement();
        rowCount++;
      }
      connectionPool.free(connection);
      connection.close();
      statement.close();
    } catch (SQLException sqle)
    {
      // Problem with the SQL database or query
      ObjectNotFoundException onfe = new ObjectNotFoundException("");
      onfe.initCause(sqle);
      //throw onfe;
    }
    if (queryResults.isEmpty())
    {
      // Empty result means that the digital object could not be found in the
      // SQL database. This could be due to incorrectly specified parameters
      // for PID OR the method is not in the SQL database. If not in the SQL
      // database, attempt to find the object in the Definitive XML storage
      // area.
      try
      {
        // Try to find object in the Definitive XML storage area.
        // FIXME - until xml storage code is implemented, the call below
        // will throw a FileNotFound exception unless the object is one of the sample
        // objects in DefinitiveBMechReader
        if (doReader == null) doReader = new DefinitiveDOReader(PID);
        methodDefs = doReader.GetBMechMethods(bDefPID, versDateTime);
        // FIXME - need to catch appropriate Exception thrown by
        // DefinitiveDOReader if the PID cannot be found. For now,
        // just catch any exception.
      } catch (Exception e)
      {
        // If object cannot be found in the Definitive XML storage area,
        // then the object request contains errors or the object does NOT
        // exist in the repository. In either case, this is a nonfatal
        // error that is passed back up the line.
        String message = "OBJECT NOT FOUND --\n PID: "+PID+"\n asOfDate: "+
                         DateUtility.convertDateToString(versDateTime)+"\n";
        //throw new ObjectNotFoundException(message);
      }
    }
    return methodDefs;
  }

  public MethodParmDef[] GetBMechMethodParm(String bDefPID, String methodName, Date versDateTime)
      throws MethodParmNotFoundException
  {
    MethodParmDef[] methodParms = null;
    MethodParmDef methodParm = null;
    Vector queryResults = new Vector();

    // Note that the query retrieves the list of available methods
    // based on Behavior Mechanism object and NOT the Behavior
    // Definition object. This is done to insure that only methods
    // that have been implemented in the mechanism are returned.
    // This distinction is only important when versioning is enabled
    // in a later release. When versioning is enabled, it is possible
    // that a given Behavior Definition may have methods that have not
    // yet been implemented by all of its associated Behavior Mechanisms.
    // In such a case, only those methods implemented in the mechanism
    // will be returned.
    String query = "SELECT DISTINCT "+
            "PARM_Name,"+
            "PARM_Default_Value,"+
            "PARM_Required_Flag,"+
            "PARM_Label "+
            " FROM "+
            "DigitalObject,"+
            "BehaviorDefinition,"+
            "BehaviorMechanism,"+
            "MechanismImpl,"+
            "Method,"+
            "Parameter "+
            " WHERE "+
            "BehaviorMechanism.BDEF_DBID=Parameter.BDEF_DBID AND "+
            "Method.BDEF_DBID=Parameter.BDEF_DBID AND "+
            "Method.METH_DBID=Parameter.METH_DBID AND "+
            "BehaviorMechanism.BDEF_DBID=Method.BDEF_DBID AND "+
            "MechanismImpl.METH_DBID=Method.METH_DBID AND " +
            "BehaviorMechanism.BDEF_DBID=BehaviorDefinition.BDEF_DBID AND "+
            "DigitalObject.DO_PID=\'" + PID + "\' AND "+
            "BehaviorDefinition.BDEF_PID='" + bDefPID + "' AND "+
            "Method.METH_Name='"  + methodName + "' ";

    if(debug) System.out.println("MethodParmQuery="+query+"\n");
    try
    {
      Connection connection = connectionPool.getConnection();
      if(debug) System.out.println("connectionPool = "+connectionPool);
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(query);
      ResultSetMetaData rsMeta = rs.getMetaData();
      int cols = rsMeta.getColumnCount();
      int rowCount = 0;
      // Note: a row is returned for each method parameter
      while (rs.next())
      {
        methodParm = new MethodParmDef();
        String[] results = new String[cols];
        for (int i=1; i<=cols; i++)
        {
          results[i-1] = rs.getString(i);
        }
        methodParm.parmName = results[0];
        methodParm.parmDefaultValue = results[1];
        Boolean B = new Boolean(results[2]);
        methodParm.parmRequired = B.booleanValue();
        methodParm.parmLabel = results[3];
        // Add each row of results to vector
        queryResults.addElement(methodParm);
        rowCount++;
      }
      methodParms = new MethodParmDef[rowCount];
      rowCount = 0;
      Enumeration e = queryResults.elements();
      while (e.hasMoreElements())
      {
        methodParms[rowCount] = (MethodParmDef)e.nextElement();
        rowCount++;
      }
      connectionPool.free(connection);
      connection.close();
      statement.close();
    } catch (SQLException sqle)
    {
      // Problem with the SQL database or query
      MethodParmNotFoundException mpnfe = new MethodParmNotFoundException("");
      mpnfe.initCause(sqle);
      throw mpnfe;
    }

    if (queryResults.isEmpty())
    {
      // Empty result means that method(Behavior Mechanism object) could
      // not be found in the SQL database. This could be due to incorrectly
      // specified parameters for bDefPID and/or method OR the method is not
      // not in the SQL database. If not in the SQL database, attempt
      // to find the object in the Definitive storage.
      try
      {
        // Try to find method parameters in the Definitive storage.
        // FIXME - until xml storage code is implemented, the call below
        // will throw a FileNotFound exception unless the object is one of the sample
        // objects in DefinitiveBMechReader
        DefinitiveBMechReader bmechReader = new DefinitiveBMechReader(bDefPID);
        // FIXME - code to get method parameters directly from the
        // XML objects NOT implemented yet.
        return methodParms;
        // FIXME - need to catch appropriate Exception thrown by
        // DefinitiveDOReader if the PID cannot be found. For now,
        // just catch any exception.
      } catch (Exception e)
      {
        // If method cannot be found in the Definitive storage, then
        // the method parameter request contains errors or the method
        // does NOT exist in the repository. In either case, this is a
        // nonfatal error that is passed back up the line.
        String message = "METHOD PARM NOT FOUND --\n bDefPID: "+bDefPID+
                         "\n methodName: "+methodName+"\n asOfDate: "+
                         DateUtility.convertDateToString(versDateTime)+"\n";
        throw new MethodParmNotFoundException(message);
      }
    } else
    return methodParms;
  }

  public InputStream GetBMechMethodsWSDL(String bDefPID, Date versDateTime)
  {
    if (doReader == null) doReader = new DefinitiveDOReader(PID);
    return doReader.GetBMechMethodsWSDL(bDefPID, versDateTime);

  }

  public DSBindingMapAugmented[] GetDSBindingMaps(Date versDateTime)
  {
    if (bMechReader == null) bMechReader = new DefinitiveBMechReader(PID);
    return bMechReader.GetDSBindingMaps(versDateTime);
  }

  public static void main(String[] args)
  {
    // Test dissemination query against SQL database
    System.out.println("\nBEGIN ----- TEST RESULTS FOR DISSEMINATION:");
    String PID = "1007.lib.dl.test/text_ead/viu00003";
    String bDefPID = "web_ead";
    String methodName = "get_web_default";
    Date versDateTime = DateUtility.convertStringToDate("2002-08-21T12:30:58");
    FastDOReader fdor = null;
    Vector results = new Vector();
    Dissemination[] dissem = null;
    try
    {
      fdor = new FastDOReader(PID);
      dissem = fdor.getDissemination(PID, bDefPID, methodName, versDateTime);
      for (int i=0; i<dissem.length; i++)
      {
          System.out.println("dissemResults["+i+"] = "+i+
          "dissemAddress: "+dissem[i].AddressLocation+
          "dissemOperation: "+dissem[i].OperationLocation+
          "dissemDSLocation: "+dissem[i].DSLocation+
          "dissemProtocol: "+dissem[i].ProtocolType+
          "dissemBindKey: "+dissem[i].DSBindKey);
          if (dissem[i].methodParms != null)
          {
            MethodParmDef[] methodParms = dissem[i].methodParms;
            for (int j=0; j<methodParms.length; j++)
            {
              System.out.println("Dissem: MethodParms:"+
              "parm["+j+"] = "+methodParms[j]);
            }
          } else
          {
            System.out.println("Dissem: Method Has NO PARMS");
          }
      }
      System.out.println("END ----- TEST RESULTS FOR DISSEMINATION\n");

      // Test reading method paramters from SQL database
      System.out.println("\nBEGIN ----- TEST RESULTS FOR READING METHOD PARMS:");
      MethodParmDef[] methodParms = null;
      fdor = new FastDOReader(PID);
      methodParms = fdor.GetBMechMethodParm(bDefPID, methodName,versDateTime);
      for (int i=0; i<methodParms.length; i++)
      {
        System.out.println("methodParmName:"+i+" \n"+methodParms[i].parmName+
        "\nmethodParmDefaultValue["+i+"] = "+methodParms[i].parmDefaultValue+
        "\nmethodParmRequiredFlag["+i+"] = "+methodParms[i].parmRequired+
        "\nmethodParmLabel["+i+"] = "+methodParms[i].parmLabel+"\n");
      }
      System.out.println("END ----- TEST RESULTS FOR READING METHOD PARAMETERS\n");

      // Test reading method paramters from SQL database
      System.out.println("\nBEGIN ----- TEST RESULTS FOR READING ALL METHODS:");
      MethodDef[] methodDefs = null;
      fdor = new FastDOReader(PID);
      methodDefs = fdor.GetBMechMethods(bDefPID, versDateTime);
      for (int i=0; i<methodDefs.length; i++)
      {
        System.out.println("methodDefName: "+i+" \n"+methodDefs[i].methodName+
        "\nmethodDefLabel["+i+"] = "+methodDefs[i].methodLabel+
        "\nmethodDefBindingURL["+i+"] = "+methodDefs[i].httpBindingURL+
        "\nmethodDefAddress["+i+"] = "+methodDefs[i].httpBindingAddress+
        "\nmethodDefOperation["+i+"] = "+methodDefs[i].httpBindingOperationLocation+"\n");
        methodParms = methodDefs[i].methodParms;
        if (methodParms != null)
        {
          for (int j=0; j<methodParms.length; j++)
          {
            System.out.println("METHOD HAS PARMs");
            System.out.println("methodParm: "+j+" \n"+methodParms[j].parmName+
            "\nmethodParmDefaultValue["+j+"] = "+methodParms[j].parmDefaultValue+
            "\nmethodParmRequiredFlag["+j+"] = "+methodParms[j].parmRequired+
            "\nmethodParmLabel["+j+"] = "+methodParms[j].parmLabel+"\n");
          }
        } else
        {
          System.out.println("\nMETHOD HAS NO PARMS");
        }
      }
      System.out.println("END ----- TEST RESULTS FOR READING ALL METHODS\n");

      System.out.println("\nBEGIN ----- TEST GET OBJECT");
      fdor = new FastDOReader(PID);
      Vector rs = fdor.getObject(PID, versDateTime);
      System.out.println("size: "+rs.size());
      Enumeration e = rs.elements();
      while (e.hasMoreElements())
      {
        String[] res = (String[])e.nextElement();
        for (int i=0; i<res.length; i++)
        {
          System.out.println("res["+i+"] = "+res[i]);
        }
      }
      System.out.println("END ----- TEST GET OBJECT");

      System.out.println("\nBEGIN ----- TEST GET BEAHVIOR DEFS");
      String[] bDefs = null;
      bDefs = fdor.GetBehaviorDefs(versDateTime);
      for (int i=0; i<bDefs.length; i++)
      {
        System.out.println("bDef["+i+"] = "+bDefs[i]);
      }
      System.out.println("END ----- TEST GET BEAHVIOR DEFS");

      System.out.println("\nBEGIN ----- TEST GET DISSEMINATOR");
      fdor = new FastDOReader(PID);
      Disseminator diss = fdor.GetDisseminator("web_ead1", versDateTime);
      System.out.println("dissID: "+diss.dissID+"\nbDefPID: "+diss.bDefID+"\nbMechPID: "+diss.bMechID+"\nbBindMapID: "+diss.dsBindMapID);
      PID = "uva-lib:1225";
      fdor = new FastDOReader(PID);
      diss = fdor.GetDisseminator("DISS1", versDateTime);
      System.out.println("dissID: "+diss.dissID+"\nbDefPID: "+diss.bDefID+"\nbMechPID: "+diss.bMechID+"\nbBindMapID: "+diss.dsBindMapID);
      System.out.println("END ----- TEST GET DISSEMINATOR");

      System.out.println("\nBEGIN ----- TEST GET DISSEMINATORS");
      fdor = new FastDOReader(PID);
      Disseminator[] diss1 = fdor.GetDisseminators(versDateTime);
      for (int i=0; i<diss1.length; i++)
      {
        System.out.println("dissID: "+diss1[i].dissID+"\nbDefPID: "+diss1[i].bDefID+"\nbMechPID: "+diss1[i].bMechID+"\nbBindMapID: "+diss1[i].dsBindMapID);
      }
      PID = "1007.lib.dl.test/text_ead/viu00001";
      fdor = new FastDOReader(PID);
      Disseminator[] diss2 = fdor.GetDisseminators(versDateTime);
      System.out.println("size: "+diss1.length);
      for (int i=0; i<diss2.length; i++)
      {
        System.out.println("dissID: "+diss2[i].dissID+"\nbDefPID: "+diss2[i].bDefID+"\nbMechPID: "+diss2[i].bMechID+"\nbBindMapID: "+diss2[i].dsBindMapID);
      }
      System.out.println("END ----- TEST GET DISSEMINATORS");

      System.out.println("\nBEGIN ----- TEST LIST DISSEMINATORIDS");
      fdor = new FastDOReader(PID);
      String[] dissIDs = fdor.ListDisseminatorIDs("");
      for (int i=0; i<dissIDs.length; i++)
      {
        System.out.println("ListdissID: "+dissIDs[i]);
      }
      PID = "uva-lib:1225";
      fdor = new FastDOReader(PID);
      dissIDs = fdor.ListDisseminatorIDs("");
      for (int i=0; i<dissIDs.length; i++)
      {
        System.out.println("ListdissID: "+dissIDs[i]);
      }
      System.out.println("END ----- TEST LIST DISSEMINATORIDS");

      System.out.println("\nBEGIN ----- TEST GET DATASTREAM");
      fdor = new FastDOReader(PID);
      Datastream ds = fdor.GetDatastream("DS1", versDateTime);
      System.out.println("GetDatastreamLabel: "+ds.DSLabel+"\nMIME: "+ds.DSMIME+"\nLocation: "+ds.DSLocation);
      PID = "1007.lib.dl.test/text_ead/viu00001";
      fdor = new FastDOReader(PID);
      ds = fdor.GetDatastream("1", versDateTime);
      System.out.println("GetDatastreamLabel: "+ds.DSLabel+"\nMIME: "+ds.DSMIME+"\nLocation: "+ds.DSLocation);
      System.out.println("END ----- TEST GET DATASTREAM");

      System.out.println("\nBEGIN ----- TEST GET DATASTREAMS");
      fdor = new FastDOReader(PID);
      Datastream[] dsa = fdor.GetDatastreams(versDateTime);
      for (int i=0; i<dsa.length; i++)
      {
        System.out.println("GetDatastreamsLabel: "+dsa[i].DSLabel+"\nMIME: "+dsa[i].DSMIME+"\nLocation: "+dsa[i].DSLocation);
      }
      PID = "uva-lib:1225";
      fdor = new FastDOReader(PID);
      dsa = fdor.GetDatastreams(versDateTime);
      for (int i=0; i<dsa.length; i++)
      {
        System.out.println("GetDatastreamLabel: "+dsa[i].DSLabel+"\nMIME: "+dsa[i].DSMIME+"\nLocation: "+dsa[i].DSLocation);
      }
      System.out.println("END ----- TEST GET DATASTREAMS");

      System.out.println("\nBEGIN ----- TEST GET OBJECT LABEL");
      fdor = new FastDOReader(PID);
      System.out.println("ObjectLabel: "+fdor.GetObjectLabel());

      System.out.println("\nBEGIN ----- TEST GET OBJECT PID");
      fdor = new FastDOReader(PID);
      System.out.println("ObjectLabel: "+fdor.GetObjectPID());
      System.out.println("END ----- TEST GET OBJECT PID");


    } catch(ObjectNotFoundException onfe)
    {
      System.out.println(onfe.getMessage());
      //onfe.printStackTrace();
    } catch(MethodParmNotFoundException mpnfe)
    {
      System.out.println("\nMethod has no parameters\n");
    }
  }
}