package fedora.client.bmech.data;

import java.util.HashMap;
import java.util.Iterator;

public class BMechTemplate {

    private String bDefPID = null;
    private String bMechLabel = null;
    private HashMap dcRecordMap = new HashMap();
    private boolean hasBaseURL;
    private String serviceBaseURL;
    private Method[] methods;
    private HashMap bMechMethods = new HashMap();
    private DSInputRule[] dsInputSpec;


    public BMechTemplate()
    {
    }

    public String getbDefPID()
    {
        return bDefPID;
    }

    public void setbDefPID(String bDefPID)
    {
        this.bDefPID = bDefPID;
    }

    public String getbMechLabel()
    {
        return bMechLabel;
    }

    public void setbMechLabel(String bMechLabel)
    {
        this.bMechLabel = bMechLabel;
    }

    public HashMap getDCRecord()
    {
        return dcRecordMap;
    }

    public void setDCRecord(HashMap dcRecordMap)
    {
        this.dcRecordMap = dcRecordMap;
    }

    public HashMap getBMechMethodMap()
    {
        return bMechMethods;
    }

    public void setBMechMethodMap(HashMap bMechMethods)
    {
        this.bMechMethods = bMechMethods;
    }

    public Method[] getBMechMethods()
    {
        return methods;
    }

    public void setBMechMethods(Method[] methods)
    {
        this.methods = methods;
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


  }