package fedora.server.storage.service;

/**
 * <p>Title: ServiceMapper.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette  payette@cs.cornell.edu
 * @version 1.0
 */

import fedora.server.storage.types.*;
import fedora.server.errors.*;

import java.util.Vector;
import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class ServiceMapper
{
  private WSDLParser wsdlHandler;
  private MmapParser methodMapHandler;
  private DSInputSpecParser dsInputSpecHandler;

  public static void main(String[] args)
  {
    if (args.length < 2)
    {
      System.err.println("usage: java ServiceMapper wsdlLocation methodMapLocation " + "\n" +
        "  wsdlLocation: the file path of the wsdl to be parsed" + "\n" +
        "  methodMapLocation: the file path of the method map to be parsed." + "\n" +
        "  dsInputSpecLocation: the file path of the datastream input spec to be parsed.");
      System.exit(1);
    }
    try
    {
      ServiceMapper mapper = new ServiceMapper();
      InputSource wsdl =  new InputSource(new FileInputStream(new File((String)args[0])));
      InputSource mmap =  new InputSource(new FileInputStream(new File((String)args[1])));
      InputSource dsSpec = new InputSource(new FileInputStream(new File((String)args[2])));
      MethodDef[] methods = mapper.getMethodDefs(mmap);
      MethodDefOperationBind[] methodBindings = mapper.getMethodDefBindings(wsdl, mmap);
      BMechDSBindSpec dsInputSpec = mapper.getDSInputSpec(dsSpec);
      System.out.println("END TEST VIA MAIN()");
    }
    catch (ServerException e)
    {
      System.out.println("ServiceMapper caught ServerException.");
      System.out.println("Suppressing message since not attached to Server.");
    }
    catch (Throwable th)
    {
      System.out.println("ServiceMapper returned error in main(). "
                + "The underlying error was a " + th.getClass().getName() + ".  "
                + "The message was "  + "\"" + th.getMessage() + "\"");
    }
    System.exit(1);

  }

  public ServiceMapper()
  {
  }

  /**
   * getMethodDefs:  creates an array of abstract method definitions in the
   * form of an array of Fedora MethodDef objects.   The creation
   * of a MethodDef object requires information from a Fedora Method Map.
   *
   * @param  methodMapSource : Fedora Method Map definition for methods
   * @return MethodDef[] : an array of abstract method definitions
   * @throws ObjectIntegrityException
   * @throws RepositoryConfigurationException
   * @throws GeneralException
   */
  public MethodDef[] getMethodDefs(InputSource methodMapSource)
    throws ObjectIntegrityException,
    RepositoryConfigurationException, GeneralException
  {
    return getMethodMap(methodMapSource).mmapMethods;
  }

  /**
   * getMethodDefBindings:  creates an array of operation bindings in the
   * form of an array of Fedora MethodDefOperationBind objects.   The creation
   * of a MethodDefOperationBind object requires information from a WSDL
   * service definition and a related Fedora Method Map.  The Fedora Method Map
   * is merged with the WSDL to provide a Fedora-specific view of the WSDL.
   *
   * @param  wsdlSource : WSDL service definition for methods
   * @param  methodMapSource : Fedora Method Map definition for methods
   * @return MethodDefOperationBind[] : an array of method bindings
   * @throws ObjectIntegrityException
   * @throws RepositoryConfigurationException
   * @throws GeneralException
   */
  public MethodDefOperationBind[] getMethodDefBindings(InputSource wsdlSource, InputSource methodMapSource)
    throws ObjectIntegrityException,
    RepositoryConfigurationException, GeneralException
  {
    return merge(getService(wsdlSource), getMethodMap(methodMapSource));
  }

  public BMechDSBindSpec getDSInputSpec(InputSource dsInputSpecSource)
    throws ObjectIntegrityException,
    RepositoryConfigurationException, GeneralException
  {
    if (dsInputSpecHandler == null)
    {
      dsInputSpecHandler = (DSInputSpecParser)
        parse(dsInputSpecSource, new DSInputSpecParser());
    }
    return dsInputSpecHandler.getServiceDSInputSpec();
  }

  private Mmap getMethodMap(InputSource methodMapSource)
    throws ObjectIntegrityException,
    RepositoryConfigurationException, GeneralException
  {
    if (methodMapHandler == null)
    {
      methodMapHandler = (MmapParser)parse(methodMapSource, new MmapParser());
    }
    return methodMapHandler.getMethodMap();
  }

  private Service getService(InputSource wsdlSource)
    throws ObjectIntegrityException,
    RepositoryConfigurationException, GeneralException
  {
    if (wsdlHandler ==  null)
    {
      wsdlHandler =(WSDLParser)parse(wsdlSource, new WSDLParser());
    }
    return wsdlHandler.getService();
  }

  private DefaultHandler parse(InputSource xml, DefaultHandler eventHandler)
    throws ObjectIntegrityException,
    RepositoryConfigurationException, GeneralException
  {
    try
    {
      // XMLSchema validation via SAX parser
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setNamespaceAware(true);
      spf.setValidating(false);
      SAXParser sp = spf.newSAXParser();
      DefaultHandler handler = eventHandler;
      XMLReader xmlreader = sp.getXMLReader();
      xmlreader.setContentHandler(handler);
      xmlreader.parse(xml);
      return handler;
      }
      catch (ParserConfigurationException e)
      {
        String msg = "ServiceMapper returned parser error. "
                  + "The underlying exception was a " + e.getClass().getName() + ".  "
                  + "The message was "  + "\"" + e.getMessage() + "\"";
        System.out.println(msg);
        throw new RepositoryConfigurationException(msg);
      }
      catch (SAXException e)
      {
        String msg = "ServiceMapper returned SAXException. "
                  + "The underlying exception was a " + e.getClass().getName() + ".  "
                  + "The message was "  + "\"" + e.getMessage() + "\"";
        System.out.println(msg);
        throw new ObjectIntegrityException(msg);
      }
      catch (Exception e)
      {
        String msg = "ServiceMapper returned error. "
                  + "The underlying error was a " + e.getClass().getName() + ".  "
                  + "The message was "  + "\"" + e.getMessage() + "\"";
        System.out.println(msg);
        throw new GeneralException(msg);
      }
  }

  private MethodDefOperationBind[] merge(Service service, Mmap methodMap)
    throws ObjectIntegrityException, GeneralException
  {
    Port port = null;
    Binding binding = null;
    MethodDefOperationBind[] fedoraMethodDefBindings = new MethodDefOperationBind[0];

    // If the WSDL Service defines multiple Ports (with Binding) for the abstract operations
    // we must CHOOSE ONE binding for Fedora to work with.
    if (service.ports.length > 1)
    {
      port = choosePort(service);
    }
    else
    {
      port = service.ports[0];
    }

    binding = port.binding;

    // Reflect on the type of binding we are dealing with, then Fedora-ize things.
    if (binding.getClass().getName().equalsIgnoreCase("fedora.server.storage.service.HTTPBinding"))
    {
      // Initialize the array to hold the Fedora-ized operation bindings.
      fedoraMethodDefBindings = new MethodDefOperationBind[((HTTPBinding)binding).operations.length];

      for (int i = 0; i < ((HTTPBinding)binding).operations.length; i++)
      {
        // From methodMap which was previously created by parsing Fedora method map metadata
        // which provides a Fedora overlay on the service WSDL.
        MmapMethodDef methodDef = (MmapMethodDef)
          methodMap.wsdlOperationToMethodDef.get(((HTTPBinding)binding).operations[i].operationName);
        fedoraMethodDefBindings[i] = new MethodDefOperationBind();
        fedoraMethodDefBindings[i].methodName = methodDef.methodName;
        fedoraMethodDefBindings[i].methodLabel = methodDef.methodLabel;
        fedoraMethodDefBindings[i].methodParms = methodDef.methodParms;

        // From WSDL Port found in Service object
        fedoraMethodDefBindings[i].serviceBindingAddress = port.portBaseURL;

        // From WSDL Binding found in Service object
        fedoraMethodDefBindings[i].protocolType = MethodDefOperationBind.HTTP_MESSAGE_PROTOCOL;
        fedoraMethodDefBindings[i].operationLocation = ((HTTPBinding)binding).operations[i].operationLocation;
        fedoraMethodDefBindings[i].operationURL =
          fedoraMethodDefBindings[i].serviceBindingAddress.concat(fedoraMethodDefBindings[i].operationLocation);

        // Get the list of datastream input binding keys that pertain to the particular
        // operation binding.  From the WSDL perspective, the datastream input keys
        // are WSDL message parts that, according the the Fedora Method Map, are
        // datastream inputs to the operation.

        MmapMethodParmDef[] parms = methodDef.wsdlMsgParts;
        Vector tmp_dsInputKeys = new Vector();
        for (int j = 0; j < parms.length; j++)
        {
          if (parms[j].parmType.equalsIgnoreCase(MethodParmDef.DATASTREAM_INPUT))
          {
            tmp_dsInputKeys.add(parms[j].parmName);
          }
        }
        fedoraMethodDefBindings[i].dsBindingKeys = (String[])tmp_dsInputKeys.toArray(new String[0]);
      }
    }
    else if (binding.getClass().getName().equalsIgnoreCase("fedora.server.storage.service.SOAPBinding"))
    {
      // FIXIT!!  Implement this!
    }
    return fedoraMethodDefBindings;
  }

  private Port choosePort(Service service)
  {
    // If there is an HTTP binding, this will be preferred.
    for (int i = 0; i < service.ports.length; i++)
    {
      Binding binding = service.ports[i].binding;
      if (binding.getClass().getName().equalsIgnoreCase("fedora.server.storage.service.HTTPBinding"))
      {
        return service.ports[i];
      }
    }
    // Otherwise, just return the first port for binding
    return service.ports[0];
  }
}