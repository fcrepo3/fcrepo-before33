package fedora.server.access;

import java.util.Date;
/**
 * <p><b>Title: </b>ObjectProfile.java</p>
 * <p><b>Description: </b>Data structure to contain a profile of
 * a digital object that includes both stored information about the object
 * and dynamic information about the object. </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class ObjectProfile
{
  public String PID = null;
  public String objectLabel = null;
  public String objectType = null;
  public String objectContentModel = null;
  public Date objectCreateDate = null;
  public Date objectLastModDate = null;
  public String dissIndexViewURL = null;
  public String itemIndexViewURL = null;
}