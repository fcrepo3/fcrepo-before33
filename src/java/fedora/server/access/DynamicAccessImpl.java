package fedora.server.access;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Map;
import java.io.File;
import java.lang.Class;
import java.lang.reflect.*;

import fedora.server.Server;
import fedora.server.Context;
import fedora.server.access.internalservices.DefaultBehavior;
import fedora.server.access.internalservices.DefaultBehaviorImpl;
import fedora.server.access.internalservices.ServiceMethodDispatcher;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.MethodNotFoundException;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ServerException;
import fedora.server.security.IPRestriction;
import fedora.server.utilities.DateUtility;
import fedora.server.search.FieldSearchQuery;
import fedora.server.search.FieldSearchResult;
import fedora.server.storage.DOManager;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.storage.types.Property;

/**
 * <p><b>Title: </b>DynamicAccessImpl.java</p>
 * <p><b>Description: </b>The implementation of the Dynamic Access module.
 * The Dynamic Access module will associate dynamic disseminators with
 * the a digital object.  It will look to the Fedora repository configuration
 * file to obtain a list of dynamic disseminators.  Currently, the system
 * supports two types of dynamic disseminators:
 *  - Default (BDefPID=fedora-system:3 and BMechPID=fedora-system:4)
 *  - Bootstrap (BDefPID=fedora-system:1 and BMechPID=fedora-system:2).
 * The Default disseminator that is associated with every object
 * in the repository.  The Default Disseminator endows the objects with a
 * set of basic generic behaviors that enable a simplistic view of the object
 * contents (the Item Index) and a list of all disseminations available on
 * the object (the Dissemination Index).
 * The Bootstrap disseminator is associated with every behavior definition and
 * behavior mechanism object.  It defines methods to get the special metadata
 * datastreams out of them, and some other methods.  (NOTE: The Bootstrap
 * Disseminator functionality is NOT YET IMPLEMENTED.</p>
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
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version 1.0
 */
public class DynamicAccessImpl
{

  private Access m_access;
  private ServiceMethodDispatcher dispatcher;
  private String reposBaseURL = null;
  private File reposHomeDir = null;
  private Hashtable dynamicBDefToMech = null;

  public DynamicAccessImpl(Access m_access, String reposBaseURL,
    File reposHomeDir, Hashtable dynamicBDefToMech)
  {
    dispatcher = new ServiceMethodDispatcher();
    this.m_access = m_access;
    this.reposBaseURL = reposBaseURL;
    this.reposHomeDir = reposHomeDir;
    this.dynamicBDefToMech = dynamicBDefToMech;
  }

  /**
   * Get a list of behavior definition identifiers for dynamic disseminators
   * associated with the digital object.
   * @param context
   * @param PID   identifier of digital object being reflected upon
   * @param asOfDateTime
   * @return an array of behavior definition PIDs
   * @throws ServerException
   */
  public String[] getBehaviorDefinitions(Context context, String PID,
      Calendar asOfDateTime) throws ServerException
  {
    // FIXIT! In FUTURE this method might consult some source that tells
    // what behavior definitions are appropriate to dynamically associate
    // with the object.  The rules for association might be based on the
    // context or based on something about the particular object (PID).
    // There is one rule that is always true - associate the Default
    // behavior definition with EVERY object. For now we will just take the
    // dynamic behavior definitions that were loaded by DynamicAccessModule.
    // NOTE: AT THIS TIME THERE THERE IS JUST ONE LOADED, NAMELY,
    // THE DEFAULT DISSEMINATOR BDEF (bDefPID = fedora-system:3)

    ArrayList bdefs = new ArrayList();
    Iterator iter = dynamicBDefToMech.keySet().iterator();
    while (iter.hasNext())
    {
      bdefs.add(iter.next());
    }
    return (String[])bdefs.toArray(new String[0]);
  }

  /**
   * Get the behavior method defintions for a given dynamic disseminator that
   * is associated with the digital object. The dynamic disseminator is
   * identified by the bDefPID.
   * @param context
   * @param PID   identifier of digital object being reflected upon
   * @param bDefPID identifier of dynamic behavior definition
   * @param asOfDateTime
   * @return an array of method definitions
   * @throws ServerException
   */
  public MethodDef[] getBehaviorMethods(Context context, String PID,
      String bDefPID, Calendar asOfDateTime) throws ServerException
  {
    Class mechClass = (Class) dynamicBDefToMech.get(bDefPID);
    if (mechClass != null)
    {
      try
      {
        Method method = mechClass.getMethod("reflectMethods", null);
        return (MethodDef[])method.invoke(null, null);
      }
      catch (Exception e)
      {
        throw new GeneralException("[DynamicAccessImpl] returned error when "
            + "attempting to get dynamic behavior method definitions. "
            + "The underlying error class was: "
            + e.getClass().getName() + ". The message "
            + "was \"" + e.getMessage() + "\"");
      }
    }
    throw new MethodNotFoundException("[DynamicAccessImpl] The object, "
          + PID + " does not have the dynamic behavior definition " + bDefPID);
  }

