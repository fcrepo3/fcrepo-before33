package fedora.server.storage;

import java.util.Date;

import fedora.server.errors.GeneralException;
import fedora.server.storage.types.DisseminationBindingInfo;
import fedora.server.storage.types.ObjectMethodsDef;

/**
 * <p>Title: DisseminatingDOReader.java</p>
 * <p>Description: Interface for getting dissemination information</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public interface DisseminatingDOReader extends DOReader
{
  public DisseminationBindingInfo[] getDissemination(String PID, String bDefPID,
      String methodName, Date versDateTime) throws GeneralException;

  public ObjectMethodsDef[] getObjectMethods(String PID, Date versDateTime)
      throws GeneralException;
}