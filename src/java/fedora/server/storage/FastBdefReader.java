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
import fedora.server.storage.types.MethodParmDef;
import fedora.server.storage.types.MethodDef;

/**
 * <p><b>Title: </b>FastBdefReader.java</p>
 * <p><b>Description: </b>BDEF Object Reader that accesses objects located in the
 * "Fast" storage area. It mirrors the functionality of SimpleBDefReader for
 * the "Definitive" storage area. To enhance performance of disseminations,
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
 * <p>This reader is designed to read bdef objects from the "Fast" storage area
 * that is implemented as a relational database. If the object cannot be found
 * in the relational database, this reader will attempt to read the object
 * from the Definitive storage area using the appropriate definitive reader.
 * When the object exists in both storage areas, preference is given to the
 * Fast storage area since this reader is designed to read primarily from the
 * Fast Storage area. A <code>SimpleBDefReader</code> should always be used to
 * read the authoritative version of a bdef object.</p>
 * <p><i>Note that versioning is not implemented in Phase 1. Methods in
 * <code>FastBdefReader</code> that contain arguments related to versioning
 * date such as <code>versDateTime</code> or <code>asOfDate</code> will be
 * ignored in Phase 1.</i></p>
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
 * @version $Id$
 */
public class FastBdefReader extends FastDOReader implements BDefReader
{

  /** Instance of BDefReader */
  private BDefReader bDefReader = null;

  /** Persistent identifier of behavior definition object */
  private String bDefPID = null;

  /** Label of behavior definition object */
  private String bDefLabel = null;

