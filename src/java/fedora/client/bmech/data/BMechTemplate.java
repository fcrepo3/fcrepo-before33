package fedora.client.bmech.data;

import java.util.Vector;

/**
 *
 * <p><b>Title:</b> BMechTemplate.java</p>
 * <p><b>Description:</b> </p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
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
