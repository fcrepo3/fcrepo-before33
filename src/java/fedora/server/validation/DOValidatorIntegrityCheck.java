package fedora.server.validation;

/**
 * <p>Title: DOValidatorIntegrityCheck.java</p>
 * <p>Description: This class will perform various kinds of integrity
 * checking that is beyond what XML schema or Schematron rules can do.
 * Referential integrity checks include:
 *    a. Determine if the behavior definition objects referenced by the
 *    data object exist in the local repository.
 *    b. Determine if the behavior mechanism objects referenced by the
 *    data object exist in the local repository.
 *    c. (Future) - some degree of link checking for datastreams.  This
 *    needs to be thought through more.
 *    d. (Future) Other TBD, as needed.</p>
 * <p></p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette, payette@cs.cornell.edu
 * @version 1.0
 */

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
   * @param xmlSchemaURL the URL of the xml schema to be used by
   *        the parser if the digital object has not already been
   *        parsed by Level 1 Validation.
   * @param c  - a database connection for lookups to support integrity checks
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public DOValidatorIntegrityCheck(File objectAsFile, String xmlSchemaURL, Connection c)
      throws ObjectValidityException, GeneralException
  {
      DOValidatorXMLSchema xsv = new DOValidatorXMLSchema(xmlSchemaURL);
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
    boolean rowFound = false;
    try
    {
      for (int i = 0; i < objectInfo.bDefPIDs.length; i++)
      {
        BDefPID = objectInfo.bDefPIDs[i];
        String  query =
            "SELECT "
            + "BehaviorDefinition.BDEF_DBID "
            + "FROM "
            + "BehaviorDefinition "
            + "WHERE "
            + "BehaviorDefinition.BDEF_PID=\'" + BDefPID + "\';";

        Statement statement = connection.createStatement();
        queryResult = statement.executeQuery(query);
        rowFound = queryResult.next();
        statement.close();
      }
    }
    catch (Throwable th)
    {
      throw new GeneralException("DOValidatorIntegrityCheck returned error. "
                + "The underlying error was a " + th.getClass().getName()
                + "The message was "  + "\"" + th.getMessage() + "\"");
    }
    if (!rowFound)
    {
      System.out.println("Digital Object Disseminator "
              + "refers to a Behavior Definition Object that does not exist "
              + "in the local repository: "
              + BDefPID);
      throw new ObjectValidityException("Digital Object Disseminator "
              + "refers to a Behavior Definition Object that does not exist "
              + "in the local repository: "
              + BDefPID);
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
    boolean rowFound = false;
    try
    {
      for (int i = 0; i < objectInfo.bMechPIDs.length; i++)
      {
        BMechPID = objectInfo.bMechPIDs[i];
        String  query =
            "SELECT "
            + "BehaviorMechanism.BMECH_DBID "
            + "FROM "
            + "BehaviorMechanism "
            + "WHERE "
            + "BehaviorMechanism.BMECH_PID=\'" + BMechPID + "\';";

        Statement statement = connection.createStatement();
        queryResult = statement.executeQuery(query);
        rowFound = queryResult.next();
        statement.close();
      }
    }
    catch (Throwable th)
    {
      throw new GeneralException("DOValidatorIntegrityCheck returned error. "
                + "The underlying error was a " + th.getClass().getName()
                + "The message was "  + "\"" + th.getMessage() + "\"");
    }
    if (!rowFound)
    {
      System.out.println("Digital Object Disseminator "
              + "refers to a Behavior Mechanism Object that does not exist "
              + "in the local repository: "
              + BMechPID);
      throw new ObjectValidityException("Digital Object Disseminator "
              + "refers to a Behavior Mechanism Object that does not exist "
              + "in the local repository: "
              + BMechPID);
    }
  }
}