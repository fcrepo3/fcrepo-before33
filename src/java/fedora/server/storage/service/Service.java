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