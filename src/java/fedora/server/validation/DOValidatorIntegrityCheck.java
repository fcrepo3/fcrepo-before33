package fedora.server.validation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

import fedora.server.errors.GeneralException;
import fedora.server.errors.ObjectValidityException;
import fedora.server.errors.InitializationException;

/**
 *
 * <p><b>Title:</b> DOValidatorIntegrityCheck.java</p>
 * <p><b>Description:</b> This class will perform various kinds of integrity
 * checking that is beyond what XML schema or Schematron rules can do.
 * Referential integrity checks include:<ol>
 *    <li>Determine if the behavior definition objects referenced by the
 *    data object exist in the local repository.</li>
 *    <li>Determine if the behavior mechanism objects referenced by the
 *    data object exist in the local repository.</li>
 *    <li>(Future) - some degree of link checking for datastreams.  This
 *    needs to be thought through more.</li>
 *    <li>(Future) Other TBD, as needed.</ol></p>
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
 * @version $Id$
 */
 public class DOValidatorIntegrityCheck
{
  /** Class variable: Debug toggle for testing */
  private static boolean debug;

  /** Database Connection to support lookups for integrity checks */
  private Connection connection = null;

  /**
   * Data from the digital object parse to be used in
   * the referential integrity checks
   */
  private DOIntegrityVariables objectInfo = null;

  /**
   * <p>Constructs a new <code>DOValidatorIntegrityCheck</code></p>
   *
   * @param iVars - data from the digital object parse
   *        that will be used in the integrity checks.
   * @param c  - a database connection for lookups to support integrity checks
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public DOValidatorIntegrityCheck(DOIntegrityVariables iVars, Connection c)
      throws ObjectValidityException, GeneralException
  {
      objectInfo = iVars;
      connection = c;
  }

  /**
   * <p>Constructs a new <code>DOValidatorIntegrityCheck</code>.
   * This version of the constructor is used in cases where the
   * digital object has not already been parsed.  If the
   * integrity checking is done without prior XML schema validation
   * (Level 1) we need to parse the digital object to get certain
   * elements and attributes that are required to perform the
   * integrity checks. </p>
   *
   * @param objectAsFile the digital object xml to be validated.
   * @param xmlSchemaPath the local path of the xml schema to be used by
   *        the parser if the digital object has not already been
   *        parsed by Level 1 Validation.
   * @param c  - a database connection for lookups to support integrity checks
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public DOValidatorIntegrityCheck(File objectAsFile, String xmlSchemaPath, Connection c)
      throws ObjectValidityException, GeneralException
  {
      DOValidatorXMLSchema xsv = new DOValidatorXMLSchema(xmlSchemaPath);
      xsv.validate(objectAsFile);
      objectInfo = xsv.getDOIntegrityVariables();
      connection = c;
  }


  public void validate() throws ObjectValidityException, GeneralException
  {
    checkBDefsExist();
    checkBMechsExist();
    //checkDatastreamLinks();
  }

  /**
   * Determine whether the behavior definitions that a data object
   * references actually exist in the local repository.  This is done by iterating
   * through the PIDs of referenced behavior definition objects and looking up
   * those PIDs in the BehaviorDefinition table of the FedoraObjects database.
   *
   * @throws ObjectValidityException
   * @throws GeneralException
   */
  private void checkBDefsExist() throws ObjectValidityException, GeneralException
  {
    String BDefPID = null;
    ResultSet queryResult = null;
    Statement statement = null;
    boolean rowFound = false;

    for (int i = 0; i < objectInfo.bDefPIDs.length; i++)
    {
      try
      {
        BDefPID = objectInfo.bDefPIDs[i];
        if (BDefPID.equalsIgnoreCase("fedora-system:1"))
        {
          continue;
        }
        String  query =
            "SELECT "
            + "bDef.bDefDbID "
            + "FROM "
            + "bDef "
            + "WHERE "
            + "bDef.bDefPID=\'" + BDefPID + "\'";

        statement = connection.createStatement();
        queryResult = statement.executeQuery(query);
        rowFound = queryResult.next();
      }
      catch (Throwable th)
      {
        throw new GeneralException("DOValidatorIntegrityCheck returned error. "
                  + "The underlying error was a " + th.getClass().getName()
                  + "The message was "  + "\"" + th.getMessage() + "\"");
      } finally
      {
        try
        {
          if (queryResult!=null) queryResult.close();
          if (statement!= null) statement.close();
        } catch (SQLException sqle)
        {
          throw new GeneralException("Unexpected error from SQL database: " + sqle.getMessage());
        } finally {
          queryResult=null;
          statement=null;
        }
      }
      if (!rowFound)
      {
        throw new ObjectValidityException("Digital Object Disseminator "
                + "refers to a Behavior Definition Object that does not exist "
                + "in the local repository: "
                + BDefPID);
      }
    }
  }

  /**
   * Determine whether the behavior mechansim objects that a data object
   * references actually exist in the local repository.  This is done by iterating
   * through the PIDs of referenced behavior mechanism objects and looking up
   * those PIDs in the BehaviorMechanism table of the FedoraObjects database.
   *
   * @throws ObjectValidityException
   * @throws GeneralException
   */
  private void checkBMechsExist() throws ObjectValidityException, GeneralException
  {
    String BMechPID = null;
    ResultSet queryResult = null;
    Statement statement = null;
    boolean rowFound = false;

    for (int i = 0; i < objectInfo.bMechPIDs.length; i++)
    {
      try
      {
        BMechPID = objectInfo.bMechPIDs[i];
        if (BMechPID.equalsIgnoreCase("fedora-system:2"))
        {
          continue;
        }
        String  query =
            "SELECT "
            + "bMech.bMechDbID "
            + "FROM "
            + "bMech "
            + "WHERE "
            + "bMech.bMechPID=\'" + BMechPID + "\'";

        statement = connection.createStatement();
        queryResult = statement.executeQuery(query);
        rowFound = queryResult.next();
      }
      catch (Throwable th)
      {
        throw new GeneralException("DOValidatorIntegrityCheck returned error. "
                  + "The underlying error was a " + th.getClass().getName()
                  + "The message was "  + "\"" + th.getMessage() + "\"");
      } finally
      {
        try
        {
          if (queryResult!=null) queryResult.close();
          if (statement!= null) statement.close();
        } catch (SQLException sqle)
        {
          throw new GeneralException("Unexpected error from SQL database: " + sqle.getMessage());
        } finally {
          queryResult=null;
          statement=null;
        }
      }
      if (!rowFound)
      {
        throw new ObjectValidityException("Digital Object Disseminator "
                + "refers to a Behavior Mechanism Object that does not exist "
                + "in the local repository: "
                + BMechPID);
      }
    }
  }
}