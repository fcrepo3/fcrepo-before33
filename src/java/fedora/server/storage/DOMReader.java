package fedora.server.storage;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import org.w3c.dom.*;

public abstract class DOMReader
{

  public DOMReader(String objectPID)
  {
  }

  public abstract Document readObject();

}