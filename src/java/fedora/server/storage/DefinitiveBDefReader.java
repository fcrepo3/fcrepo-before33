package fedora.server.storage;

/**
 * <p>Title: DefinitiveBDefReader.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import fedora.server.storage.types.*;
import fedora.server.errors.*;
import java.util.Date;
import java.util.Vector;
import java.util.StringTokenizer;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class DefinitiveBDefReader extends DefinitiveDOReader implements BDefReader
{

  private MethodDef[] methodDefs;

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
      DefinitiveBDefReader doReader = new DefinitiveBDefReader(null, args[1]);
      doReader.GetObjectPID();
      doReader.GetObjectLabel();
      doReader.ListDatastreamIDs("A");
      doReader.ListDisseminatorIDs("A");
      doReader.GetDatastreams(null);
      Datastream[] dsArray = doReader.GetDatastreams(null);
      doReader.GetDatastream(dsArray[0].DatastreamID, null);
      doReader.GetDisseminators(null);
      doReader.GetBehaviorDefs(null);
      Disseminator d = doReader.GetDisseminator("DISS1", null);
      doReader.GetBMechMethods(d.bDefID, null);
      doReader.GetDSBindingMaps(null);
      // Bdef reader methods
      doReader.GetBehaviorMethods(null);
      doReader.GetBehaviorMethodsWSDL(null);
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

  public DefinitiveBDefReader(DefaultDOManager mgr, String objectPID) throws ServerException
  {
    super(mgr, objectPID);
  }

  public MethodDef[] GetBehaviorMethods(Date versDateTime) throws GeneralException
  {
    try
    {
      DatastreamXMLMetadata wsdlDS = (DatastreamXMLMetadata)datastreamTbl.get("WSDL");
      InputSource wsdlXML = new InputSource(new ByteArrayInputStream(wsdlDS.xmlContent));

      // reset the xmlreader of superclass to the specical WSDLEventHandler
      WSDLBehaviorDeserializer wsdl = new WSDLBehaviorDeserializer();
      xmlreader.setContentHandler(wsdl);
      xmlreader.setFeature("http://xml.org/sax/features/namespaces", false);
      xmlreader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
      xmlreader.parse(wsdlXML);
      methodDefs = wsdl.methodDefs;
      if (debug) printBehaviorMethods();
    }
    catch (Exception e)
    {
      System.err.println("DefinitiveBDefReader: " + e.toString());
      throw new GeneralException("DefinitiveBDefReader.GetBehaviorMethods: " + e.getMessage());
    }
    return(methodDefs);
  }

  public InputStream GetBehaviorMethodsWSDL(Date versDateTime) throws GeneralException
  {
    DatastreamXMLMetadata wsdlDS = (DatastreamXMLMetadata)datastreamTbl.get("WSDL");
    InputStream wsdl = new ByteArrayInputStream(wsdlDS.xmlContent);
    return(wsdl);
  }

  private void printBehaviorMethods()
  {
    System.out.println("Printing Behavior Methods...");
    for (int i = 0; i < methodDefs.length; i++)
    {
      System.out.println("METHOD: " + i);
      System.out.println("  method[" + i + "]=" + methodDefs[i].methodName);
      for (int j = 0; j < methodDefs[i].methodParms.length; j++)
      {
        System.out.println(
              "  parm[" + j + "]=" +
              methodDefs[i].methodParms[j].parmName +
              " parmType=" + methodDefs[i].methodParms[j].parmType +
              " defaultValue=" + methodDefs[i].methodParms[j].parmDefaultValue +
              " required=" + methodDefs[i].methodParms[j].parmRequired);

        for (int j2 = 0; j2 < methodDefs[i].methodParms[j].parmDomainValues.length; j2++)
        {
          System.out.println(
              "  parmDomainValue[" + j2 + "]=" +
              methodDefs[i].methodParms[j].parmDomainValues[j2]);
        }
      }
    }
  }
}