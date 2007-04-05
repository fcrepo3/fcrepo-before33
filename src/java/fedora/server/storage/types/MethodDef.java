/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.types;

/**
 *
 * <p><b>Title:</b> MethodDef.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class MethodDef
{

    public String methodName = null;
    public String methodLabel = null;
    public MethodParmDef[] methodParms = new MethodParmDef[0];

    public MethodDef()
    {
    }

}