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

public class SystemMetadata
{

  public static final String objectType = "FEDORA_DO";

  public String objectPID;

  public String objectLabel;

  public String objectContentModelID;

  public Date objectCreateDateTime;

  public Date objectModDataTime;

  public String objectState;

  public Agent[] objectAgents;

  public AuditRecord[] objectAuditTrail;

  public SystemMetadata()
  {
  }

}