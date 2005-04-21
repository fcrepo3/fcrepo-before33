package fedora.client.bmech.data;

/**
 *
 * <p><b>Title:</b> Datastream.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class ServiceProfile
{
	public String serviceName = null;
	public String serviceLabel = null;
	public String serviceTestURL = null;

	public String transProtocol = null;
	public String msgProtocol = null;
	public String[] inputMIMETypes = new String[0];
	public String[] outputMIMETypes = new String[0];
	public ServiceSoftware[] software = new ServiceSoftware[0];

  public ServiceProfile()
  {
  }
}