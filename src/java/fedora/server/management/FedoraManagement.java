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
 * @author Sandy Payette xxxxx
 * @version 1.0
 */

public interface FedoraManagement
{
  /**
   * API-M Access Methods
   */
    public byte[] GetObject(String pid);

    public String[] ListObjectPIDs(String namespace);

    public Datastream[] GetDatastreams(String pid);

    public Disseminator[] GetDisseminators(String pid);

    /**
     * API-M Creation Methods
     */

    public String IngestObject(byte[] mets, Agent[] agents);

    public String CreateObject(String pid, String label, String contentModelID, Agent[] agents);

    public void ObtainObjectLock(String pid, String userID, String reason);

    public void ReleaseObjectLock(String pid);

    /** External Datastream:  Referenced Content */
    public String AddDatastreamERC(String pid, String url);

    /** Internal Datastream: Fedora Content */
    public String AddDatastreamFC(String pid, String dsLabel, byte[] content);

    /** Internal Datastream: Fedora Content */
    public String AddDatastreamFCURL(String pid, String dsLabel, String url);

    /** Internal Datastream : Fedora User Metadata */
    public String AddDatastreamFUM(String pid, String dsLabel, String mdType, byte[] xmlMD);

    /** Internal Datastream : Fedora User Metadata */
    public String AddDatastreamFUMURL(String pid, String dsLabel, String mdType, String xmlurl);

    public String AddDisseminator(String pid, String bDefID, String bMechID, String dissLabel, DSBindingMap dsBindMap);

    // API-M Modify Methods

    public boolean ModifyDatastreamERC(String pid, String datastreamID, String dsLabel, String url);

    public boolean ModifyDatastreamFC(String pid, String datastreamID, String dsLabel, byte[] content);

    public boolean ModifyDatastreamFCURL(String pid, String datastreamID, String dsLabel, String url);

    public boolean ModifyDatastreamFUM(String pid, String datastreamID, String dsLabel, byte[] xmlMD);

    public boolean ModifyDatastreamFUMURL(String pid, String datastreamID, String dsLabel, String xmlurl);

    public boolean ModifyDisseminator(String pid, String dissID, String bDefID, String bMechID, String dissLabel, DSBindingMap dsBindMap);


  /**
   * API-M Deletion Methods
   */

    public boolean DeleteObject(String pid);

    public boolean DeleteDatastream(String pid, String datastreamID);

    public boolean DeleteDisseminator(String pid, String dissID);

}