  /**
   * <p>Constructs an instance of FastBdefReader.</p>
   *
   * <p>Constructs a new <code>FastBdefReader</code> for the specified bdef
   * object. If the object is found, this constructor initializes the class
   * variables for <code>bDefPID</code> and <code>bDefLabel</code>.
   *
   * @param context The context of this request.
   * @param objectPID The persistent identifier of the bdef object.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public FastBdefReader(Context context, String objectPID) throws ServerException
  {
    super(context, objectPID);

    // Override FastDOReader constructor.
    // In FastBdefReader, the object PID is assumed to always be a bdef PID.
    try
    {
      // Attempt to find bdef object in either Fast or Definitive store
      this.bDefLabel = locateBdefPID(objectPID);
      this.bDefPID = objectPID;
    } catch (ServerException se)
    {
      throw se;
    } catch (Throwable th)
    {
      s_server.logWarning("[FastBdefReader] Unable to construct FastBdefReader");
      throw new GeneralException("[FastBdefReader] An error has occurred. "
          + "The error was a \"" + th.getClass().getName() + "\"  .  "
          + "Reason: \""  + th.getMessage() + "\"  .");
    }
  }


  /**
   * <p>This method retrieves the list of available methods based on
   * Behavior Definition object.</p>
   *
   * @param versDateTime The versioning datetime stamp.
   * @return An array of method definitions.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public MethodDef[] getAbstractMethods(Date versDateTime) throws ServerException
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
          + "method "
          + "WHERE "
          + "method.bDefDbID = bDef.bDefDbID AND "
          + "bDef.bDefPID = \'" + bDefPID + "\'";

      s_server.logFinest("[FastBdefReader] getAbstractMethodsQuery: " + query);
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
            methodDef.methodParms = getAbstractMethodParms(methodDef.methodName,
                versDateTime);
          } catch (Throwable th)
          {
            // Failed to get method paramters
            throw new GeneralException("[FastBdefReader] An error has occured. The "
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
      } catch (Throwable th)
      {
        throw new GeneralException("[FastBdefReader] An error has occured. The "
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
          throw new GeneralException("[FastBdefReader] Unexpected error from SQL "
              + "database. The error was: " + sqle.getMessage());
        }
      }
    } else if (isFoundInDefinitiveStore || versDateTime != null)
    {
      // Requested object exists in Definitive storage area or is versioned;
      // query Definitive storage area.
      try
      {
        if (bDefReader == null)
        {
          bDefReader = m_manager.getBDefReader(m_context, PID);
        }
        methodDefs = bDefReader.getAbstractMethods(versDateTime);
      } catch (Throwable th)
      {
        throw new GeneralException("[FastBdefReader] Definitive bDefReader returned "
            + "error. The underlying error was a  \"" + th.getClass().getName()
          + "\"  . The message was  \"" + th.getMessage() + "\"  .");
      }
    }
    return methodDefs;
  }

  /**
   * <p>Gets XML containing method definitions. Since the XML representation
   * of digital objects is not stored in the Fast storage area, this method
   * uses a <code>BDefReader</code> to query the Definitive
   * storage area.</p>
   *
   * @param versDateTime The versioning datetime stamp.
   * @return A stream of bytes containing XML-encoded representation of
   *         method definitions from XML in the Behavior Definition
   *         object.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public InputStream getAbstractMethodsXML(Date versDateTime) throws ServerException
  {
    try
    {
      if (bDefReader == null)
      {
        bDefReader = m_manager.getBDefReader(m_context, PID);
      }
      return bDefReader.getAbstractMethodsXML(versDateTime);
    } catch (ServerException se)
    {
      throw se;

    } catch (Throwable th)
    {
      throw new GeneralException("[FastbDefReader] Definitive bDefReader returned "
          + "error. The underlying error was a  \"" + th.getClass().getName()
          + "\"  . The message was  \"" + th.getMessage() + "\"  .");
    }
  }

  /**
   * <p>Gets user method parameters associated with the specified method name.</p>
   *
   * @param methodName The name of the method.
   * @param versDateTime The versioning datetime stamp.
   * @return An array of method parameter definitions.
   * @throws GeneralException If there was any misc exception that we want to
   *         catch and re-throw as a Fedora exception. Extends ServerException.
   */
  public MethodParmDef[] getAbstractMethodParms(String methodName,
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
          + "parmName,"
          + "parmDefaultValue,"
          + "parmDomainValues,"
          + "parmRequiredFlag,"
          + "parmLabel,"
          + "parmType "
          + " FROM "
          + "bDef,"
          + "method,"
          + "parm "
          + " WHERE "
          + "method.bDefDbID=parm.bDefDbID AND "
          + "method.methodDbID=parm.methodDbID AND "
          + "bDef.bDefPID='" + bDefPID + "' AND "
          + "method.methodName='"  + methodName + "'";

      s_server.logFinest("GetBdefMethodParmQuery=" + query);
      try
      {
        connection = connectionPool.getConnection();
        s_server.logFinest("connectionPool = " + connectionPool);
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
            s_server.logFinest("[FastBdefReader] "
                + "methodParms: " + methodParm.parmName
                + "label: " + methodParm.parmLabel
                + "default: " + methodParm.parmDefaultValue
                + "required: " + methodParm.parmRequired
                + "type: " + methodParm.parmType);
            for (int j=0; j<methodParm.parmDomainValues.length; j++)
            {
              s_server.logFinest("[FastBdefReader] domainValues: "
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
        throw new GeneralException("[FastBdefReader] An error has occured. The "
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
          throw new GeneralException("[FastBdefReader] Unexpected error from SQL "
              + "database. The error was: " + sqle.getMessage());
        }
      }
    } else if (isFoundInDefinitiveStore || versDateTime != null)
    {
      // Requested object exists in Definitive storage area or is versioned;
      // query Definitive storage area.
      try
      {
        if (bDefReader == null)
        {
          bDefReader = m_manager.getBDefReader(m_context, bDefPID);
        }
        methodParms = bDefReader.getObjectMethodParms(bDefPID, methodName,
            versDateTime);
      } catch (Throwable th)
      {
        throw new GeneralException("[FastBdefReader] Definitive bDefReader returned "
            + "error. The underlying error was a  \"" + th.getClass().getName()
            + "\"  . The message was  \"" + th.getMessage() + "\"  .");
      }
    }
    return methodParms;
  }

  /**
   * <p>Locates the specified bdef object using its persistent identifier.
   * This method will first attempt to locate the object in the Fast storage
   * area. If the the object cannot be located there, it will then try to find
   * it in the Definitive storage area. If the object is found, the object's
   * label is returned. Otherwise, it throws
   * <code>GeneralException</code>.</p>
   *
   * @param bDefPID persistent identifier of the digital object.
   * @return String containing label of the specified digital object.
   * @throws GeneralException If there was any misc exception that we want to
   *         catch and re-throw as a Fedora exception. Extends ServerException.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public String locateBdefPID(String bDefPID) throws GeneralException, ServerException
  {
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;
    String  query =
        "SELECT "
        + "bDef.bDefLabel "
        + "FROM "
        + "bDef "
        + "WHERE "
        + "bDef.bDefPID=\'" + bDefPID + "\'";
    s_server.logFinest("LocatBdefPIDQuery: " + query);

    try
    {
      connection = connectionPool.getConnection();
      s_server.logFinest("[FastBdefReader] LocateBdefPIDConnectionPool: "
          + connectionPool);
      statement = connection.createStatement();
      rs = statement.executeQuery(query);
      while (rs.next())
      {
        bDefLabel = rs.getString(1);
      }
    } catch (Throwable th)
    {
      throw new GeneralException("[FastBdefReader] An error has occurred. The "
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
        throw new GeneralException("[FastBdefReader] Unexpected error from SQL "
            + "database. The error was: " + sqle.getMessage());
      }
    }
    if (bDefLabel == null || bDefLabel.equalsIgnoreCase(""))
    {
      // Empty result means that the bdef object could not be found in the
      // relational database. This could be due to incorrectly specified
      // parameter for PID OR the object is not in the relational database.
      // If not in the relational database, attempt to find the object in the
      // Definitive storage area.
      try
      {
        if (doReader == null)
        {
          doReader = m_manager.getReader(m_context, bDefPID);
        }
        bDefLabel = doReader.GetObjectLabel();
        isFoundInDefinitiveStore = true;
        s_server.logFinest("[FastBdefReader] BDEF OBJECT FOUND IN DEFINITIVE "
            + "STORE: " + bDefPID);
      } catch (ServerException se)
      {
        throw se;
      } catch (Throwable th)
      {
        s_server.logWarning("[FastBdefReader] BDEF OBJECT NOT FOUND IN "
            + "DEFINITIVE STORE: " + bDefPID);
        throw new GeneralException("[FastBdefReader] Definitive doReader "
            + "returned error. The underlying error was a  \""
            + th.getClass().getName() + "\"  . The message "
            + "was  \"" + th.getMessage() + "\"  .");
      }
    } else
    {
      isFoundInFastStore = true;
      s_server.logFinest("[FastBdefReader] BDEF OBJECT FOUND IN FAST STORE: "
          + bDefPID);
    }
    return bDefLabel;
  }
}