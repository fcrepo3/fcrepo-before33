package fedora.server.storage;

/**
 * <p>Title: DefinitiveBMechReader.java</p>
 * <p>Description: A Reader for Fedora Behavior Mechanism Objects. Since
 * a Behavior Mechanism Object can be treated like any other Fedora
 * object, the DefinitiveBMechReader extends the functionality of the
 * DefinitiveDOReader. The DefinitiveBMechReader can provide a list
 * of behavior methods for the service that the Behavior Mechanism Object
 * represents.  The method definitions and bindings are drawn out of the
 * WSDL datastream in the Behavior Mechanism Object.
 * WARNING!!  This code ignores versioning on datastreams!! </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette payette@cs.cornell.edu
 * @version 1.0
 */

import fedora.server.storage.types.*;
import fedora.server.errors.*;
import fedora.server.storage.service.*;
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class DefinitiveBMechReader extends DefinitiveDOReader implements BMechReader
{
  private ServiceMapper serviceMapper;

  public static void main(String[] args)
  {
    if (args.length == 0)
    {
      System.err.println("provide args: [0]=debug(true/false) [1]=PID");
      System.exit(1);
    }
    debug = (args[0].equalsIgnoreCase("true")) ? true : false;
    try
    {
      // FOR TESTING...
      DefinitiveBMechReader doReader = new DefinitiveBMechReader(null, args[1]);
      doReader.GetObjectPID();
      doReader.GetObjectLabel();
      doReader.ListDatastreamIDs("A");
      doReader.ListDisseminatorIDs("A");
      doReader.GetDatastreams(null);
      Datastream[] dsArray = doReader.GetDatastreams(null);
      doReader.GetDatastream(dsArray[0].DatastreamID, null);
      doReader.GetDisseminators(null);
      String[] bdefArray = doReader.GetBehaviorDefs(null);
      doReader.getObjectMethods(bdefArray[0], null);
      doReader.GetDSBindingMaps(null);
      // BMech reader methods
      doReader.getServiceMethods(null);
      doReader.getServiceMethodsXML(null);
      doReader.printMethods(doReader.getServiceMethodBindings(null));
      doReader.printDSInputSpec(doReader.getServiceDSInputSpec(null));
    }
    catch (ServerException e)
    {
      System.err.println("FEDORA EXCEPTION: suppressing print of ServerException");
    }
    catch (Exception e)
    {
      System.err.println("NON-FEDORA EXCEPTION:)" + e.toString());
    }
  }

  public DefinitiveBMechReader(DefaultDOManager mgr, String objectPID) throws ServerException
  {
    super(mgr, objectPID);
    serviceMapper = new ServiceMapper();
  }

  /**
   * Get the set of method definitions.
   * Specifically, these are abstract service operations that are defined in the WSDL
   * datastream of the Behavior Mechanism Object.
   * @param versDateTime
   * @return  an array of method definitions
   * @throws GeneralException
   */
  public MethodDef[] getServiceMethods(Date versDateTime)
    throws ObjectIntegrityException, RepositoryConfigurationException, GeneralException
  {
    return serviceMapper.getMethodDefs(
      new InputSource(new ByteArrayInputStream(
          ((DatastreamXMLMetadata)datastreamTbl.get("METHODMAP")).xmlContent)));
  }

  public MethodParmDef[] getServiceMethodParms(String methodName, Date versDateTime)
        throws GeneralException, ServerException
  {
    return getParms(this.getServiceMethods(versDateTime), methodName);
  }

  /**
   * Get the parms out of a particular service method definition.
   * @param methods
   * @return
   */
   private MethodParmDef[] getParms(MethodDef[] methods, String methodName)
    throws MethodNotFoundException, ServerException
   {
      for (int i=0; i<methods.length; i++)
      {
        if (methods[i].methodName.equalsIgnoreCase(methodName))
        {
          return methods[i].methodParms;
        }
      }
      throw new MethodNotFoundException("[getParms] The behavior mechanism object, " + PID
                  + ", does not have a service method named '" + methodName);
   }

  /**
   * Get the set of method definitions with binding information.
   * Specifically, these are concrete service operations that are defined in the WSDL
   * datastream of the Behavior Mechanism Object.
   * @param versDateTime
   * @return  an array of method bindings
   * @throws GeneralException
   */
  public MethodDefOperationBind[] getServiceMethodBindings(Date versDateTime)
    throws ObjectIntegrityException, RepositoryConfigurationException, GeneralException
  {
    return serviceMapper.getMethodDefBindings(
      new InputSource(new ByteArrayInputStream(
          ((DatastreamXMLMetadata)datastreamTbl.get("WSDL")).xmlContent)),
      new InputSource(new ByteArrayInputStream(
          ((DatastreamXMLMetadata)datastreamTbl.get("METHODMAP")).xmlContent)));
  }


  /**
   * Get the datastream input specification that defines the datastream requirements
   * for Data Objects that will associate with this Behavior Mechanism Object.
   * It can be seen as a "contract" between the Behavior Mechanism Object and any
   * Data Objects that use the service that the Behavior Mechanism Object represents.
   * Specifically, the binding specification defines what kinds of datastreams
   * should be made available to the service that this Behavior Mechanism Object
   * represents.  It defines an abstract perspective of the contents of a
   * Data Object from the perspective of the service that will process the
   * contents of the Data Object.  The datastream binding specification
   * will define a formal name for each kind of datastream input (i.e., a binding key),
   * acceptable MIME types for those datastreams, and the cardinality and ordinality
   * for those datastreams.  The datastream binding specification can be used
   * to facilitate the creation of Disseminators on Data Objects, or to validate
   * the relationships between Data Objects and Behavior Mechanism Objects.
   * @param versDateTime
   * @return  the datastream input specification for the Behavior Mechansim Object
   * @throws GeneralException
   */
  public BMechDSBindSpec getServiceDSInputSpec(Date versDateTime)
    throws ObjectIntegrityException, RepositoryConfigurationException, GeneralException
  {
    return serviceMapper.getDSInputSpec(
      new InputSource(new ByteArrayInputStream(
          ((DatastreamXMLMetadata)datastreamTbl.get("DSINPUTSPEC")).xmlContent)));
  }

  /**
   * Get the set of method definitions that are implemented by the service
   * that this Behavior Mechanism Object represents.  In this case, the
   * method defintions will be returned as a stream of XML that is encoded
   * in accordance with the Fedora Method Map schema.
   * @param versDateTime
   * @return  an input stream that is XML encoded to the Fedora Method Map schema
   * @throws GeneralException
   */
  public InputStream getServiceMethodsXML(Date versDateTime) throws GeneralException
  {
    return(new ByteArrayInputStream(
      ((DatastreamXMLMetadata)datastreamTbl.get("METHODMAP")).xmlContent));
  }

  /**
   * Print the method def bindings for the service represented by
   * the Behavior Mechanism Object.
   */
  private void printMethods(MethodDefOperationBind[] methodDefBindings)
  {
    System.out.println("Printing Service Methods...");
    for (int i = 0; i < methodDefBindings.length; i++)
    {
      System.out.println("METHOD: " + i);
      System.out.println("  method[" + i + "]=" + methodDefBindings[i].methodName);
      System.out.println("  protocol[" + i + "]=" + methodDefBindings[i].protocolType);
      System.out.println("  service address[" + i + "]=" + methodDefBindings[i].serviceBindingAddress);
      System.out.println("  operation loc[" + i + "]=" + methodDefBindings[i].operationLocation);
      System.out.println("  operation URL[" + i + "]=" + methodDefBindings[i].operationURL);
      for (int j = 0; j < methodDefBindings[i].methodParms.length; j++)
      {
        System.out.println(
              "  parm[" + j + "]=" +
              methodDefBindings[i].methodParms[j].parmName +
              " parmType=" + methodDefBindings[i].methodParms[j].parmType +
              " defaultValue=" + methodDefBindings[i].methodParms[j].parmDefaultValue +
              " required=" + methodDefBindings[i].methodParms[j].parmRequired);

        for (int j2 = 0; j2 < methodDefBindings[i].methodParms[j].parmDomainValues.length; j2++)
        {
          System.out.println(
              "  parmDomainValue[" + j2 + "]=" +
              methodDefBindings[i].methodParms[j].parmDomainValues[j2]);
        }
      }
      for (int k = 0; k < methodDefBindings[i].dsBindingKeys.length; k++)
      {
        System.out.println("  dsBindingKey[" + k + "]=" + methodDefBindings[i].dsBindingKeys[k]);
      }
    }
  }

  /**
   * Print the datastream binding specification for the service
   * represented by the Behavior Mechanism Object.
   */
  private void printDSInputSpec(BMechDSBindSpec dsBindSpec)
  {
    System.out.println("Printing DS Input Specification...");
    for (int i = 0; i < dsBindSpec.dsBindRules.length; i++)
    {
      System.out.println(">>>>>Binding[" + i + "].bindKey = " + dsBindSpec.dsBindRules[i].bindingKeyName);
      System.out.println(">>>>>Binding[" + i + "].instruct = " + dsBindSpec.dsBindRules[i].bindingInstruction);
      System.out.println(">>>>>Binding[" + i + "].label = " + dsBindSpec.dsBindRules[i].bindingLabel);
      System.out.println(">>>>>Binding[" + i + "].max = " + dsBindSpec.dsBindRules[i].maxNumBindings);
      System.out.println(">>>>>Binding[" + i + "].min = " + dsBindSpec.dsBindRules[i].minNumBindings);
      System.out.println(">>>>>Binding[" + i + "].ordin = " + dsBindSpec.dsBindRules[i].ordinality);
    }
  }
}