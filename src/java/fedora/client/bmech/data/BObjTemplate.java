package fedora.client.bmech.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class BObjTemplate {

    private String bObjPID = null;
    private String bObjLabel = null;
    private String bObjName = null;
    private DCElement[] dcRecord = new DCElement[0];
    private Datastream[] docDatastreams = new Datastream[0];
    private Method[] methods;
    private HashMap hmap_methods = new HashMap();


    public BObjTemplate()
    {
    }

    public String getbObjPID()
    {
        return bObjPID;
    }

    public void setbObjPID(String bObjPID)
    {
        this.bObjPID = bObjPID;
    }

    public String getbObjLabel()
    {
        return bObjLabel;
    }

    public void setbObjLabel(String bObjLabel)
    {
        this.bObjLabel = bObjLabel;
    }

    public String getbObjName()
    {
        return bObjName;
    }

    public void setbObjName(String bObjName)
    {
        this.bObjName = bObjName;
    }

    public DCElement[] getDCRecord()
    {
        return dcRecord;
    }

    public void setDCRecord(DCElement[] dcRecord)
    {
        this.dcRecord = dcRecord;
    }

    public Datastream[] getDocDatastreams()
    {
        return docDatastreams;
    }

    public void setDocDatastreams(Datastream[] docDatastreams)
    {
        this.docDatastreams = docDatastreams;
    }

    public HashMap getMethodsHashMap()
    {
        return hmap_methods;
    }

    public void setMethodsHashMap(HashMap hmap_methods)
    {
        this.hmap_methods = hmap_methods;
    }

    public Method[] getMethods()
    {
        return methods;
    }

    public void setMethods(Method[] methods)
    {
        this.methods = methods;
    }
  }