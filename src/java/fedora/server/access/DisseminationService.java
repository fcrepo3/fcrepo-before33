package fedora.server.access;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fedora.server.Server;
import fedora.server.access.localservices.HttpService;
import fedora.server.errors.DisseminationBindingInfoNotFoundException;
import fedora.server.errors.HttpServiceNotFoundException;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ServerException;
import fedora.server.errors.ServerInitializationException;
import fedora.server.storage.types.DisseminationBindingInfo;
import fedora.server.storage.ExternalContentManager;
import fedora.server.storage.DefaultExternalContentManager;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.Property;

/**
 * <p>Title: DisseminationService.java</p>
 * <p>Description: A service for executing a dissemination given its
 * binding information.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class DisseminationService
{
  /** The Fedora Server instance */
  private static Server s_server;

  /** The exception indicating that initialization failed. */
  private static InitializationException s_initException;

  /** Signifies the special type of address location known as LOCAL.
   *  An address location of LOCAL implies that no remote host name is
   *  required for the address location and that the contents of the
   *  operation location are sufficient to execute the associated mechanism.
   */
  private static final String LOCAL_ADDRESS_LOCATION = "LOCAL";

  /** User-supplied method parameters. */
  private Hashtable h_userParms = new Hashtable();

  /** Make sure we have a server instance for error logging purposes. */
  static
  {
    try
    {
      String fedoraHome = System.getProperty("fedora.home");
      if (fedoraHome == null) {
          throw new ServerInitializationException(
              "Server failed to initialize: The 'fedora.home' "
              + "system property was not set.");
      } else {
          s_server = Server.getInstance(new File(fedoraHome));
      }
    } catch (InitializationException ie) {
        System.err.println(ie.getMessage());
    }
  }

  /**
   * <p>Assembles a dissemination given an instance of <code>
   * DisseminationBindingInfo</code> which has the dissemination-related
   * information from the digital object and its associated Behavior
   * Mechanism object.</p>
   *
   * @param userParms An array of user-supplied method parameters.
   * @param dissBindInfoArray The associated dissemination binding information.
   * @return A MIME-typed stream containing the result of the dissemination.
   * @throws ServerException If unable to assemble the dissemination for any
   *         reason.
   */
  public MIMETypedStream assembleDissemination(Property[] userParms,
      DisseminationBindingInfo[] dissBindInfoArray)
      throws ServerException
  {
    String protocolType = null;
    DisseminationBindingInfo dissBindInfo = null;
    String dissURL = null;
    String operationLocation = null;
    MIMETypedStream dissemination = null;
    if (userParms != null && userParms.length > 0)
    {
      for (int i=0; i<userParms.length; i++)
      {
        h_userParms.put(userParms[i].name, userParms[i].value);
      }
    }
    if (dissBindInfoArray != null && dissBindInfoArray.length > 0)
    {
      String replaceString = null;
      int numElements = dissBindInfoArray.length;

      // Get row(s) of WSDL results and perform string substitution
      // on DSBindingKey and method parameter values in WSDL
      // Note: In case where more than one datastream matches the
      // DSBindingKey or there are multiple DSBindingKeys for the
      // method, multiple rows will be present; otherwise there us only
      // a single row.
      for (int i=0; i<dissBindInfoArray.length; i++)
      {
        dissBindInfo = dissBindInfoArray[i];

        // If AddressLocation has a value of "LOCAL", this is a flag to
        // indicate the associated OperationLocation requires no
        // AddressLocation. i.e., the OperationLocation contains all
        // information necessary to perform the dissemination request.
        if (dissBindInfo.AddressLocation.
            equalsIgnoreCase(LOCAL_ADDRESS_LOCATION))
        {
          dissBindInfo.AddressLocation = "";
        }

        // Match DSBindingKey pattern in WSDL
        String bindingKeyPattern = "\\(" + dissBindInfo.DSBindKey + "\\)";
        if (i == 0)
        {
          operationLocation = dissBindInfo.OperationLocation;
          dissURL = dissBindInfo.AddressLocation+dissBindInfo.OperationLocation;
          protocolType = dissBindInfo.ProtocolType;
        }
        s_server.logFinest("DissBindingInfo index: " + i
                           + " DissBindingInfo length: " + numElements);
        String currentKey = dissBindInfo.DSBindKey;
        String nextKey = "";
        if (i != numElements-1)
        {
          // Except for last row, get the value of the next binding key
          // to compare with the value of the current binding key.
          s_server.logFinest("currentKey: '" + currentKey + "'");
          nextKey = dissBindInfoArray[i+1].DSBindKey;
          s_server.logFinest("nextKey: '" + nextKey + "'");
        }

        // In most cases, there is only a single datastream that matches a
        // given DSBindingKey so the substitution process is to just replace
        // the occurence of (BINDING_KEY) with the value of the datastream
        // location. However, when multiple datastreams match the same
        // DSBindingKey, the occurrence of (BINDING_KEY) is replaced with the
        // value of the datastream location and the value +(BINDING_KEY) is
        // appended so that subsequent datastreams matching the binding key
        // will be substituted. The end result is that the binding key will
        // be replaced by a series of datastream locations separated by a
        // plus(+) sign. For example, in the case where 3 datastreams match
        // the binding key for PHOTO:
        //
        // file=(PHOTO) becomes
        // file=dslocation1+dslocation2+dslocation3
        //
        // It is the responsibility of the Behavior Mechanism to know how to
        // handle an input parameter with multiple datastream locations.
        //
        // In the case of a method containing multiple binding keys,
        // substitutions are performed on each binding key. For example, in
        // the case where there are 2 binding keys named PHOTO and WATERMARK
        // where each matches a single datastream:
        //
        // image=(PHOTO)&watermark=(WATERMARK) becomes
        // image=dslocation1&watermark=dslocation2
        //
        // In the case with mutliple binding keys and multiple datastreams,
        // the substitution might appear like the following:
        //
        // image=(PHOTO)&watermark=(WATERMARK) becomes
        // image=dslocation1+dslocation2&watermark=dslocation3
        if (nextKey.equalsIgnoreCase(currentKey) & i != numElements)
        {
          replaceString = dissBindInfo.DSLocation
                        + "+(" + dissBindInfo.DSBindKey + ")";
        } else
        {
          replaceString = dissBindInfo.DSLocation;
        }
        s_server.logFinest("replaceString: " + replaceString);
        dissURL = substituteString(dissURL, bindingKeyPattern, replaceString);
        s_server.logFinest("replaced dissURL: "
                           + dissURL.toString()
                           + " DissBindingInfo index: " + i);
      }

      // Substitute user-supplied parameter values in dissemination URL
      Enumeration e = h_userParms.keys();
      while (e.hasMoreElements())
      {
        String name = (String)e.nextElement();
        String value = (String)h_userParms.get(name);
        String pattern = "\\(" + name + "\\)";
        dissURL = substituteString(dissURL, pattern, value);
        s_server.logFinest("User parm substituted in URL: " + dissURL);
      }

      // Resolve content referenced by dissemination result.
      s_server.logFinest("ProtocolType: " + protocolType);
      if (protocolType.equalsIgnoreCase("http"))
      {
        // FIXME!! need to implement Access Policy control.
        ExternalContentManager externalContentManager = (ExternalContentManager)
          s_server.getModule("fedora.server.storage.ExternalContentManager");

        //HttpService httpService = new HttpService();
        try
        {
          //dissemination = httpService.getHttpContent(dissURL);
          dissemination = externalContentManager.getExternalContent(dissURL);
        } catch (HttpServiceNotFoundException hsnfe)
        {
          s_server.logWarning("Unable to establish HTTP service for URL: "
                              + dissURL + hsnfe.getMessage());
          throw hsnfe;
        }
      } else if (protocolType.equalsIgnoreCase("soap"))
      {
        // FIXME!! future handling of soap bindings.
        String message = "DisseminationService: Protocol type: "
            + protocolType + "NOT yet implemented";
        s_server.logWarning(message);
        throw new DisseminationBindingInfoNotFoundException(message);
      } else
      {
        String message = "DisseminationService: Protocol type: "
            + protocolType + "NOT supported.";
        s_server.logWarning(message);
        throw new DisseminationBindingInfoNotFoundException(message);
      }
    } else
    {
      // DisseminationBindingInfo was empty so there was no information
      // provided to construct a dissemination.
      String message = "DisseminationService: Dissemination Binding "+
                         "Info contained no data";
      s_server.logWarning(message);
      throw new DisseminationBindingInfoNotFoundException(message);
    }
     return dissemination;
  }

  /**
   * <p>Performs simple string replacement using regular expressions.
   * All matching occurrences of the pattern string will be replaced in the
   * input string by the replacement string.
   *
   * @param inputString The source string.
   * @param patternString The regular expression pattern.
   * @param replaceString The replacement string.
   * @return The source string with substitutions.
   */
  private String substituteString(String inputString, String patternString,
                                 String replaceString)
  {
    Pattern pattern = Pattern.compile(patternString);
    Matcher m = pattern.matcher(inputString);
    return m.replaceAll(replaceString);
  }
}