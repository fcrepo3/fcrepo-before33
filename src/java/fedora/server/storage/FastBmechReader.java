package fedora.server.storage;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import fedora.server.Context;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ServerException;
import fedora.server.storage.types.BMechDSBindSpec;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodDefOperationBind;
import fedora.server.storage.types.MethodParmDef;

/**
 * <p>Title: FastBmechReader.java</p>
 * <p>Description: BMECH Object Reader that accesses objects located in the
 * "Fast" storage area. It mirros the functionality of SimpleBMechReader for
 * the "Definitive" storage aread. To enhance performance of disseminations,
 * there are two distinct storage areas for digital objects:
 * <ol>
 * <li>
 * "Fast" storage area - The storage area containing a subset of digital
 * objects that is optimized for performance. Both the composition of the
 * subset of objects and storage area are implementation specific. For Phase 1,
 * this object subset consists of a partial replication of the most current
 * version of each object and is used as the primary source for resolving
 * dissemination requests. The replication is partial since only information
 * required to disseminate the object is replicated in the Fast storage area.
 * For Phase 1, the Fast storage area is implemented as a relational database
 * that is accessed via JDBC. <i>Note that an appropriate definitve reader
 * should always be used to obtain the most complete information about a
 * specific object. A fast reader is used primarily for dissemination
 * requests.</i>.
 * </li>
 * <li>
 * Definitive storage area - The storage area containing complete information on
 * all digital objects in the repository. This storage area is used as the
 * authoritative source for reading complete information about a digital object.
 * This storage area is used as a secondary source for resolving dissemination
 * requests when the specified object does not exist in the Fast storage area.
 * </li>
 * </ol>
 * <p>This reader is designed to read bmech objects from the "Fast" storage area
 * that is implemented as a relational database. If the object cannot be found
 * in the relational database, this reader will attempt to read the object
 * from the Definitive storage area using the appropriate definitive reader.
 * When the object exists in both storage areas, preference is given to the
 * Fast storage area since this reader is designed to read primarily from the
 * Fast Storage area. A <code>SimpleBMechReader</code> should always be used to
 * read the authoritative version of a bmech object.</p> <i><b>Note that
 * versioning is not implemented in Phase 1. Methods in
 * <code>FastBmechReader</code> that contain arguments related to versioning
 * date such as <code>versDateTime</code> or <code>asOfDate</code> will be
 * ignored in Phase 1.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class FastBmechReader extends FastDOReader implements BMechReader
{

  /** Instance of BMechReader */
  private BMechReader bMechReader = null;

  /** Persistent identifier of behavior mechanism object */
  private String bMechPID = null;

  /** Label of behavior mechanism object */
  private String bMechLabel = null;

  /**
   * <p>Constructs an instance of FastBmechReader.</p>
   *
   * <p>Constructs a new <code>FastBmechReader</code> for the specified bmech
   * object. If the object is found, this constructor initializes the class
   * variables for <code>bMechPID</code> and <code>bMechLabel</code>.
   *
   * @param context The context of this request.
   * @param objectPID The persistent identifier of the bmech object.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public FastBmechReader(Context context, String objectPID) throws ServerException
  {
    super(context, objectPID);

    // Override FastDOReader constructor.
    // In FastBmechReader, the object PID is assumed to always be a bmech PID.
    try
    {
      // Attempt to find bmech object in either Fast or Definitive store
      this.bMechLabel = locateBmechPID(objectPID);
      this.bMechPID = objectPID;
    } catch (ServerException se)
    {
      throw se;
    } catch (Throwable th)
    {
      s_server.logWarning("[FastBmechReader] Unable to construct FastBmechReader");
      throw new GeneralException("[FastBmechReader] An error has occurred. "
          + "The error was a \"" + th.getClass().getName() + "\"  .  "
          + "Reason: \""  + th.getMessage() + "\"  .");
    }
  }

  /**
   * <p>Gets default mechanism method parameters associated with the specified
   * method name. Default method parameters are defined by the Behavior
   * Mechanism object as mechanism default parameters and cannot be altered
   * by the user.</p>
   *
   * @param methodName The name of the method.
   * @param versDateTime The versioning datetime stamp.
   * @return An array of method parameter definitions.
   * @throws GeneralException If there was any misc exception that we want to
   *         catch and re-throw as a Fedora exception. Extends ServerException.
   */
  public MethodParmDef[] getServiceMethodParms(String methodName,
      Date versDateTime) throws GeneralException
  {
    MethodParmDef[] methodParms = null;
    MethodParmDef methodParm = null;
    Vector queryResults = new Vector();
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;

    if (isFoundInFastStore && versDateTime == null)
    {
      // Requested object exists in Fast storage area and is NOT versioned;
      // query relational database
      String query =
          "SELECT DISTINCT "
          + "defParmName,"
          + "defParmDefaultValue,"
          + "defParmDomainValues,"
          + "defParmRequiredFlag,"
          + "defParmLabel,"
          + "defParmType "
          + " FROM "
          + "bDef,"
          + "bMech,"
          + "mechImpl,"
          + "method,"
          + "mechDefParm "
          + " WHERE "
          + "bMech.bMechDbID=mechDefParm.bMechDbID AND "
          + "method.methodDbID=mechDefParm.methodDbID AND "
          + "bMech.bDefDbID=method.bDefDbID AND "
          + "mechImpl.methodDbID=method.methodDbID AND "
          + "bMech.bDefDbID=bDef.bDefDbID AND "
          + "bMech.bMechPID='" + bMechPID + "' AND "
          + "method.methodName='"  + methodName + "' ";

      s_server.logFinest("[FastBmechReader] GetBmechDefaultMethodParmQuery=" + query);
      try
      {
        connection = connectionPool.getConnection();
        s_server.logFinest("[FastBmechReader] connectionPool = " + connectionPool);
        statement = connection.createStatement();
        rs = statement.executeQuery(query);
        ResultSetMetaData rsMeta = rs.getMetaData();
        int cols = rsMeta.getColumnCount();

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
          methodParm.parmDomainValues = results[2].split(",");
          Boolean B = new Boolean(results[3]);
          methodParm.parmRequired = B.booleanValue();
          methodParm.parmLabel = results[4];
          methodParm.parmType = results[5];
          s_server.logFinest("[FastBmechReader] "
              + "methodParms: " + methodParm.parmName
              + "\nlabel: " + methodParm.parmLabel
              + "\ndefault: " + methodParm.parmDefaultValue
              + "\nrequired: " + methodParm.parmRequired
              + "\ntype: " + methodParm.parmType);
          for (int j=0; j<methodParm.parmDomainValues.length; j++)
          {
            s_server.logFinest("FastBmechReader:domainValues: "
                + methodParm.parmDomainValues[j]);
          }
          queryResults.addElement(methodParm);
        }
        methodParms = new MethodParmDef[queryResults.size()];
        int rowCount = 0;
        for (Enumeration e = queryResults.elements(); e.hasMoreElements();)
        {
          methodParms[rowCount] = (MethodParmDef)e.nextElement();
          rowCount++;
        }
      } catch (Throwable th)
      {
        throw new GeneralException("[FastBmechReader] An error has occurred. "
                                   + "The underlying error was a \""
                                   + th.getClass().getName() + "\"  The message "
                                   + "was \"" + th.getMessage() + "\"  .");
      } finally
      {
        try
        {
          if (rs != null) rs.close();
          if (statement != null) statement.close();
          connectionPool.free(connection);
        } catch (SQLException sqle)
        {
          throw new GeneralException("[FastBmechReader] Unexpected error "
              + "from SQL database. The error was \"" + sqle.getMessage()
              + "\"  .");
        }
      }
    } else if (isFoundInDefinitiveStore || versDateTime != null)
    {
      // Requested object exists in Definitive storage area or is versioned;
      // query Definitive storage area.
      try
      {
        if (bMechReader == null)
        {
          bMechReader = m_manager.getBMechReader(m_context, bMechPID);
        }
        methodParms = bMechReader.getServiceMethodParms(methodName, versDateTime);
      } catch (Throwable th)
      {
        throw new GeneralException("[FastBmechReader] Definitive doReader "
            + "returned an error. The underlying error was a  \""
            + th.getClass().getName() + "\"  The message "
            + "was  \"" + th.getMessage() + "\"  .");
      }
    }
    return methodParms;
  }

  /**
   * <p>Gets all method defintiions associated with the specified Behavior
   * Mechanism. Note the PID of the associated Behavior Mechanism object is
   * determined via reflection based on the specified PID of the digital object
   * and the PID of its Behavior Definition object. This method retrieves the
   * list of available methods based on the assocaited Behavior Mechanism
   * object and NOT the Behavior Definition object. This is done to insure
   * that only methods that have been implemented in the mechanism are returned.
   * This distinction is only important when versioning is enabled
   * in a later release. When versioning is enabled, it is possible
   * that a versioned Behavior Definition may have methods that have not
   * yet been implemented by all of its associated Behavior Mechanisms.
   * In such a case, only those methods implemented in the mechanism
   * will be returned.</p>
   *
   * @param bDefPID The persistent identifier of Behavior Definition object.
   * @param versDateTime The versioning datetime stamp.
   * @return An array of method definitions.
   * @throws GeneralException If there was any misc exception that we want to
   *         catch and re-throw as a Fedora exception. Extends ServerException.
   */
  public MethodDef[] getServiceMethods(Date versDateTime) throws ServerException
  {
    MethodDef[] methodDefs = null;
    MethodDef methodDef = null;
    Vector queryResults = new Vector();
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;
    if (isFoundInFastStore && versDateTime == null)
    {
      // Requested object exists in Fast storage area and is NOT versioned;
      // query relational database
      String  query =
          "SELECT DISTINCT "
          + "method.methodName,"
          + "method.methodLabel "
          + "FROM "
          + "bDef,"
          + "method,"
          + "bMech,"
          + "mechImpl "
          + "WHERE "
          + "bMech.bMechDbID = mechImpl.bMechDbID AND "
          + "bDef.bDefDbID = mechImpl.bDefDbID AND "
          + "method.methodDbID = mechImpl.methodDbID AND "
          + "method.bDefDbID = bDef.bDefDbID AND "
          + "bMech.bMechPID = \'" + bMechPID + "\' ;";

      s_server.logFinest("getObjectMethodsQuery: " + query);
      String[] results = null;
      try
      {
        connection = connectionPool.getConnection();
        statement = connection.createStatement();
        rs = statement.executeQuery(query);
        ResultSetMetaData rsMeta = rs.getMetaData();
        int cols = rsMeta.getColumnCount();
        while (rs.next())
        {
          results = new String[cols];
          methodDef = new MethodDef();
          for (int i=1; i<=cols; i++)
          {
            results[i-1] = rs.getString(i);
          }
          methodDef.methodName = results[0];
          methodDef.methodLabel = results[1];
          try
          {
            methodDef.methodParms = getServiceMethodParms(methodDef.methodName,
                versDateTime);
          } catch (Throwable th)
          {
            // Failed to get method paramters
            throw new GeneralException("[FastBmechReader] An error has occured. The "
                + "underlying error was a  \"" + th.getClass().getName()
            + "\"  . The message was  \"" + th.getMessage() + "\"  .");
          }
          queryResults.add(methodDef);
        }
        methodDefs = new MethodDef[queryResults.size()];
        int rowCount = 0;
        for (Enumeration e = queryResults.elements(); e.hasMoreElements();)
        {
          methodDefs[rowCount] = (MethodDef)e.nextElement();
          rowCount++;
        }
        return methodDefs;
      } catch (Throwable th)
      {
        throw new GeneralException("[FastBmechReader] An error has occured. The "
            + "underlying error was a  \"" + th.getClass().getName()
            + "\"  . The message was  \"" + th.getMessage() + "\"  .");
      } finally
      {
        try
        {
          if (rs != null) rs.close();
          if (statement != null) statement.close();
          connectionPool.free(connection);
        } catch (SQLException sqle)
        {
          throw new GeneralException("[FastBmechReader] Unexpected error "
              + "from SQL database. The error was \"" + sqle.getMessage()
              + "\"  .");
        }
      }
    } else if (isFoundInDefinitiveStore || versDateTime != null)
    {
      // Requested object exists in Definitive storage area or is versioned;
      // query Definitive storage area.
      try
      {
        if (bMechReader == null)
        {
          bMechReader = m_manager.getBMechReader(m_context, PID);
        }
        return bMechReader.getServiceMethods(versDateTime);
      } catch (ServerException se)
      {
        throw se;

      } catch (Throwable th)
      {
        throw new GeneralException("[FastbMechReader] Definitive bMechReader returned "
            + "error. The underlying error was a  \"" + th.getClass().getName()
            + "\"  . The message was  \"" + th.getMessage() + "\"  .");
      }
    }
    return null;
  }

  public MethodDefOperationBind[] getServiceMethodBindings(Date versDateTime) throws ServerException
  {
    try
    {
      if (bMechReader == null)
      {
        bMechReader = m_manager.getBMechReader(m_context, PID);
      }
      return bMechReader.getServiceMethodBindings(versDateTime);
    } catch (ServerException se)
    {
      throw se;

    } catch (Throwable th)
    {
      throw new GeneralException("[FastbMechReader] Definitive bMechReader returned "
          + "error. The underlying error was a  \"" + th.getClass().getName()
          + "\"  . The message was  \"" + th.getMessage() + "\"  .");
    }
  }

  /**
   * <p>Gets XML containing method definitions. Since the XML representation
   * of digital objects is not stored in the Fast storage area, this method
   * uses a <code>BMechReader</code> to query the Definitive
   * storage area.</p>
   *
   * @param versDateTime The versioning datetime stamp.
   * @return A stream of bytes containing XML-encoded representation of
   *         method definitions from XML in the Behavior Mechanism
   *         object.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public InputStream getServiceMethodsXML(Date versDateTime) throws ServerException
  {
    try
    {
      if (bMechReader == null)
      {
        bMechReader = m_manager.getBMechReader(m_context, PID);
      }
      return bMechReader.getServiceMethodsXML(versDateTime);
    } catch (ServerException se)
    {
      throw se;

    } catch (Throwable th)
    {
      throw new GeneralException("[FastbMechReader] Definitive bMechReader returned "
          + "error. The underlying error was a  \"" + th.getClass().getName()
          + "\"  . The message was  \"" + th.getMessage() + "\"  .");
    }
  }

  public BMechDSBindSpec getServiceDSInputSpec(Date versDateTime) throws ServerException
  {
    try
    {
      if (bMechReader == null)
      {
        bMechReader = m_manager.getBMechReader(m_context, PID);
      }
      return bMechReader.getServiceDSInputSpec(versDateTime);
    } catch (ServerException se)
    {
      throw se;

    } catch (Throwable th)
    {
      throw new GeneralException("[FastbMechReader] Definitive bMechReader returned "
          + "error. The underlying error was a  \"" + th.getClass().getName()
          + "\"  . The message was  \"" + th.getMessage() + "\"  .");
    }
  }

  /**
   * <p>Locates the specified bmech object using its persistent identifier.
   * This method will first attempt to locate the object in the Fast storage
   * area. If the the object cannot be located there, it will then try to find
   * it in the Definitive storage area. If the object is found, the object's
   * label is returned. Otherwise, it throws
   * <code>GeneralException</code>.</p>
   *
   * @param bMechPID persistent identifier of the digital object.
   * @return String containing label of the specified digital object.
   * @throws GeneralException If there was any misc exception that we want to
   *         catch and re-throw as a Fedora exception. Extends ServerException.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public String locateBmechPID(String bMechPID) throws GeneralException, ServerException
  {
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;
    String  query =
        "SELECT "
        + "bMech.bMechLabel "
        + "FROM "
        + "bMech "
        + "WHERE "
        + "bMech.bMechPID=\'" + bMechPID + "\';";
    s_server.logFinest("LocateBmechPIDQuery: " + query);

    try
    {
      connection = connectionPool.getConnection();
      s_server.logFinest("[FastBmechReader] LocateBmechPIDConnectionPool: "
          + connectionPool);
      statement = connection.createStatement();
      rs = statement.executeQuery(query);
      while (rs.next())
      {
        bMechLabel = rs.getString(1);
      }
    } catch (Throwable th)
    {
      throw new GeneralException("[FastBmechReader] An error has occurred. The "
          + "underlying error was a  \"" + th.getClass().getName()
          + "\"  . The message was  \"" + th.getMessage() + "\"  .");
    } finally
      {
      try
      {
        if (rs != null) rs.close();
        if (statement != null) statement.close();
        connectionPool.free(connection);
      } catch (SQLException sqle)
      {
        throw new GeneralException("[FastBmechReader] Unexpected error "
            + "from SQL database. The error was \"" + sqle.getMessage()
            + "\"  .");
        }
      }
    if (bMechLabel == null || bMechLabel.equalsIgnoreCase(""))
    {
      // Empty result means that the bmech object could not be found in the
      // relational database. This could be due to incorrectly specified
      // parameter for PID OR the object is not in the relational database.
      // If not in the relational database, attempt to find the object in the
      // Definitive storage area.
      try
      {
        if (doReader == null)
        {
          doReader = m_manager.getReader(m_context, bMechPID);
        }
        bMechLabel = doReader.GetObjectLabel();
        isFoundInDefinitiveStore = true;
        s_server.logFinest("[FastBmechReader] BMECH OBJECT FOUND IN DEFINITIVE "
            + "STORE: " + bMechPID);
      } catch (ServerException se)
      {
        throw se;
      } catch (Throwable th)
      {
        s_server.logWarning("[FastBmechReader] BMECH OBJECT NOT FOUND IN "
            + "DEFINITIVE STORE: " + bMechPID);
        throw new GeneralException("[FastBmechReader] Definitive doReader "
            + "returned error. The underlying error was a  \""
            + th.getClass().getName() + "\"  . The message "
            + "was  \"" + th.getMessage() + "\"  .");
      }
    } else
    {
      isFoundInFastStore = true;
      s_server.logFinest("[FastBmechReader] BMECH OBJECT FOUND IN FAST STORE: "
          + bMechPID);
    }
    return bMechLabel;
  }
}