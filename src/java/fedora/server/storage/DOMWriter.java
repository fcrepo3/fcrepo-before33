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
import java.io.InputStream;

public abstract class DOMWriter
{

  public DOMWriter(String objectPID, InputStream objectXML)
  {
  }

  public abstract Document writeObject();

}