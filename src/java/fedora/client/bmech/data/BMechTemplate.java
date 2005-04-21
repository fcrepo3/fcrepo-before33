package fedora.client.bmech.data;

import java.util.Vector;

/**
 *
 * <p><b>Title:</b> BMechTemplate.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */

public class BMechTemplate extends BObjTemplate
{
    // variables specific to behavior mechanisms
    private String bDefContractPID = null;
    private boolean hasBaseURL = false;
    private String serviceBaseURL = null;
    private DSInputRule[] dsInputSpec = new DSInputRule[0];
    private Vector dsBindingKeys = new Vector();
    private ServiceProfile profile = null;

    public BMechTemplate()
    {
    }

    public String getbDefContractPID()
    {
        return bDefContractPID;
    }

    public void setbDefContractPID(String bDefContractPID)
    {
        this.bDefContractPID = bDefContractPID;
    }

    public void setHasBaseURL(boolean hasBaseURL)
    {
      this.hasBaseURL = hasBaseURL;
    }

    public boolean getHasBaseURL()
    {
      return hasBaseURL;
    }

    public String getServiceBaseURL()
    {
        return serviceBaseURL;
    }

    public void setServiceBaseURL(String in_baseURL)
    {
        serviceBaseURL = in_baseURL;
    }

    public void setDSInputSpec(DSInputRule[] dsInputSpec)
    {
        this.dsInputSpec = dsInputSpec;
    }

    public DSInputRule[] getDSInputSpec()
    {
        return dsInputSpec;
    }

    public Vector getDSBindingKeys()
    {
        return dsBindingKeys;
    }

    public void setDSBindingKeys(Vector dsBindingKeys)
    {
        this.dsBindingKeys = dsBindingKeys;
    }
    
	public void setServiceProfile(ServiceProfile profile)
	{
		this.profile = profile;
	}

	public ServiceProfile getServiceProfile()
	{
		return profile;
	}
  }
