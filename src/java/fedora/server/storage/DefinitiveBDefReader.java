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
import fedora.server.storage.service.ServiceMapper;
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
      doReader.getAbstractMethods(null);
      doReader.getAbstractMethodsXML(null);
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
    serviceMapper = new ServiceMapper();
  }

  public MethodDef[] getAbstractMethods(Date versDateTime)
    throws ObjectIntegrityException, RepositoryConfigurationException, GeneralException
  {
    return serviceMapper.getMethodDefs(
      new InputSource(new ByteArrayInputStream(
          ((DatastreamXMLMetadata)datastreamTbl.get("METHODMAP")).xmlContent)));
  }

  public InputStream getAbstractMethodsXML(Date versDateTime) throws GeneralException
  {
    return(new ByteArrayInputStream(
      ((DatastreamXMLMetadata)datastreamTbl.get("METHODMAP")).xmlContent));
  }

  private void printMethods(MethodDef[] methodDefs)
  {
    System.out.println("Printing Abstract Methods...");
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