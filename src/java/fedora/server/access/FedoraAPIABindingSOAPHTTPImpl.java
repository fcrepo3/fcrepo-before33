package fedora.server.access;

/**
 * <p>Title: FedoraAPIABindingSOAPHTTPImpl.java</p>
 * <p>Description: Implements the Fedora Access SOAP service.
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */

import fedora.server.access.localservices.HttpService;
import fedora.server.errors.HttpServiceNotFoundException;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.storage.FastDOReader;
import fedora.server.storage.types.DisseminationBindingInfo;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.utilities.DateUtility;

// java imports
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class FedoraAPIABindingSOAPHTTPImpl implements
    fedora.server.access.FedoraAPIA
{
  private static final String CONTENT_TYPE_XML = "text/xml";
  private static final String LOCAL_ADDRESS_LOCATION = "LOCAL";
  private static final boolean debug = true;
  private Hashtable h_userParms = new Hashtable();

  /**
   * <p>Gets a list of Behavior Definition object PIDs for the specified
   * digital object.</p>
   *
   * @param PID persistent identifier of the digital object
   * @param asOfDateTime versioning datetime stamp
   * @return String[] containing Behavior Definitions
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
      bDefs = fastReader.GetBehaviorDefs(versDateTime);
      for (int i=0; i<bDefs.length; i++)
      {
        System.out.println("bDef["+i+"] = "+bDefs[i]);
      }
    } catch(ObjectNotFoundException onfe)
    {
      System.out.println("BDEF FAILDED:"+onfe.getMessage());
      return bDefs;
    }
    return bDefs;
  }

  /**
   * <p>Gets a list of Behavior Methods associated with the specified
   * Behavior Mechanism object.</p>
   *
   * @param PID persistent identifier of Digital Object
   * @param bDefPID persistent identifier of Behavior Definition object
   * @param asOfDateTime versioning datetime stamp
   * @return MethodDef[] containing method definitions
   * @throws java.rmi.RemoteException
   */
  public fedora.server.types.gen.MethodDef[] getBehaviorMethods(
      java.lang.String PID, java.lang.String bDefPID,
      java.util.Calendar asOfDateTime) throws java.rmi.RemoteException
  {
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    fedora.server.types.gen.MethodDef[] behaviorDefs = null;
    try
    {
      FastDOReader fastReader = new FastDOReader(PID);
      MethodDef[] methodResults = fastReader.GetBMechMethods(bDefPID,
          versDateTime);
      System.out.println("size: "+methodResults.length);
      System.out.flush();
      behaviorDefs =
          new fedora.server.types.gen.MethodDef[methodResults.length];
      for (int i=0; i<methodResults.length; i++)
      {
        fedora.server.types.gen.MethodDef mdef =
                 new fedora.server.types.gen.MethodDef();
        //mdef.setHttpBindingOperationLocation(
        //    methodResults[i].httpBindingOperationLocation);
        //mdef.setHttpBindingURL(methodResults[i].httpBindingURL);
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
    //  System.out.println(onfe.getMessage());
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    return behaviorDefs;
  }

  /**
   * <p>Gets a bytestream containing the WSDL that defines the Behavior Methods
   * of the associated Behavior Mechanism object.</p>
   *
   * @param PID persistent identifier of Digital Object
   * @param bDefPID persistent identifier of Behavior Definition object
   * @param asOfDateTime versioning datetime stamp
   * @return MIMETypedStream containing WSDL method definitions
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
      FastDOReader fastReader = new FastDOReader(PID);
      // FIXME!! versioning based on datetime not yet implemented
      System.out.println("PID: "+PID+"FPID: "+fastReader.GetObjectPID());
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
    //  System.out.println(ioe.getMessage());
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
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
   * @param PID persistent identifier of the Digital Object
   * @param bDefPID persistent identifier of the Behavior Definition object
   * @param methodName name of the method
   * @param asOfDateTime version datetime stamp of the digital object
   * @param userParms array of user-supplied method parameters and values
   * @return MIMETypedStream containing the dissemination result
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
        if (debug) System.out.println("counter: "+i+" numelem: "+numElements);
        String currentKey = dissResult.DSBindKey;
        String nextKey = "";
        if (i != numElements-1)
        {
          // Except for last row, get the value of the next binding key
          // to compare with the value of the current binding key.
          if (debug) System.out.println("currentKey: '"+currentKey+"'");
          nextKey = dissResults[i+1].DSBindKey;
          if (debug) System.out.println("' nextKey: '"+nextKey+"'");
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
        if (debug) System.out.println("replaceString: "+replaceString);
        dissURL = substituteString(dissURL, bindingKeyPattern, replaceString);
        if (debug) System.out.println("replaced dissURL = "+
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
        if (debug) System.out.println("UserParmSubstitution dissURL: "+dissURL);
      }

      // Resolve content referenced by dissemination result
      if (debug) System.out.println("ProtocolType = "+protocolType);
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
          System.out.println(onfe.getMessage());
        }
      } else if (protocolType.equalsIgnoreCase("soap"))
      {
        // FIXME!! future handling by soap interface
        System.out.println("Protocol type specified: "+protocolType);
        dissemination = null;
      } else
      {
        System.out.println("Unknown protocol type: "+protocolType);
        dissemination = null;
      }
    } catch (ObjectNotFoundException onfe)
    {
      // FIXME!! Decide on Exception handling
      // Object was not found in SQL database or in XML storage area
      System.out.println("getdissem: ObjectNotFound");
    }
   return dissemination;
  }

  /**
   * <p>Gets a list of all method definitions for the specified object.</p>
   *
   * @param PID persistent identifier for the digital object
   * @param asOfDateTime versioning datetime stamp
   * @return ObjectMethodsDef array of object method definitions
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
      Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
      System.out.println("verDate: "+versDateTime+"\nPID: "+PID);
      fastReader = new FastDOReader(PID);
      ObjectMethodsDef[] methodResults = fastReader.getObjectMethods(PID,
          versDateTime);
      int size = methodResults.length;
      System.out.println("count: "+size);
      System.out.flush();
      methodDefs =
          new fedora.server.types.gen.ObjectMethodsDef[methodResults.length];
      for (int i=0; i<methodResults.length; i++)
      {
        //System.out.println("PID:["+i+"] ="+methodResults[i].PID);
        //System.out.println("bDEF:["+i+"] ="+methodResults[i].bDefPID);
        //System.out.println("meth:["+i+"] ="+methodResults[i].methodName);
        //System.out.flush();
        fedora.server.types.gen.ObjectMethodsDef mdef =
            new fedora.server.types.gen.ObjectMethodsDef();
        mdef.setPID(methodResults[i].PID);
        mdef.setBDefPID(methodResults[i].bDefPID);
        mdef.setMethodName(methodResults[i].methodName);
        methodDefs[i] = mdef;
      }
    } catch (ObjectNotFoundException onfe)
    {
      System.out.println(onfe.getMessage());
    }
    return methodDefs;
  }

    /**
     * <p>Performs simple string replacement using regular expressions.
     * All matching occurrences of the pattern string will be replaced in the
     * input string by the replacement string.
     *
     * @param inputString source string
     * @param patternString regular expression pattern
     * @param replaceString replacement string
     * @return String source string with substitutions
     */
    private String substituteString(String inputString, String patternString,
                                   String replaceString)
    {
      Pattern pattern = Pattern.compile(patternString);
      Matcher m = pattern.matcher(inputString);
      return(m.replaceAll(replaceString));
    }
}
