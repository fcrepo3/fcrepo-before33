package fedora.server.management;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.util.Date;

public class AuditRecord
{

  public String auditRecordID;

  public String eventProcess;

  public String eventAction;

  public String eventResponsibility;

  public Date eventDate;

  public String eventJustification;

  public AuditRecord()
  {
  }

}