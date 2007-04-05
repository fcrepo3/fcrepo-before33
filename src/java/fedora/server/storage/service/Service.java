/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.service;

/**
 *
 * <p><b>Title:</b> Service.java</p>
 * <p><b>Description:</b> A data structure for holding WSDL Service definition.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class Service
{
  public String serviceName;
  public PortType portType;
  public Port[] ports;
}