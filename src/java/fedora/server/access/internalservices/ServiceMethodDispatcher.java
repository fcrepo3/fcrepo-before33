package fedora.server.access.internalservices;

import java.lang.Object;
import java.lang.reflect.*;
import fedora.server.errors.ServerException;
import fedora.server.errors.GeneralException;
import fedora.server.storage.types.Property;

/**
 * <p>Title: ServiceMethodDispatcher.java</p>
 * <p>Description:  Invokes a method on an internal service.
 * This is done using Java reflection where the service is
 * the target object of a dynamic  method request.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette payette@cs.cornell.edu
 * @version 1.0
 */

public class ServiceMethodDispatcher {

    /**
      *  Invoke a method on an internal service.  This is done using
      *  Java reflection where the service is the target object of a dynamic
      *  method request.
     **/
    public Object invokeMethod(Object service_object, String methodName,
        Property[] userParms) throws ServerException
    {
        System.out.println("invokeMethod: composing method request...");
        Method method = null;
        //Object method_result = null;
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