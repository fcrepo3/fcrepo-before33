package fedora.server.storage.abstraction;

/**
 * <p>Title: BMechDSBindRule.java </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

public class BMechDSBindRule
{
  public String bindingKeyName;

  public int minNumBindings;

  public int maxNumBindings;

  public boolean ordinality;

  public String bindingLabel;

  public String bindingInstruction;

  public String[] bindingMIMETypes;

  public BMechDSBindRule()
  {
  }
}