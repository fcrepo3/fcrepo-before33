package fedora.server.access.internalservices;

import fedora.server.errors.ServerException;
import fedora.server.storage.types.MethodDef;

/**
 * <p>Title: InternalService.java</p>
 * <p>Description:  Abstract class that should be extended by every
 * internal service class.  This defines the methods that
 * all internal services must implement.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */
public abstract class InternalService
{

  /**
   * <p>A method to reflect the behavior method definitions
   * implemented by the internal service.</p>
   */
  public static MethodDef[] reflectMethods() throws ServerException
  {
    return null;
  }

}