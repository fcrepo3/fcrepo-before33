package fedora.server.storage.service;

/**
 * <p>Title: Mmap.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */
import java.util.Hashtable;
import fedora.server.storage.types.MethodDef;

public class Mmap
{
    public String mmapName = null;
    public MethodDef[] fedoraMethodDefs = new MethodDef[0];
    //public MmapMethod[] mmapMethods = new MmapMethod[0];
    public Hashtable wsdlMsgToMethodDef;
    public Hashtable wsdlOperationToMethodDef;
}