package fedora.server.access.internalservices;

import java.lang.Object;
import java.lang.reflect.*;
import fedora.server.errors.ServerException;
import fedora.server.errors.GeneralException;
import fedora.server.storage.types.Property;

/**
 * <p><b>Title: </b>ServiceMethodDispatcher.java</p>
 * <p><b>Description: </b>Invokes a method on an internal service.
 * This is done using Java reflection where the service is
 * the target object of a dynamic  method request.</p>
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
 * @author payette@cs.cornell.edu
 * @version 1.0
 */

public class ServiceMethodDispatcher {

   /**
    *  Invoke a method on an internal service.  This is done using
    *  Java reflection where the service is the target object of a dynamic
    *  method request.
    * @param service_object  the target object of the service request
    * @param methodName  the method to invoke on the target object
    * @param userParms   parameters to the method to invoke on target object
    * @return
    * @throws ServerException
    */
    public Object invokeMethod(Object service_object, String methodName,
        Property[] userParms) throws ServerException
    {
        System.out.println("invokeMethod: composing method request...");
        Method method = null;
        if (userParms == null)
        {
          userParms = new Property[0];
        }
        Object[] parmValues = new Object[userParms.length];
        Class[] parmClassTypes = new Class[userParms.length];
        for (int i = 0; i < userParms.length; i++)
        {
            // Get parm value.  Always treat the parm value as a string.
            parmValues[i] = new String(userParms[i].value);
            parmClassTypes[i] = parmValues[i].getClass();
        }
        // Invoke method: using Java Reflection
        try
        {
          method = service_object.getClass().getMethod(methodName, parmClassTypes);
          return method.invoke(service_object, parmValues);
        }
        catch (Exception e)
        {
          throw new GeneralException("ServiceMethodDispatcher returned error. The "
                                     + "underlying error was a "
                                     + e.getClass().getName() + "The message "
                                     + "was \"" + e.getMessage() + "\"");
        }
    }
}