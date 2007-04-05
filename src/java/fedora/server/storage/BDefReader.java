/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage;

import fedora.server.storage.types.*;
import fedora.server.errors.ServerException;
import java.io.InputStream;
import java.util.Date;

/**
 *
 * <p><b>Title:</b> BDefReader.java</p>
 * <p><b>Description:</b> Interface for reading Behavior Mechanism Objects.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public interface BDefReader extends DOReader
{
  public MethodDef[] getAbstractMethods(Date versDateTime) throws ServerException;

  public InputStream getAbstractMethodsXML(Date versDateTime) throws ServerException;
}