//=====================================================================
//                         Mellon FEDORA
//   Flexible Extensible Digital Object Repository Architecture
//=====================================================================
package fedora.server.management;

/**
 * <p>Title: FedoraManagement.java </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

public interface FedoraManagement
{
    // API-M Access Methods
    public byte[] GetObject(String pid);

    public String[] ListObjectPIDs(String namespace);

    public Datastream[] GetDatastreams(String pid);

    public Disseminator[] GetDisseminators(String pid);

    // API-M Creation Methods

    public String IngestObject(byte[] mets);

    public String CreateObject(String pid, String label, String contentModelID);

    public boolean CreateRefDatastream(String pid, String url);

    public boolean CreateInternalDatastream(String pid, String dsLabel, byte[] content);

    public boolean CreateInternalDatastream(String pid, String dsLabel, String url);

    public boolean CreateMetadataDatastream(String pid, String dsLabel, String mdType, byte[] xmlMD);

    public boolean CreateMetadataDatastream(String pid, String dsLabel, String mdType, String xmlurl);

    public boolean CreateDisseminator(String pid, String bDefID, String bMechID, String dissLabel, DSBindingMap dsBindMap);

    // API-M Modify Methods

    public boolean ModifyRefDatastream(String pid, String datastreamID, String dsLabel, String url);

    public boolean ModifyInternalDatastream(String pid, String datastreamID, String dsLabel, byte[] content);

    public boolean ModifyInternalDatastream(String pid, String datastreamID, String dsLabel, String url);

    public boolean ModifyMetadataDatastream(String pid, String datastreamID, String dsLabel, byte[] xmlMD);

    public boolean ModifyMetadataDatastream(String pid, String datastreamID, String dsLabel, String xmlurl);

    public boolean ModifyDisseminator(String pid, String dissID, String bDefID, String bMechID, String dissLabel, DSBindingMap dsBindMap);


    // API-M Delete Methods

    public boolean DeleteObject(String pid);

    public boolean DeleteDatastream(String pid, String datastreamID);

    public boolean DeleteDisseminator(String pid, String dissID);

}








