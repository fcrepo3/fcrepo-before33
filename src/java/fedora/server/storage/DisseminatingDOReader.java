package fedora.server.storage;

/**
 * <p>Title: DisseminatingDOReader.java</p>
 * <p>Description: Interface for getting dissemination information</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */

// java imports
import java.util.Date;
import java.util.Vector;

// fedora imports
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.errors.ObjectNotFoundException;


public interface DisseminatingDOReader extends DOReader
{
  public Vector getDissemination(String PID, String bDefPID, String methodName,
                                 Date versDateTime) throws ObjectNotFoundException;
  public Vector getObject(String PID, Date versDateTime) throws ObjectNotFoundException;
}