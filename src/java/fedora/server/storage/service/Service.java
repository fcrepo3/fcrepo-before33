package fedora.server.storage.service;

/**
 * <p>Title: Service.java</p>
 * <p>Description: A data structure for holding WSDL Service definition.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

public class Service
{
  public String serviceName;
  public PortType portType;
  public Port[] ports;
}