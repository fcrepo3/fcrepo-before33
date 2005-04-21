package fedora.server.storage.service;

/**
 *
 * <p><b>Title:</b> Port.java</p>
 * <p><b>Description:</b> A data structure for holding WSDL Port which defines
 * bindings for a set of abstract operations.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class Port
{
  public String portName;
  public String portBaseURL;
  public Binding binding;
}