package fedora.server.storage.types;

/**
 * <p>Title: MethodParmDef.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

public class MethodParmDef
{

    public String parmName = null;
    public String parmType = null;
    public String parmDefaultValue = null;
    public String[] parmDomainValues = new String[0];
    public boolean parmRequired = true;
    public String parmLabel = null;

    public MethodParmDef()
    {

    }
}