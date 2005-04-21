package fedora.client.batch.types;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

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

  private ArrayList m_auditRecordIdList;

  public String parentPID;

  public boolean isNew=false;

  public String dissID;

  public String dissLabel;

  public String dissVersionID;

  public String bDefID;

  public String bMechID;

  public String dsBindMapID;

  public fedora.server.storage.types.DSBindingMap dsBindMap;

  public Date dissCreateDT;

  public String dissState;

  public String asOfDate;
  
  public boolean force = false;
  
  public String logMessage;

  public Disseminator()
  {
    m_auditRecordIdList=new ArrayList();
  }

  public List auditRecordIdList()
  {
    return m_auditRecordIdList;
  }
}