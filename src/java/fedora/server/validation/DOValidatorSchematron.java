package fedora.server.validation;

/**
 * <p>Title: DOValidatorSchematron.java</p>
 * <p>Description: Schematron validation with FedoraRules schema as default.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette, payette@cs.cornell.edu
 * @version 1.0
 */

import fedora.server.errors.ObjectValidityException;
import fedora.server.errors.ServerException;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Properties;

// DOM classes
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

// TrAX classes
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.*;

import javax.xml.parsers.ParserConfigurationException;

public class DOValidatorSchematron
{

  /**
   * FOR TESTING: Configuration variables for Schematron Validation
   * (These are set via Server configuration and provided in constructors.)
   * These values are used for stand-alone testing via main();
   *
   */
  private static final String preprocessorID = "schematron/preprocessor.xslt";
  private static final String validatingStyleSheetID = "schematron/fedoraValidator.xslt";

  private StreamSource rulesSource;
  private StreamSource preprocessorSource;
  private StreamSource validatingStyleSheet;

  private StringBuffer string = new StringBuffer();

  public static void main(String[] args)
  {
    if (args.length < 2)
    {
      System.err.println("usage: java DOValidatorSchematron schemaLocation objectLocation workFlowPhase" + "\n" +
        "  schemaLocation: the file path of the Schematron schema to validate against" + "\n" +
        "  objectLocation: the file path of the object to be validated" + "\n" +
        "  workFlowPhase: {ingest|store} the phase of the object lifecycle to which validation pertains");
      System.exit(1);
    }

    try
    {
      DOValidatorSchematron dovs = new DOValidatorSchematron(args[0], args[2]);
      dovs.validate(new File(args[1]));
    }
    catch (ServerException e)
    {
      System.out.println("DOValidatorSchematraon caught ServerException in main().");
      System.out.println("Suppressing message since not attached to Server.");
    }
    catch (Exception e)
    {
      System.err.println("DOValidatorSchematron says Exception in main(): " + e.getMessage());
    }
  }

  public DOValidatorSchematron(String schemaID, String preprocessorID, String validatingStyleSheetID, String workFlowPhase)
      throws ObjectValidityException
  {
    validatingStyleSheet = setUp(preprocessorID, schemaID, validatingStyleSheetID, workFlowPhase);
  }

  public DOValidatorSchematron(String schemaID, String workFlowPhase)
      throws ObjectValidityException
  {
    validatingStyleSheet = setUp(preprocessorID, schemaID, validatingStyleSheetID, workFlowPhase);
  }

  public DOValidatorSchematron(InputStream schema, String workFlowPhase)
      throws ObjectValidityException
  {
    validatingStyleSheet = setUp(preprocessorID, schema, validatingStyleSheetID, workFlowPhase);
  }

  public void validate(File objectAsFile) throws ServerException
  {
    validate(new StreamSource(objectAsFile));
  }

  public void validate(InputStream objectAsStream) throws ServerException
  {
    validate(new StreamSource(objectAsStream));
  }

  public void validate(StreamSource objectSource) throws ServerException
  {
    DOValidatorSchematronResult result = null;
    try
    {
      // Create a transformer that uses the validating stylesheet
      TransformerFactory tfactory = TransformerFactory.newInstance();
      Transformer vtransformer = tfactory.newTransformer(validatingStyleSheet);

      // Run the Schematron validation of the Fedora object and output results in DOM format
      DOMResult validationResult = new DOMResult();
      vtransformer.transform(objectSource, validationResult);
      //vtransformer.transform(objectSource, new StreamResult(System.out));
      result = new DOValidatorSchematronResult(validationResult);
    }
    catch(TransformerException e)
    {
       System.err.println("Schematron validation: " + e.getMessage()) ;
       throw new ObjectValidityException(e.getMessage());
    }
    catch(Exception e)
    {
       System.err.println("Schematron validation: " + e.getMessage()) ;
       throw new ObjectValidityException(e.getMessage());
    }

    // Examine the validation results

    //String sr2 = result.serializeResult(new FileWriter("c:/mellon-test/work/test.out"));
    //System.out.println("MY SERIALIZE RESULT:");
    //System.out.println(sr2);

    if (!result.isValid())
    {
      String msg = null;
      try
      {
        System.out.println("The object is not valid according to the Schematron!");
        msg = result.getXMLResult();
        System.err.println(msg);
      }
      catch(Exception e)
      {
         System.err.println("Schematron validation: " + e.getMessage()) ;
         throw new ObjectValidityException(e.getMessage());
      }
      throw new ObjectValidityException(msg);
    }
  }

  private StreamSource setUp(String preprocessorID, String fedoraSchemaID, String validatingStyleSheetID, String workFlowPhase)
    throws ObjectValidityException
  {

    rulesSource = new StreamSource(fedoraSchemaID);
    preprocessorSource = new StreamSource(preprocessorID);
    return(createValidatingStyleSheet(rulesSource, preprocessorSource, validatingStyleSheetID, workFlowPhase));
  }

  private StreamSource setUp(String preprocessorID, InputStream fedoraSchema, String validatingStyleSheetID, String workFlowPhase)
    throws ObjectValidityException
  {

    rulesSource = new StreamSource(fedoraSchema);
    preprocessorSource = new StreamSource(preprocessorID);
    return(createValidatingStyleSheet(rulesSource, preprocessorSource, validatingStyleSheetID, workFlowPhase));
  }

  private StreamSource createValidatingStyleSheet(
    StreamSource rulesSource, StreamSource preprocessorSource, String validatingStyleSheetID, String workFlowPhase)
    throws ObjectValidityException
  {
    try
    {
      System.out.println("CREATING NEW VALIDATING STYLESHEET FOR WORKFLOW PHASE: " + workFlowPhase);
      // Create a transformer for that uses the Schematron preprocessor stylesheet.
      TransformerFactory tfactory = TransformerFactory.newInstance();
      Transformer ptransformer = tfactory.newTransformer(preprocessorSource);
      ptransformer.setParameter("phase", workFlowPhase);

      // Transform the Schematron schema (rules) into a validating stylesheet
      // that will be written to a file in the system-configured location
      ptransformer.transform(rulesSource, new StreamResult(new File(validatingStyleSheetID)));
    }
    catch(TransformerException e)
    {
       System.err.println("Schematron validation: " + e.getMessage()) ;
       throw new ObjectValidityException(e.getMessage());
    }
    return(new StreamSource(new File(validatingStyleSheetID)));
  }
}