package fedora.server.storage.abstraction;

/**
 * <p>Title: BMechDSBindSpec.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

public class BMechDSBindSpec
{
  public String bMechPID;

  public String bDefPID;

  public String bindSpecLabel;

  public String state;

  public BMechDSBindRule[] dsBindRule;

  public BMechDSBindSpec()
  {
  }
}
