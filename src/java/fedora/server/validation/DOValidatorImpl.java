package fedora.server.validation;

/**
 * <p><b>Title: DOValidatorImpl.java </b></p>
 * <p><b>Description: </b>The implementation of the digital object validation
 * module (see DOValidator.class and DOValidatorModule.class).  Digital
 * object validation is implemented as three levels:
 * <pre>
 *   - Level 1:  XML Schema Validation - the digital object will be
 *   validated against the METS XML Schema.  An ObjectValidityException
 *   will be thrown if the object fails the schema test.
 *   - Level 2: Schematron Rules Validation - the digital object
 *   will be validated against a set of rules express by a Schematron
 *   schema.  These rules are Fedora-specific and are beyond what
 *   is expressed in the METS XML Schema.  The Schematron schema expressed
 *   rules for different phases of the object lifecycle.  There are rules
 *   appropriate to a digital object when it is first ingested into the
 *   repository (essentially rules for a valid "ingest package."  There are
 *   additional rules that must be met before a digital object is considered
 *   valid to be stored in the repository.  These rules pertain to aspects
 *   of the object that are system assigned, such as created dates and
 *   state codes. An ObjectValidityException will be thrown if the object
 *   fails the Fedora rules test.
 *   - Level 3:  Referential Integrity Checking - the digital object
 *   will be validated programmatically to check that certain relationships
 *   expressed in the object are valid.  Disseminators will be checked to
 *   make sure that Behavior Defintion and Behavior Mechanism objects that
 *   are referred to actually exist in the local repository.  (In the future
 *   some degree of link checking may be performed on datastreasm, plus
 *   other TBD checks.) An ObjectValidityException will be thrown if the
 *   object fails the integrity checks.</p>
 * </pre>
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
// Fedora imports
import fedora.server.errors.ServerException;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ObjectValidityException;
import fedora.server.storage.ConnectionPool;
import java.sql.Connection;

// Java imports
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

// SAX imports
import org.xml.sax.SAXException;

// TrAX classes
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

public class DOValidatorImpl implements DOValidator
{
    protected static boolean debug = true;

   // FOR TESTING: The configuration variables are normally set via
   // DOValidatorModule initialization, but they are set with some default
   // values so that this class can be tested stand-alone via main().

   /** Configuration variable: tempdir is a working area for validation */
    protected static String tempDir = "temp/";

    /**
     * Configuration variable: xmlSchemaURL is the URL of the METS XML Schema
     */
    protected static String xmlSchemaURL =
      "http://www.cs.cornell.edu/payette/mellon/fedora/mets-fedora-ext.xsd";

    /**
     *  Configuration variable: schematronPreprocessorID is the Schematron
     *  stylesheet that is used to transform a Schematron schema into a
     *  validating stylesheet based on the rules in the schema.
     */
    protected static String schematronPreprocessorID = "schematron/preprocessor.xslt";

    /**
     *  Configuration variable: schematronSchemaID is the Schematron
     *  schema that expressed Fedora-specific validation rules.
     *  It is transformed into a validating stylesheet by the Schematron
     *  preprocessing stylesheet.
     */
    protected static String schematronSchemaID = "schematron/fedoraRules.xml";

    /**
     * Configuration variable: connectionPool is a database connection
     *  pool to be used by the validation module for miscellaneous lookups.
     */
    protected static ConnectionPool connectionPool = null;

    // FOR TESTING: Class variables to get a database ConnectionPool
    // during unit testing when Server is not instantiated.
    private static int minConnections = 10;
    private static int maxConnections = 100;
    private static String jdbcDriverClass = "org.gjt.mm.mysql.Driver";
    private static String dbUsername = null;
    private static String dbPassword = null;
    private static String jdbcURL = "jdbc:mysql://localhost/FedoraObjects";

    /**
     *  Digital object variables that are required to support
     *  Level 3 validation (Integrity).  These are picked up
     *  by a content handler during XML Schema validation so the
     *  digital object does not have to be re-parsed when these
     *  variables are needed.
     */
    private DOIntegrityVariables iVars = null;

    public static void main(String[] args)
    {
      if (args.length < 2)
      {
        System.err.println("usage: java DOValidatorImpl objectLocation validationLevel " +
          "workFlowPhase dbUsername dbPassword" + "\n" +
          "  objectLocation: the file path of the object to be validated" + "\n" +
          "  validationLevel: {0|1|2|3} where" + "\n" +
          "      0=all validation, 1=xmlSchema only, 2=schematron only, 3=referential integrity only" + "\n" +
          "  workFlowPhase: {ingest|store} the phase of the object lifecycle to which validation pertains" + "\n" +
          "  dbUsername: userName for the FedoraObjects database" + "\n" +
          "  dbPassword: password for the FedoraObjects database");
        System.exit(1);
      }

      try
      {
        // Get a database connection in the absence of a Server instance
        // which would be available in DOValidatorModule.
        dbUsername = args[3];
        dbPassword = args[4];
        connectionPool = new ConnectionPool(
                          jdbcDriverClass, jdbcURL, dbUsername,
                          dbPassword, minConnections, maxConnections, true);

        File objectAsFile = new File((String)args[0]);
        DOValidatorImpl dov = new DOValidatorImpl();
        dov.validate(new FileInputStream(objectAsFile),
            new Integer(args[1]).intValue(), args[2]);
      }
      catch (ObjectValidityException e)
      {
        System.out.println("DOValidatorImpl caught ObjectValidityException.");
        System.out.println("Suppressing message since not attached to Server.");
      }
      catch (ServerException e)
      {
        System.out.println("DOValidatorImpl caught ServerException.");
        System.out.println("Suppressing message since not attached to Server.");
      }
      catch (Throwable th)
      {
        System.out.println("DOValidatorImpl returned error in main(). "
                  + "The underlying error was a " + th.getClass().getName() + ".  "
                  + "The message was "  + "\"" + th.getMessage() + "\"");
      }
      System.exit(1);

    }

  /**
   * <p>Constructs a new DOValidatorImpl to support all forms of
   * digital object validation, using defaults for all configuration
   * values.</p>
   *
   * @throws ServerException If construction fails for any reason.
   */
    public DOValidatorImpl() throws ServerException
    {
    }

  /**
   * <p>Constructs a new DOValidatorImpl to support all forms of
   * digital object validation, using specified values for
   * configuration values.</p>
   * <p>
   * Any parameter may be given as null, in which case the default
   * value is assumed.
   * </p>
   * @param tempDir Working area for validation, default is <i>temp/</i>
   * @param xmlSchemaURL URL to METS-Fedora WXS (W3 Schema), default is
   *        <i>http://www.cs.cornell.edu/payette/mellon/fedora/mets-fedora.xsd</i>
   * @param schematronPreprocessorID Local location of Schematron
   *        rules-to-stylesheet stylesheet, default is
   *        <i>schematron/preprocessor.xslt</i>
   * @param schematronSchemaID Local location of Fedora Schematron rules,
   *        default is <i>schematron/fedoraRules.xml</i>
   * @param connectionPool For level3 validation, connectionpool to db holding
   *        Fedora objects, default is null.
   * @throws ServerException If construction fails for any reason.
   */
    public DOValidatorImpl(String tempDir, String xmlSchemaURL,
            String schematronPreprocessorID, String schematronSchemaID,
            ConnectionPool connectionPool)
            throws ServerException
    {
        if (tempDir!=null) this.tempDir=tempDir;
        if (xmlSchemaURL!=null) this.xmlSchemaURL=xmlSchemaURL;
        if (schematronPreprocessorID!=null) this.schematronPreprocessorID=schematronPreprocessorID;
        if (schematronSchemaID!=null) this.schematronSchemaID=schematronSchemaID;
        if (connectionPool!=null) this.connectionPool=connectionPool;
    }

  /**
   * <p>Validates a digital object.</p>
   *
   * @param objectAsStream The digital object provided as a bytestream.
   * @param validationLevel The level of validation to perform on the digital object.
   *        This is an integer from 0-3 with the following meanings:
   *        0 = do all validation levels
   *        1 = perform only XML Schema validation
   *        2 = perform only Schematron Rules validation
   *        3 = perform only referential integrity checks for the object
   *
   * @param workFlowPhase The stage in the work flow for which the validation should be contextualized.
   *        "ingest" = the object is in the submission format for the ingest stage phase
   *        "store" = the object is in the authoritative format for the final storage phase
   *
   * @throws ObjectValidityException If validation fails for any reason.
   * @throws GeneralException If validation fails for any reason.
   */
    public void validate(InputStream objectAsStream, int validationLevel, String workFlowPhase)
      throws ObjectValidityException, GeneralException
    {
      // FIXIT!!: We need to use the object Inputstream twice, once for XML Schema validation
      // and once for Schematron validation.  We may want to consider implementing some form
      // of a rewindable InputStream. For now, I will just write the object InputStream to
      // disk so I can read it multiple times.
      try
      {
        System.out.println("TEMP FILE DIR:" + tempDir);
        File objectAsFile = streamtoFile(tempDir, objectAsStream);
        validate(objectAsFile, validationLevel, workFlowPhase);
      }
      catch (ObjectValidityException e)
      {
        throw e;
      }
      catch (GeneralException e)
      {
        throw e;
      }
      catch (Exception e)
      {
        System.out.println("DOValidatorImpl caught Exception. Re-throwing as GeneralException: " + e.getMessage());
        throw new GeneralException(e.getMessage());
      }
    }

  /**
   * <p>Validates a digital object.</p>
   *
   * @param objectAsFile The digital object provided as a file.
   * @param validationLevel The level of validation to perform on the digital object.
   *        This is an integer from 0-3 with the following meanings:
   *        0 = do all validation levels
   *        1 = perform only XML Schema validation
   *        2 = perform only Schematron Rules validation
   *        3 = perform only referential integrity checks for the object
   * @param workFlowPhase The stage in the work flow for which the validation should be contextualized.
   *        "ingest" = the object is in the submission format for the ingest stage phase
   *        "store" = the object is in the authoritative format for the final storage phase
   * @throws ObjectValidityException If validation fails for any reason.
   * @throws GeneralException If validation fails for any reason.
   */
    public void validate(File objectAsFile, int validationLevel, String workFlowPhase)
      throws ObjectValidityException, GeneralException
    {
      switch ( validationLevel )
      {
      // ALL forms of validation
      case 0:
        // FIXME: The order is swapped here as a temporary fix to the
        // xsd schemaLocation problem.  The schemaLocation problem manifests
        // itself when a remote server hangs as a result of a request that
        // the W3C Schema validation code makes as a result of seeing a
        // schemaLocation element.  Here, level 1 is run *after* L2,
        // so that L1 won't load schemas from schemaLocation attributes.
        // (L2 ensures that there are no non-root schemaLocation elements)
        validate_L2(objectAsFile, schematronSchemaID,
                    schematronPreprocessorID, workFlowPhase);
        validate_L1(objectAsFile);
        validate_L3(objectAsFile);
        break;

      // XML Schema Validation only
      case 1:
        // FIXME: For the time being, this shouldn't be run without having
        // first run L2 validation, or you risk hanging the running thread.
        // This is due to the schemaLocation problem mentioned above.
        validate_L1(objectAsFile);
        break;

      // Schematron Rules Validation only
      case 2:
        validate_L2(objectAsFile, schematronSchemaID,
                    schematronPreprocessorID, workFlowPhase);
        break;

      // Referential Integrity Checks only
      case 3:
        validate_L3(objectAsFile);
        break;

      // Default: if no validation level specified, throw exception
      default:
        cleanUp(objectAsFile);
        throw new GeneralException("DOValidatorImpl.validate has invalid validationLevel: "
            + validationLevel);
      }
      cleanUp(objectAsFile);
    }

    /**
     * Do the Level 1 validation of a Fedora object.  Level 1 is XML Schema
     * validation using the Fedora extension of the METS XML schema.
     * @param objectAsFile The digital object provided as a file.
     * @throws ObjectValidityException If validation fails for any reason.
     * @throws GeneralException If validation fails for any reason.
     */
    private void validate_L1(File objectAsFile)
      throws ObjectValidityException, GeneralException
    {
      // Level 1: METS rules validation using XML Schema
      System.out.println("Validating object to Level 1 (METS/XML SCHEMA) ...");
      try
      {
        DOValidatorXMLSchema xsv = new DOValidatorXMLSchema(xmlSchemaURL);
        xsv.validate(objectAsFile);
        iVars = xsv.getDOIntegrityVariables();
      }
      catch (ObjectValidityException e)
      {
        cleanUp(objectAsFile);
        throw e;
      }
      catch (GeneralException e)
      {
        cleanUp(objectAsFile);
        throw e;
      }
      catch (Exception e)
      {
        cleanUp(objectAsFile);
        throw new GeneralException(e.getMessage());
      }
      System.out.println("Level 1 Validation OK (METS/XMLSchema).");
    }

    /**
     * Do Level 2 Validation on the Fedora object.  Level 2 is Schematron
     * validation using a set of rules expressed in a Schematron schema for
     * Fedora objects.
     *
     * @param objectAsFile The digital object provided as a file.
     * @param schemaID Local location of Fedora Schematron rules.
     * @param preprocessorID Local location of Schematron rules-to-stylesheet
     *                       stylesheet
     * @param phase The validation phase.
     * @throws ObjectValidityException If validation fails for any reason.
     * @throws GeneralException If validation fails for any reason.
     */
    private void validate_L2(File objectAsFile, String schemaID,
      String preprocessorID, String phase)
      throws ObjectValidityException, GeneralException
    {
      // Level 2: Fedora rules validation using Schematron
      System.out.println("Validating object to Level 2 (FEDORA/SCHEMATRON) ...");
      try
      {
        DOValidatorSchematron validator =
          new DOValidatorSchematron(schemaID, preprocessorID, phase);
        validator.validate(objectAsFile);
      }
      catch (ObjectValidityException e)
      {
        cleanUp(objectAsFile);
        throw e;
      }
      catch (GeneralException e)
      {
        cleanUp(objectAsFile);
        throw e;
      }
      catch (Exception e)
      {
        cleanUp(objectAsFile);
        throw new GeneralException(e.getMessage());
      }
      System.out.println("Level 2 Validation OK (FEDORA/SCHEMATRON).");
    }

    /**
     * Do Level 3 Validation on the Fedora object.  Level 3 is programmatic
     * validation that will perform various referential integrity checks.
     * Currently, the only checks that are implemented are ensuring that
     * a data object references behavior definition and mechanisms objects
     * that already exist in the repository.  Other checks will be implemented
     * in the future.
     * @param objectAsFile The digital object provided as a file.
     * @throws ObjectValidityException If validation fails for any reason.
     * @throws GeneralException If validation fails for any reason.
     */
    private void validate_L3(File objectAsFile)
      throws ObjectValidityException, GeneralException
    {
      System.out.println("Validating object to Level 3 (INTEGRITY) ...");
      DOValidatorIntegrityCheck iChecker = null;
      Connection dbConnection = dbConnect(connectionPool);
      try
      {
        if (iVars == null)
        {
          iChecker = new DOValidatorIntegrityCheck(objectAsFile, xmlSchemaURL, dbConnection);
        }
        else
        {
          iChecker = new DOValidatorIntegrityCheck(iVars, dbConnection);
        }
        iChecker.validate();
        dbDisconnect(connectionPool, dbConnection);
      }
      catch (ObjectValidityException e)
      {
        cleanUp(objectAsFile);
        throw e;
      }
      catch (GeneralException e)
      {
        cleanUp(objectAsFile);
        throw e;
      }
      catch (Exception e)
      {
        cleanUp(objectAsFile);
        throw new GeneralException(e.getMessage());
      }
      finally {
        connectionPool.free(dbConnection);
      }
      System.out.println("Level 3 Validation OK (INTEGRITY).");
      return;
    }

    private void streamCopy(InputStream in, OutputStream out)
      throws IOException
    {
      int bufferSize = 512;
      byte[] buffer= new byte[bufferSize];
      int bytesRead = 0;
      while ((bytesRead = in.read(buffer, 0, bufferSize)) != -1)
      {
              out.write(buffer,0,bytesRead);
      }
      in.close();
      out.close();
    }

    private File streamtoFile(String dirname, InputStream objectAsStream)
        throws IOException
    {
        File objectAsFile = null;
        try
        {
          File tempDir = new File(dirname);
          String fileLocation = null;

          if ( tempDir.exists() || tempDir.mkdirs() )
          {
              fileLocation = tempDir.toString() + File.separator
                            + System.currentTimeMillis() + ".tmp";
              FileOutputStream fos = new FileOutputStream(fileLocation);
              streamCopy(objectAsStream, fos);
              objectAsFile = new File(fileLocation);
          }
        }
        catch (IOException e)
        {
          if (objectAsFile.exists())
          {
            objectAsFile.delete();
          }
          throw e;
        }
        return(objectAsFile);
    }

    private Connection dbConnect(ConnectionPool pool) throws GeneralException
    {
      try
      {
        // Get a database connection so we can query the FedoraObjects database
        return pool.getConnection();
      }
      catch (Throwable th)
      {
        throw new GeneralException("Digital Object Validator returned error: ("
            + th.getClass().getName() + ") - "  + th.getMessage());
      }
    }

    private void dbDisconnect(ConnectionPool pool, Connection connection)
      throws ServerException
    {
      try
      {
        pool.free(connection);
        connection.close();
      }
      catch (Throwable th)
      {
        throw new GeneralException("Digital Object Validator returned error: ("
            + th.getClass().getName() + ") - " + th.getMessage());
      }
    }

    // FIXIT!!  This is a hack for now.  I want to distinguish temporary object files
    // from real object files that were passed in for validation.  This is a bit
    // ugly as it stands, but it should only blow away files in the temp directory.
    private void cleanUp(File f)
    {
      if (f.getParentFile() != null)
      {
        if (((new File(tempDir)).getAbsolutePath()).equalsIgnoreCase(
            ((f.getParentFile()).getAbsolutePath())))
        {
          System.out.println("Deleting temporary file: " + f.getAbsolutePath());
          f.delete();
        }
      }
    }
}