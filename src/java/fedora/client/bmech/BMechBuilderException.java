/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.bmech;

/**
 * @author Sandy Payette
 */
public class BMechBuilderException
        extends Exception {

    private static final long serialVersionUID = 1L;

    public BMechBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public BMechBuilderException(String message) {
        super(message);
    }

}
