package fedora.server.storage;

import java.util.Vector;
import fedora.server.access.MIMETypedStream;

/**
 * <p>Title: </p>
 * <p>Description: Interface for getting dissemination information</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */


public interface DisseminatingDOReader extends DOReader
{
  public Vector getDissemination(String PID, String bDefPID, String method);
  public MIMETypedStream getHttpContent(String url);
}