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

    public static final String PASS_BY_REF = "URL_REF";
    public static final String PASS_BY_VALUE = "VALUE";

    public static final String DATASTREAM_INPUT = "fedora:datastreamInputType";
    public static final String USER_INPUT = "fedora:userInputType";
    public static final String DEFAULT_INPUT = "fedora:defaultInputType";

    public String parmName = null;
    public String parmType = null;
    public String parmDefaultValue = null;
    public String[] parmDomainValues = new String[0];
    public boolean parmRequired = true;
    public String parmLabel = null;
    public String parmPassBy = null;

    public MethodParmDef()
    {

    }
}