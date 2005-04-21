package fedora.server.access.defaultdisseminator;

import fedora.server.errors.ServerException;
import fedora.server.storage.types.MethodDef;

/**
 * <p><b>Title: </b>InternalService.java</p>
 * <p><b>Description: </b>Abstract class that should be extended by every
 * internal service class.  This defines the methods that
 * all internal services must implement.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
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