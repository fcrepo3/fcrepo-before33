package fedora.client.bmech.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class BMechTemplate extends BObjTemplate
{
    // variables specific to behavior mechanisms
    private String bDefContractPID = null;
    private boolean hasBaseURL;
    private String serviceBaseURL;
    private DSInputRule[] dsInputSpec;
    private Vector dsBindingKeys = new Vector();

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

    public void setDSBindingKeys(Vector dsBindingKesy)
    {
        this.dsBindingKeys = dsBindingKeys;
    }
  }
