package fedora.server.storage.types;

import java.util.Date;

/**
 *
 * <p><b>Title:</b> Disseminator.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class Disseminator
{

  //private ArrayList m_auditRecordIdList;

  public String parentPID;

  public boolean isNew=false;

  public String dissID;

  public String dissLabel;

  public String dissVersionID;

  public String bDefID;

  public String bMechID;

  public String dsBindMapID;

  public DSBindingMap dsBindMap;

  public Date dissCreateDT;

  public String dissState;
  
  public boolean dissVersionable;

  public Disseminator()
  {
  }
}