/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.types;

/**
 *
 * <p><b>Title:</b> DSBindingMap.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class DSBindingAugmented extends DSBinding
{
  public String DSVersionID = null;

  public String DSLabel = null;

  public String DSMIME = null;

  public String DSControlGrp = null;

  public String DSLocation = null;

  public DSBindingAugmented()
  {
  }
}