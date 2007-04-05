/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.types;

import fedora.server.storage.types.MethodParmDef;

/**
 *
 * <p><b>Title:</b> Dissemination.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class Dissemination
{

  public String DSBindKey = null;
  public MethodParmDef[] methodParms = null;
  public String DSLocation = null;
  public String AddressLocation = null;
  public String OperationLocation = null;
  public String ProtocolType = null;



  public Dissemination()
  {
  }

}