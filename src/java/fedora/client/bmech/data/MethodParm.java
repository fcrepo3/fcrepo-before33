package fedora.client.bmech.data;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class MethodParm {

    public static final String PASS_BY_REF = "URL_REF";
    public static final String PASS_BY_VALUE = "VALUE";

    public static final String DATASTREAM_INPUT = "fedora:datastreamInputType";
    public static final String USER_INPUT = "fedora:userInputType";
    public static final String DEFAULT_INPUT = "fedora:defaultInputType";

    public String parmName = null;
    public String parmType = null;
    public String parmDefaultValue = null;
    public String[] parmDomainValues = new String[0];
    public String parmRequired = null;
    public String parmLabel = null;
    public String parmPassBy = null;

    public MethodParm()
    {

    }
}