package fedora.server.access;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fedora.server.Server;
import fedora.server.ReadOnlyContext;
import fedora.server.access.localservices.HttpService;
import fedora.server.errors.HttpServiceNotFoundException;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.errors.ServerInitializationException;
import fedora.server.storage.FastDOReader;
import fedora.server.storage.types.DisseminationBindingInfo;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.utilities.DateUtility;

/**
 * <p>Title: FedoraAPIABindingSOAPHTTPImpl.java</p>
 * <p>Description: Implements the Fedora Access SOAP service.
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class FedoraAPIABindingSOAPHTTPImpl implements
    fedora.server.access.FedoraAPIA
{
  /** The Fedora Server instance */
  private static Server s_server;

  /** Whether the service has initialized... true if we got a good Server instance. */
  private static boolean s_initialized;

  /** The exception indicating that initialization failed. */
  private static InitializationException s_initException;

  private static Access s_access;

  private static ReadOnlyContext s_context;

  private static final String CONTENT_TYPE_XML = "text/xml";
  private static final String LOCAL_ADDRESS_LOCATION = "LOCAL";
  private static final boolean debug = true;
  private Hashtable h_userParms = new Hashtable();

  /** Before fulfilling any requests, make sure we have a server instance. */
  static
  {
    try
    {
      String fedoraHome=System.getProperty("fedora.home");
      if (fedoraHome == null) {
          s_initialized = false;
          s_initException = new ServerInitializationException(
              "Server failed to initialize: The 'fedora.home' "
              + "system property was not set.");
      } else {
          s_server=Server.getInstance(new File(fedoraHome));
          s_initialized = true;
          s_access =
              (Access) s_server.getModule("fedora.server.access.Access");
          HashMap h=new HashMap();
          h.put("application", "apia");
          s_context=new ReadOnlyContext(h);
      }
    } catch (InitializationException ie) {
        System.err.println(ie.getMessage());
        s_initialized = false;
        s_initException = ie;
    }
  }

  /**
   * <p>Gets a list of Behavior Definition object PIDs for the specified
   * digital object.</p>
   *
   * @param PID The persistent identifier of the digital object.
   * @param asOfDateTime The versioning datetime stamp.
   * @return An array containing Behavior Definition PIDs.
   * @throws java.rmi.RemoteException
   */
  public java.lang.String[] getBehaviorDefinitions(java.lang.String PID,
      java.util.Calendar asOfDateTime) throws java.rmi.RemoteException
  {
    String[] bDefs = null;
    try
    {
      FastDOReader fastReader = new FastDOReader(PID);
      Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
      //bDefs = s_access.getBehaviorDefinitions(s_context, PID, asOfDateTime);
      bDefs = fastReader.GetBehaviorDefs(versDateTime);
      for (int i=0; i<bDefs.length; i++)
      {
        System.err.println("bDef["+i+"] = "+bDefs[i]);
      }
    } catch(Exception e)
    {
      System.err.println("BDEF FAILDED:"+e.getMessage());
      return bDefs;
    }
    return bDefs;
  }

  /**
   * <p>Gets a list of Behavior Methods associated with the specified
   * Behavior Mechanism object.</p>
   *
   * @param PID The persistent identifier of digital object.
   * @param bDefPID The persistent identifier of Behavior Definition object.
   * @param asOfDateTime The versioning datetime stamp.
   * @return An array of method definitions.
   * @throws java.rmi.RemoteException.
   */
  public fedora.server.types.gen.MethodDef[] getBehaviorMethods(
      java.lang.String PID, java.lang.String bDefPID,
      java.util.Calendar asOfDateTime) throws java.rmi.RemoteException
  {
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    fedora.server.types.gen.MethodDef[] behaviorDefs = null;
    try
    {
      // behaviorDefs = s_access.getBehaviorMethods(s_context, PID, bDefPID, asOfDateTime);
      FastDOReader fastReader = new FastDOReader(PID);
      MethodDef[] methodResults = fastReader.GetBMechMethods(bDefPID,
          versDateTime);
      System.err.println("size: "+methodResults.length);
      System.out.flush();
      behaviorDefs =
          new fedora.server.types.gen.MethodDef[methodResults.length];
      for (int i=0; i<methodResults.length; i++)
      {
        fedora.server.types.gen.MethodDef mdef =
                 new fedora.server.types.gen.MethodDef();
        mdef.setMethodLabel(methodResults[i].methodLabel);
        mdef.setMethodName(methodResults[i].methodName);
        MethodParmDef[] parmResults = methodResults[i].methodParms;
        if (parmResults.length > 0)
        {
          fedora.server.types.gen.MethodParmDef[] methodParms =
              new fedora.server.types.gen.MethodParmDef[parmResults.length];
          for (int j=0; j<parmResults.length; j++)
          {
            fedora.server.types.gen.MethodParmDef parmdef =
                       new fedora.server.types.gen.MethodParmDef();
            parmdef.setParmDefaultValue(parmResults[j].parmDefaultValue);
            parmdef.setParmLabel(parmResults[j].parmLabel);
            parmdef.setParmName(parmResults[j].parmName);
            parmdef.setParmRequired(parmResults[j].parmRequired);
            methodParms[j] = parmdef;
          }
          mdef.setMethodParms(methodParms);
        }
        behaviorDefs[i] = mdef;
      }
    //} catch (ObjectNotFoundException onfe)
    //{
    //  System.err.println(onfe.getMessage());
    } catch (Exception e)
    {
      System.err.println(e.getMessage());
    }
    return behaviorDefs;
  }

  /**
   * <p>Gets a bytestream containing the WSDL that defines the Behavior Methods
   * of the associated Behavior Mechanism object.</p>
   *
   * @param PID The persistent identifier of Digital Object.
   * @param bDefPID The persistent identifier of Behavior Definition object.
   * @param asOfDateTime The versioning datetime stamp.
   * @return A MIME-typed stream containing WSDL method definitions.
   * @throws java.rmi.RemoteException
   */
  public fedora.server.types.gen.MIMETypedStream
  getBehaviorMethodsAsWSDL(java.lang.String PID, java.lang.String bDefPID,
  java.util.Calendar asOfDateTime) throws java.rmi.RemoteException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
    fedora.server.types.gen.MIMETypedStream methodDefs = null;
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    InputStream methodResults = null;
    try
    {
      //methodDefs = s_access.getBehaviorMethodsAsWSDL(s_context, PID, bDefPID, asOfDateTime);
      FastDOReader fastReader = new FastDOReader(PID);
      // FIXME!! versioning based on datetime not yet implemented
      System.err.println("PID: "+PID+"FPID: "+fastReader.GetObjectPID());
      System.out.flush();
      methodResults = fastReader.GetBMechMethodsWSDL(bDefPID, versDateTime);
      int byteStream = 0;
      while ((byteStream = methodResults.read()) >= 0)
      {
        baos.write(byteStream);
      }
      methodResults.close();
    //} catch (IOException ioe)
    //{
    //  System.err.println(ioe.getMessage());
    } catch (Exception e)
    {
      System.err.println(e.getMessage());
      return methodDefs;
    }
    if (methodResults != null)
    {
    methodDefs = new fedora.server.types.gen.MIMETypedStream();
    methodDefs.setMIMEType(CONTENT_TYPE_XML);
    methodDefs.setStream(baos.toByteArray());
    }
    return methodDefs;
  }

  /**
   * <p>Gets a MIME-typed bytestream containing the result of a dissemination.
   * </p>
   *
   * @param PID The persistent identifier of the Digital Object.
   * @param bDefPID The persistent identifier of the Behavior Definition object.
   * @param methodName The name of the method.
   * @param asOfDateTime The version datetime stamp of the digital object.
   * @param userParms An array of user-supplied method parameters and values.
   * @return A MIME-typed stream containing the dissemination result.
   * @throws java.rmi.RemoteException
   */
  public fedora.server.types.gen.MIMETypedStream
  getDissemination(java.lang.String PID,
  java.lang.String bDefPID,
  java.lang.String methodName,
  fedora.server.types.gen.Property[] parameters,
  java.util.Calendar asOfDateTime) throws java.rmi.RemoteException
  {
    String protocolType = null;
    DisseminationBindingInfo[] dissResults = null;
    DisseminationBindingInfo dissResult = null;
    String dissURL = null;
    String operationLocation = null;
    fedora.server.types.gen.MIMETypedStream dissemination = null;
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    FastDOReader fastReader = null;
    try
    {
      //dissemination = s_access.getDissemination(s_context, PID, bDefPID, methodName, userparms, asOfDateTime);
      fastReader = new FastDOReader(PID);
      dissResults = fastReader.getDissemination(PID, bDefPID, methodName,
          versDateTime);
      String replaceString = null;
      int numElements = dissResults.length;

      // Get row(s) of WSDL results and perform string substitution
      // on DSBindingKey and method parameter values in WSDL
      // Note: In case where more than one datastream matches the
      // DSBindingKey or there are multiple DSBindingKeys for the
      // method, multiple rows will be returned; otherwise
      // a single row is returned.
      for (int i=0; i<dissResults.length; i++)
      {
        dissResult = dissResults[i];

        // If AddressLocation has a value of "LOCAL", this is a flag to
        // indicate the associated OperationLocation requires no
        // AddressLocation. i.e., the OperationLocation contains all
        // information necessary to perform the dissemination request.
        if (dissResult.AddressLocation.equalsIgnoreCase(LOCAL_ADDRESS_LOCATION))
        {
          dissResult.AddressLocation = "";
        }
        // Match DSBindingKey pattern in WSDL
        String bindingKeyPattern = "\\("+dissResult.DSBindKey+"\\)";
        if (i == 0)
        {
          operationLocation = dissResult.OperationLocation;
          dissURL = dissResult.AddressLocation+dissResult.OperationLocation;
          protocolType = dissResult.ProtocolType;
        }
        if (debug) System.err.println("counter: "+i+" numelem: "+numElements);
        String currentKey = dissResult.DSBindKey;
        String nextKey = "";
        if (i != numElements-1)
        {
          // Except for last row, get the value of the next binding key
          // to compare with the value of the current binding key.
          if (debug) System.err.println("currentKey: '"+currentKey+"'");
          nextKey = dissResults[i+1].DSBindKey;
          if (debug) System.err.println("' nextKey: '"+nextKey+"'");
        }

        // In most cases, there is only a single datastream that matches a given
        // DSBindingKey so the substitution process is to just replace the
        // occurence of (BINDING_KEY) with the value of the datastream location.
        // However, when multiple datastreams match the same DSBindingKey, the
        // occurrence of (BINDING_KEY) is replaced with the value of the
        // datastream location and the value +(BINDING_KEY) is appended so that
        // subsequent datastreams matching the binding key will be substituted.
        // The end result is that the binding key will be replaced by a string
        // datastream locations separated by a plus(+) sign. e.g.,
        //
        // file=(PHOTO) becomes
        // file=dslocation1+dslocation2+dslocation3
        //
        // It is the responsibility of the Behavior Mechanism to know how to
        // handle an input parameter with multiple datastreams.
        //
        // In the case of a method containing multiple binding keys,
        // substitutions are performed on each binding key. e.g.,
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
          replaceString = dissResult.DSLocation+"+("+dissResult.DSBindKey+")";
        } else
        {
          replaceString = dissResult.DSLocation;
        }
        if (debug) System.err.println("replaceString: "+replaceString);
        dissURL = substituteString(dissURL, bindingKeyPattern, replaceString);
        if (debug) System.err.println("replaced dissURL = "+
                                     dissURL.toString()+
                                     " counter = "+i);
      }

      // User-supplied parameters have already been validated.
      // Substitute user-supplied parameter values in dissemination URL
      Enumeration e = h_userParms.keys();
      while (e.hasMoreElements())
      {
        String name = (String)e.nextElement();
        String value = (String)h_userParms.get(name);
        String pattern = "\\("+name+"\\)";
        dissURL = substituteString(dissURL, pattern, value);
        if (debug) System.err.println("UserParmSubstitution dissURL: "+dissURL);
      }

      // Resolve content referenced by dissemination result
      if (debug) System.err.println("ProtocolType = "+protocolType);
      if (protocolType.equalsIgnoreCase("http"))
      {
        // FIXME!! need to implement Access Policy control.
        // If access is based on restrictions to content,
        // this is the last chance to apply those restrictions
        // before returnign dissemination result to client.
        HttpService httpService = new HttpService(dissURL);
        MIMETypedStream diss = null;
        try
        {
          diss = httpService.getHttpContent(dissURL);
          dissemination = new fedora.server.types.gen.MIMETypedStream();
          dissemination.setMIMEType(diss.MIMEType);
          dissemination.setStream(diss.stream);
        } catch (HttpServiceNotFoundException onfe)
        {
          // FIXME!! -- Decide on Exception handling
          System.err.println(onfe.getMessage());
        }
      } else if (protocolType.equalsIgnoreCase("soap"))
      {
        // FIXME!! future handling by soap interface
        System.err.println("Protocol type specified: "+protocolType);
        dissemination = null;
      } else
      {
        System.err.println("Unknown protocol type: "+protocolType);
        dissemination = null;
      }
    } catch (Exception e)
    {
      // FIXME!! Decide on Exception handling
      // Object was not found in SQL database or in XML storage area
      System.err.println(e.getMessage());
      System.err.println("getdissem: ObjectNotFound");
    }
   return dissemination;
  }

  /**
   * <p>Gets a list of all method definitions for the specified object.</p>
   *
   * @param PID The persistent identifier for the digital object.
   * @param asOfDateTime The versioning datetime stamp.
   * @return An array of object method definitions.
   * @throws java.rmi.RemoteException
   */
  public fedora.server.types.gen.ObjectMethodsDef[]
      getObjectMethods(java.lang.String PID,
      java.util.Calendar asOfDateTime) throws java.rmi.RemoteException
  {

    fedora.server.types.gen.ObjectMethodsDef[] methodDefs = null;
    FastDOReader fastReader = null;
    try
    {
      //methodDefs = s_access.getObjectMethods(s_context, PID, asOfDateTime);
      Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
      System.err.println("verDate: "+versDateTime+"\nPID: "+PID);
      fastReader = new FastDOReader(PID);
      ObjectMethodsDef[] methodResults = fastReader.getObjectMethods(PID,
          versDateTime);
      int size = methodResults.length;
      System.err.println("count: "+size);
      System.out.flush();
      methodDefs =
          new fedora.server.types.gen.ObjectMethodsDef[methodResults.length];
      for (int i=0; i<methodResults.length; i++)
      {
        fedora.server.types.gen.ObjectMethodsDef mdef =
            new fedora.server.types.gen.ObjectMethodsDef();
        mdef.setPID(methodResults[i].PID);
        mdef.setBDefPID(methodResults[i].bDefPID);
        mdef.setMethodName(methodResults[i].methodName);
        methodDefs[i] = mdef;
      }
    } catch (Exception e)
    {
      System.err.println(e.getMessage());
    }
    return methodDefs;
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
      return(m.replaceAll(replaceString));
    }
}
