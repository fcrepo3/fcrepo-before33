package fedora.client.bmech.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class BMechTemplate {

    private String bDefPID = null;
    private String bMechLabel = null;
    private String bMechName = null;
    private DCElement[] dcRecord = new DCElement[0];
    private boolean hasBaseURL;
    private String serviceBaseURL;
    private Method[] methods;
    private HashMap bMechMethods = new HashMap();
    private Vector dsBindingKeys = new Vector();
    private DSInputRule[] dsInputSpec;
    private Datastream[] docDatastreams = new Datastream[0];


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

    public String getbMechName()
    {
        return bMechName;
    }

    public void setbMechName(String bMechName)
    {
        this.bMechName = bMechName;
    }

    public DCElement[] getDCRecord()
    {
        return dcRecord;
    }

    public void setDCRecord(DCElement[] dcRecord)
    {
        this.dcRecord = dcRecord;
    }

    public Vector getDSBindingKeys()
    {
        return dsBindingKeys;
    }

    public void setDSBindingKeys(Vector dsBindingKesy)
    {
        this.dsBindingKeys = dsBindingKeys;
    }

    public Datastream[] getDocDatastreams()
    {
        return docDatastreams;
    }

    public void setDocDatastreams(Datastream[] docDatastreams)
    {
        this.docDatastreams = docDatastreams;
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