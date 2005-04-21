package fedora.server.storage.service;

import java.util.Hashtable;

/**
 *
 * <p><b>Title:</b> Mmap.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class Mmap
{
    public String mmapName = null;
    //public MethodDef[] fedoraMethodDefs = new MethodDef[0];
    public MmapMethodDef[] mmapMethods = new MmapMethodDef[0];
    public Hashtable wsdlOperationToMethodDef;

    //public Hashtable wsdlOperationToDSInputKeys;
    //public MmapMethod[] mmapMethods = new MmapMethod[0];
    //public Hashtable wsdlMsgToMethodDef;
}