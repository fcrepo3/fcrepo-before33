package fedora.server.access;

import java.util.Date;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.ObjectMethodsDef;
/**
 * <p>Title: ObjectProfile.java</p>
 * <p>Description: Data structure to contain a profile of
 * a digital object that includes both stored information about the object
 * and dynamic information about the object. </p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */


public class ObjectProfile
{
  public String PID = null;
  public String objectLabel = null;
  public String objectType = null;
  public String objectContentModel = null;
  public String objectCreateDate = null;
  public String objectLastModDate = null;
  public String dissIndexViewURL = null;
  public String itemIndexViewURL = null;
}