  /**
   * Get an XML encoding of the behavior defintions for a given dynamic
   * disseminator that is associated with the digital object.  The dynamic
   * disseminator is identified by the bDefPID.
   * @param context
   * @param PID  identifier of digital object being reflected upon
   * @param bDefPID  identifier of dynamic behavior definition
   * @param asOfDateTime
   * @return MIME-typed stream containing XML-encoded method definitions
   * @throws ServerException
   */
  public MIMETypedStream getBehaviorMethodsXML(Context context, String PID,
      String bDefPID, Calendar asOfDateTime) throws ServerException
  {
    return null;
  }

  /**
   * Perform a dissemination for a behavior method that belongs to a
   * dynamic disseminator that is associate with the digital object.  The
   * method belongs to the dynamic behavior definition and is implemented
   * by a dynamic behavior mechanism (which is an internal service in the
   * repository access subsystem).
   * @param context
   * @param PID  identifier of the digital object being disseminated
   * @param bDefPID  identifier of dynamic behavior definition
   * @param methodName
   * @param userParms
   * @param asOfDateTime
   * @return a MIME-typed stream containing the dissemination result
   * @throws ServerException
   */
  public MIMETypedStream getDissemination(Context context, String PID,
      String bDefPID, String methodName, Property[] userParms,
      Calendar asOfDateTime, DOReader reader) throws ServerException
  {
    if (bDefPID.equalsIgnoreCase("fedora-system:3"))
    {
      // FIXIT!! Use lookup to dynamicBDefToMech table to get class for
      // DefaultBehaviorImpl and construct via Java reflection.
      Object result = dispatcher.invokeMethod(
          new DefaultBehaviorImpl(context, asOfDateTime,
            reader, m_access, reposBaseURL, reposHomeDir), methodName, userParms);
      if (result.getClass().getName().equalsIgnoreCase(
        "fedora.server.storage.types.MIMETypedStream"))
      {
        return (MIMETypedStream)result;
      }
      else
      {
          throw new GeneralException("[DynamicAccessImpl] returned error. "
            + "Internal service must return a MIME typed stream. "
            + "(see fedora.server.storage.types.MIMETypedStream)");
      }
    }
    else
    {
      // FIXIT! (FUTURE) Open up the possibility of there being other
      // kinds of dynamic behaviors.  Use the bDefPID to locate the
      // appropriate mechanism for the dynamic behavior.  In future
      // we want the mechanism for a dynamic behavior def intion to
      // be able to be either an internal services, a local services,
      // or a distributed service.  We'll have to rework some things to
      // be able to see what kind of mechanism we have, and to do the
      // request dispatching appropriately.
    }
    return null;
  }

  /**
   * Get the definitions for all dynamic disseminations on the object. This will
   * return the method definitions for all methods for all of the dynamic
   * disseminators associated with the object.
   * @param context
   * @param PID  identifier of digital object being reflected upon
   * @param asOfDateTime
   * @return an array of object method definitions
   * @throws ServerException
   */
  public ObjectMethodsDef[] getObjectMethods(Context context, String PID,
      Calendar asOfDateTime) throws ServerException
  {
    String[] bDefPIDs = getBehaviorDefinitions(context, PID, asOfDateTime);
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    ArrayList objectMethods = new ArrayList();
    for (int i=0; i<bDefPIDs.length; i++)
    {
      MethodDef[] methodDefs =
        getBehaviorMethods(context, PID, bDefPIDs[i], asOfDateTime);
      for (int j=0; j<methodDefs.length; j++)
      {
        ObjectMethodsDef method = new ObjectMethodsDef();
        method.PID = PID;
        method.asOfDate = versDateTime;
        method.bDefPID = bDefPIDs[i];
        method.methodName = methodDefs[j].methodName;
        method.methodParmDefs = methodDefs[j].methodParms;
        objectMethods.add(method);
      }
    }
    return (ObjectMethodsDef[])objectMethods.toArray(new ObjectMethodsDef[0]);
  }

  /**
   * Get the profile information for the digital object.  This contain key
   * metadata and URLs for the Dissemination Index and Item Index of the
   * object.
   * @param context
   * @param PID  identifier of digital object being reflected upon
   * @param asOfDateTime
   * @return an object profile data structure
   * @throws ServerException
   */
  public ObjectProfile getObjectProfile(Context context, String PID,
    Calendar asOfDateTime) throws ServerException
  {
    // FIXIT! Return something here.
    return null;
  }

  // FIXIT: What do these mean in this context...anything?
  // Maybe these methods' exposure needs to be re-thought?
  public FieldSearchResult findObjects(Context context,
          String[] resultFields, int maxResults, FieldSearchQuery query)
          throws ServerException {
      return null;
  }

  // FIXIT: What do these mean in this context...anything?
  // Maybe these methods' exposure needs to be re-thought?
  public FieldSearchResult resumeFindObjects(Context context,
          String sessionToken) throws ServerException {
      return null;
  }

  public boolean isDynamicBehaviorDefinition(Context context, String PID,
        String bDefPID)
        throws ServerException
  {
    if (dynamicBDefToMech.containsKey(bDefPID))
    {
      System.out.println("DETECTED A DYNAMIC BEHAVIOR DEF: " + bDefPID);
      return true;
    }
    return false;
  }
}