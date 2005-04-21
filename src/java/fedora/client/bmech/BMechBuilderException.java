package fedora.client.bmech;

/**
 *
 * <p><b>Title:</b> BMechBuilderException.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class BMechBuilderException
        extends Exception
{

    public BMechBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public BMechBuilderException(String message) {
        super(message);
    }

}