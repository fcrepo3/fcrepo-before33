package fedora.server.storage.service;

/**
 * <p>Title: Port.java</p>
 * <p>Description: A data structure for holding WSDL Port which defines
 * bindings for a set of abstract operations.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

public class Port
{
  public String portName;
  public String portBaseURL;
  public Binding binding;
}