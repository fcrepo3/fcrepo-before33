package fedora.server.storage;

import fedora.server.storage.types.*;
import fedora.server.errors.ServerException;
import java.io.InputStream;
import java.util.Date;

/**
 *
 * <p><b>Title:</b> BMechReader.java</p>
 * <p><b>Description:</b> Interface for reading Behavior Mechanism Objects.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public interface BMechReader extends DOReader
{
  public MethodDef[] getServiceMethods(Date versDateTime) throws ServerException;

  public MethodDefOperationBind[] getServiceMethodBindings(Date versDateTime)
      throws ServerException;

  public InputStream getServiceMethodsXML(Date versDateTime) throws ServerException;

  public BMechDSBindSpec getServiceDSInputSpec(Date versDateTime) throws ServerException;

  public MethodParmDef[] getServiceMethodParms(String methodName, Date versDateTime) throws ServerException;
}