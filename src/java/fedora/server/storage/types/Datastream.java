package fedora.server.storage.types;

/**
 * <p>Title: Datastream.java </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import fedora.server.errors.StreamIOException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Datastream
{

  private ArrayList m_auditRecordIdList;

  public Datastream() {
      m_auditRecordIdList=new ArrayList();
  }

  public String DatastreamID;

  public String DSVersionID;

  public String DSLabel;

  public String DSMIME;

  public Date DSCreateDT;

  public long DSSize;

  /** Datastream Control Group:
   *  This indicates the nature of the repository's control over the
   *  datastream content.  Values are:
   *  P = Protected-ExternalRef.  The datastream content is external to
   *      to the repository and referenced by a URL.  The content is not
   *      under the direct custodianship of the repository.  However,
   *      the datastream content is protected by the repository.
   *      The repository will not reveal the underlying location of the
   *      datastream content and will mediate all access to the content
   *      by collaborating services (e.g., a behavior mechanism service).
   *  E = ExternalRef.  The datastream content is external to the repository
   *      and referenced by URL.  The content is not under the direct
   *      custodianship of the repository.  The URL is considered public
   *      so the repository does not worry about whether it exposes the
   *      datastream location to collaborating services.
   *  M = Managed-Internal.  The datastream content is stored and managed
   *      by the repository.  The content is considered under the direct
   *      custodianship of the repository.  The repository does not expose
   *      the underlying storage location to collaborating services and
   *      it mediates all access to the content by collaborating services.
   *  X = Inline-XML-User-Metadata.  The datastream content is user-defined
   *      XML metadata that is stored within the digital object XML file itself.
   *      As such, it is intrinsically bound to the digital object, and by
   *      implication, it is stored and managed by the repository.  The content
   *      considered under the custodianship of the repository.
   */
  public String DSControlGrp;

  /** Info Type: DATA or one of the METS MDType values */
  public String DSInfoType;

  public String DSState;

  public String DSLocation;

  public List auditRecordIdList()
  {
      return m_auditRecordIdList;
  }

  public InputStream getContentStream()
          throws StreamIOException
  {
      return null;
  }

}