package fedora.server.access;

import java.io.File;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.types.NonNegativeInteger;

import fedora.server.Context;
import fedora.server.Server;
import fedora.server.ReadOnlyContext;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ServerException;
import fedora.server.errors.ServerInitializationException;
import fedora.server.types.gen.Condition;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ObjectFields;
import fedora.server.utilities.AxisUtility;
import fedora.server.utilities.TypeUtility;

/**
 * <p><b>Title: </b>FedoraAPIABindingSOAPHTTPImpl.java</p>
 * <p><b>Description: </b>Implements the Fedora Access SOAP service.</p>
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
 * @author rlw@virginia.edu
 * @version 1.0
 */
public class FedoraAPIABindingSOAPHTTPImpl implements
    fedora.server.access.FedoraAPIA
{
  /** The Fedora Server instance. */
  private static Server s_server;

  /** Whether the service has initialized... true if initialized. */
  private static boolean s_initialized;

  /** The exception indicating that initialization failed. */
  private static InitializationException s_initException;

  /** Instance of the access subsystem */
  private static Access s_access;

  /** Context for cached objects. */
  private static ReadOnlyContext context;

  /** Debug toggle for testing. */
  private static boolean debug = false;

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
          Boolean B1 = new Boolean(s_server.getParameter("debug"));
          debug = B1.booleanValue();
          s_server.logFinest("got server instance: " +
                             "s_init: "+s_initialized);
      }
    } catch (InitializationException ie) {
        System.err.println(ie.getMessage());
        s_initialized = false;
        s_initException = ie;
    }
  }

  private Context getContext() {
      HashMap h=new HashMap();
      h.put("application", "apia");
      h.put("useCachedObject", "true");
      h.put("userId", "fedoraAdmin");
      HttpServletRequest req=(HttpServletRequest) MessageContext.
              getCurrentContext().getProperty(
              HTTPConstants.MC_HTTP_SERVLETREQUEST);
      h.put("host", req.getRemoteAddr());
      return new ReadOnlyContext(h);
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
    Context context=getContext();
    assertInitialized();
    try
    {
      String[] bDefs =
          s_access.getBehaviorDefinitions(context, PID, asOfDateTime);
      if (bDefs != null && debug)
      {
        for (int i=0; i<bDefs.length; i++)
        {
          s_server.logFinest("bDef["+i+"] = "+bDefs[i]);
        }
      }
      return bDefs;
    } catch (ServerException se)
    {
      s_server.logFinest("ServerException: " + se.getMessage());
      logStackTrace(se);
      AxisUtility.throwFault(se);
    } catch (Exception e) {
      s_server.logFinest("Exception: " + e.getMessage());
      logStackTrace(e);
      AxisUtility.throwFault(
          new ServerInitializationException(e.getClass().getName() + ": "
          + e.getMessage()));
    }
    return null;
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
    Context context=getContext();
    assertInitialized();
    try
    {
      fedora.server.storage.types.MethodDef[] methodDefs =
          s_access.getBehaviorMethods(context, PID, bDefPID, asOfDateTime);
      fedora.server.types.gen.MethodDef[] genMethodDefs =
          TypeUtility.convertMethodDefArrayToGenMethodDefArray(methodDefs);
      return genMethodDefs;
    } catch (ServerException se)
    {
      logStackTrace(se);
      AxisUtility.throwFault(se);
    } catch (Exception e) {
      logStackTrace(e);
      AxisUtility.throwFault(
          new ServerInitializationException(e.getClass().getName() + ": "
          + e.getMessage()));
    }
    return null;
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
  getBehaviorMethodsXML(java.lang.String PID, java.lang.String bDefPID,
  java.util.Calendar asOfDateTime) throws java.rmi.RemoteException
  {
    Context context=getContext();
    try
    {
      fedora.server.storage.types.MIMETypedStream mimeTypedStream =
          s_access.getBehaviorMethodsXML(context, PID,
          bDefPID, asOfDateTime);
      fedora.server.types.gen.MIMETypedStream genMIMETypedStream =
          TypeUtility.convertMIMETypedStreamToGenMIMETypedStream(
          mimeTypedStream);
      return genMIMETypedStream;
    } catch (ServerException se)
    {
      logStackTrace(se);
      AxisUtility.throwFault(se);
    } catch (Exception e) {
      logStackTrace(e);
      AxisUtility.throwFault(
          new ServerInitializationException(e.getClass().getName() + ": "
          + e.getMessage()));
    }
    return null;
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
      fedora.server.types.gen.Property[] userParms,
      java.util.Calendar asOfDateTime) throws java.rmi.RemoteException
  {
    Context context=getContext();
    try
    {
      fedora.server.storage.types.Property[] properties =
          TypeUtility.convertGenPropertyArrayToPropertyArray(userParms);
      fedora.server.storage.types.MIMETypedStream mimeTypedStream =
          s_access.getDissemination(context, PID, bDefPID, methodName,
          properties, asOfDateTime);
      fedora.server.types.gen.MIMETypedStream genMIMETypedStream =
          TypeUtility.convertMIMETypedStreamToGenMIMETypedStream(
          mimeTypedStream);
      return genMIMETypedStream;
    } catch (ServerException se)
    {
      logStackTrace(se);
      AxisUtility.throwFault(se);
    } catch (Exception e) {
      logStackTrace(e);
      AxisUtility.throwFault(
          new ServerInitializationException(e.getClass().getName() + ": "
          + e.getMessage()));
    }
    return null;
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
    Context context=getContext();
    try
    {
      fedora.server.storage.types.ObjectMethodsDef[] objectMethodDefs =
          s_access.getObjectMethods(context, PID, asOfDateTime);
      fedora.server.types.gen.ObjectMethodsDef[] genObjectMethodDefs =
          TypeUtility.convertObjectMethodsDefArrayToGenObjectMethodsDefArray(
          objectMethodDefs);
      return genObjectMethodDefs;
    } catch (ServerException se)
    {
      logStackTrace(se);
      AxisUtility.throwFault(se);
    } catch (Exception e) {
      logStackTrace(e);
      AxisUtility.throwFault(
          new ServerInitializationException(e.getClass().getName() + ": "
          + e.getMessage()));
    }
    return null;
  }

  public FieldSearchResult findObjects(String[] resultFields,
          NonNegativeInteger maxResults, FieldSearchQuery query)
          throws RemoteException {
      Context context=getContext();
      assertInitialized();
      try {
          fedora.server.search.FieldSearchResult result=s_access.
                  findObjects(context, resultFields, maxResults.intValue(),
                  TypeUtility.convertGenFieldSearchQueryToFieldSearchQuery(
                  query));
          return TypeUtility.convertFieldSearchResultToGenFieldSearchResult(result);
      } catch (ServerException se) {
          logStackTrace(se);
          throw AxisUtility.getFault(se);
      } catch (Exception e) {
          logStackTrace(e);
          throw AxisUtility.getFault(e);
      }
  }

/*
  public ObjectFields[] advancedFieldSearch(
          String[] resultFields, Condition[] conditions)
          List searchConditionList=TypeUtility.
                  convertGenConditionArrayToSearchConditionList(conditions);
          List objectFields=s_access.search(context, resultFields, searchConditionList);
          return TypeUtility.convertSearchObjectFieldsListToGenObjectFieldsArray(
                  objectFields);

  public ObjectFields[] simpleFieldSearch(
          String[] resultFields, String terms)
          List objectFields=s_access.search(context, resultFields, terms);
          return TypeUtility.convertSearchObjectFieldsListToGenObjectFieldsArray(
                  objectFields);
*/

  public FieldSearchResult resumeFindObjects(String sessionToken)
          throws java.rmi.RemoteException {
      Context context=getContext();
      assertInitialized();
      try {
          fedora.server.search.FieldSearchResult result=s_access.
                  resumeFindObjects(context, sessionToken);
          return TypeUtility.convertFieldSearchResultToGenFieldSearchResult(result);
      } catch (ServerException se) {
          logStackTrace(se);
          throw AxisUtility.getFault(se);
      } catch (Exception e) {
          logStackTrace(e);
          throw AxisUtility.getFault(e);
      }
  }

  /**
   * <p>Gets the object profile which included key metadata about the object
   * and URLs for the Dissemination Index and Item Index of the object.</p>
   *
   * @param PID The persistent identifier for the digital object.
   * @param asOfDateTime The versioning datetime stamp.
   * @return The object profile data structure.
   * @throws java.rmi.RemoteException
   */
  public fedora.server.types.gen.ObjectProfile
      getObjectProfile(java.lang.String PID,
      java.util.Calendar asOfDateTime) throws java.rmi.RemoteException
  {
    Context context=getContext();
    try
    {
      fedora.server.access.ObjectProfile objectProfile =
          s_access.getObjectProfile(context, PID, asOfDateTime);
      fedora.server.types.gen.ObjectProfile genObjectProfile =
          TypeUtility.convertObjectProfileToGenObjectProfile(
          objectProfile);
      return genObjectProfile;
    } catch (ServerException se)
    {
      logStackTrace(se);
      AxisUtility.throwFault(se);
    } catch (Exception e) {
      logStackTrace(e);
      AxisUtility.throwFault(
          new ServerInitializationException(e.getClass().getName() + ": "
          + e.getMessage()));
    }
    return null;
  }

  private void logStackTrace(Exception e)
  {
    StackTraceElement[] ste = e.getStackTrace();
    StringBuffer lines = new StringBuffer();
    boolean skip = false;
    for (int i = 0; i < ste.length; i++)
    {
      if (ste[i].toString().indexOf("FedoraAPIABindingSOAPHTTPSkeleton") != -1)
      {
        skip=true;
      }
      if (!skip)
      {
        lines.append(ste[i].toString());
        lines.append("\n");
      }
    }
    s_server.logFiner("Error carried up to API-A level: "
                      + e.getClass().getName() + "\n" + lines.toString());
  }

  private void assertInitialized() throws java.rmi.RemoteException
  {
    if (!s_initialized)
    {
      AxisUtility.throwFault(s_initException);
    }
  }
}