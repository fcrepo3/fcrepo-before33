package fedora.client.bmech.data;

/**
 *
 * <p><b>Title:</b> MethodParm.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
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