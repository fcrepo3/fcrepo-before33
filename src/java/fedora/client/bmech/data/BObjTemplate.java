package fedora.client.bmech.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * <p><b>Title:</b> BObjTemplate.java</p>
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
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version 1.0
 */

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