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

    public String parmName;
    public String parmType;
    public String parmDefaultValue;
    public String[] parmDomainValues;
    public boolean parmRequired;
    public String parmLabel;

    public MethodParmDef()
    {

    }
}