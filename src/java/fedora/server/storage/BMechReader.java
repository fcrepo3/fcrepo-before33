package fedora.server.storage;

/**
 * <p>Title: BMechReader.java</p>
 * <p>Description: Interface for reading Behavior Mechanism Objects</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import fedora.server.storage.types.*;
import fedora.server.errors.ServerException;
import java.io.InputStream;
import java.util.Date;

public interface BMechReader extends DOReader
{
  public MethodDef[] getServiceMethods(Date versDateTime) throws ServerException;

  public MethodDefOperationBind[] getServiceMethodBindings(Date versDateTime) throws ServerException;

  public InputStream getServiceMethodsXML(Date versDateTime) throws ServerException;

  public BMechDSBindSpec getServiceDSInputSpec(Date versDateTime) throws ServerException;
}