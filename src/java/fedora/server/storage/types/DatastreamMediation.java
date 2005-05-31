package fedora.server.storage.types;

/**
 *
 * <p><b>Title:</b> DatastreraMediation.java</p>
 * <p><b>Description:</b> A data structure containing information needed for the
 * datastream mediation service.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class DatastreamMediation
{
  public String mediatedDatastreamID = null;
  public String dsLocation = null;
  public String dsControlGroupType = null;
  public String callbackRole = null;
  public String callUsername = null;
  public String callPassword = null;
  public String methodName = null;
  public boolean callBasicAuth = false;
  public boolean callbackBasicAuth = false;
  public boolean callSSL = false;
  public boolean callbackSSL = false;
}