/**
 * FedoraAPIABindingSOAPHTTPImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package fedora.server.access;

import fedora.server.storage.DefinitiveDOReader;
import fedora.server.storage.DefinitiveBDefReader;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.FastDOReader;
import java.util.Enumeration;
import java.util.Vector;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FedoraAPIABindingSOAPHTTPImpl implements fedora.server.access.FedoraAPIA
{
  private static final String CONTENT_TYPE_XML = "text/xml";
  private static final String LOCAL_ADDRESS_LOCATION = "LOCAL";
  private static final boolean debug = true;

    public java.lang.String[] getBehaviorDefinitions(java.lang.String PID, java.util.Calendar asOfDateTime) throws java.rmi.RemoteException {
      DefinitiveDOReader doReader = new DefinitiveDOReader(PID);
      // FIXME!! versioning based on datetime not yet implemented
    return doReader.GetBehaviorDefs(null);
    }

    public fedora.server.types.MIMETypedStream getBehaviorMethods(java.lang.String PID, java.lang.String bDefPID, java.util.Calendar asOfDateTime) throws java.rmi.RemoteException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
      fedora.server.types.MIMETypedStream bDefMethods = null;
      try
      {
        DefinitiveBDefReader doReader = new DefinitiveBDefReader(bDefPID);
        // FIXME!! versioning based on datetime not yet implemented
        InputStream methodResults = doReader.GetBehaviorMethodsWSDL(null);
        int byteStream = 0;
        while ((byteStream = methodResults.read()) >= 0)
        {
          baos.write(byteStream);
        }
      } catch (IOException ioe)
      {
        System.out.println(ioe);
      }
      //bDefMethods = new MIMETypedStream(CONTENT_TYPE_XML,baos.toByteArray());
      bDefMethods = new fedora.server.types.MIMETypedStream();
      bDefMethods.setMIMEType(CONTENT_TYPE_XML);
      bDefMethods.setStream(baos.toByteArray());

      return(bDefMethods);

    }

    public fedora.server.types.MIMETypedStream getDissemination(java.lang.String PID, java.lang.String bDefPID, java.lang.String methodName, fedora.server.types.Property[] parameters, java.util.Calendar asOfDateTime) throws java.rmi.RemoteException {
      String protocolType = null;
      Vector dissResult = null;
      String dissURL = null;
      fedora.server.types.MIMETypedStream dissemination = null;
      FastDOReader fastReader = new FastDOReader(PID, bDefPID, methodName);
      dissResult = fastReader.getDissemination(PID, bDefPID, methodName);
      if (dissResult.size() == 0)
      {
        // FIXME!! need to implement code for getting dissemination
        // via XML vs SQL.
        // If null is returned from FastDOReader, this indicates that
        // the desired object is not in SQL database. Should then try to
        // get dissemination from authoratative version of object in
        // XML store using DefinitiveDOReader.
        System.out.println("Dissemination Result: NULL");

      } else {
        String replaceString = null;
        DissResultSet results = null;
        int counter = 1;
        int numElements = dissResult.size();
        Enumeration e = dissResult.elements();
        // Get row(s) of WSDL results and perform string substitution
        // on DSBindingKey and method parameter values in WSDL
        // Note: In case where more than one datastream matches the
        // DSBindingKey, multiple rows will be returned; otherwise
        // a single row is returned.
        while (e.hasMoreElements())
        {
          results = new DissResultSet((String[])e.nextElement());
          // If AddressLocation is LOCAL, this is a flag to indicate
          // the associated OperationLocation requires no AddressLocation.
          // i.e., the OperationLocation contains all information necessary
          // to perform the dissemination request.
          if (results.addressLocation.equals(LOCAL_ADDRESS_LOCATION))
          {
            results.addressLocation = "";
          }
          // Match DSBindingKey pattern in WSDL
          String bindingKeyPattern = "\\("+results.dsBindingKey+"\\)";
          if (counter == 1)
          {
            dissURL = results.addressLocation+results.operationLocation;
            protocolType = results.protocolType;
          }
          // If more than one datastream returned alter replacement string by
          // appending +(DSBindingKey) for substitution in next iteration.
          if (numElements == 1 | counter >= numElements)
          {
            replaceString = results.dsLocation;
          } else {
            replaceString = results.dsLocation+"+("+results.dsBindingKey+")";
          }
          dissURL = substituteString(dissURL, bindingKeyPattern, replaceString);
          counter++;
          if (debug) System.out.println("replaced dissURL = "+
                                       dissURL.toString()+
                                       " counter = "+counter);
        }

        // FIXME!! need to implement handling of user-supplied method parameters
        // Would need to validate user-supplied parameters and then substitute
        // values in operationLocation.

        // FIXME!! need to implement Access Policy control

        // Resolve content referenced by dissemination result
        if (debug) System.out.println("ProtocolType = "+protocolType);
        if (protocolType.equalsIgnoreCase("http"))
        {
          MIMETypedStream diss = null;
          diss = fastReader.getHttpContent(dissURL);
          dissemination = new fedora.server.types.MIMETypedStream();
          dissemination.setMIMEType(diss.MIMEType);
          dissemination.setStream(diss.stream);
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
      }

    return dissemination;
    }

    /**
     * Method to perform simple string replacement using regular expressions.
     * All mathcing occurrences of the pattern string will be replaced in the
     * input string by the replacement string.
     *
     * @param inputString - source string
     * @param patternString - regular expression pattern
     * @param replaceString - replacement string
     * @return - source string with substitutions
     */
    private String substituteString(String inputString, String patternString,
                                   String replaceString)
    {
      Pattern pattern = Pattern.compile(patternString);
      Matcher m = pattern.matcher(inputString);
      return(m.replaceAll(replaceString));
    }

}
