package fedora.server.storage.types;

/**
 * <p>Title: ObjectMethodsDef.java</p>
 * <p>Description: Data structure to contain all method definitions for
 * a digital object. </p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
import java.util.Date;
import fedora.server.storage.types.MethodDef;

public class ObjectMethodsDef
{
  public String PID = null;
  public String bDefPID = null;
  public String methodName = null;
  public Date asOfDate = null;

  public ObjectMethodsDef()
  {
  }

}