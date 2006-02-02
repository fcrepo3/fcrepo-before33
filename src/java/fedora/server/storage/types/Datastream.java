package fedora.server.storage.types;

import fedora.server.errors.StreamIOException;

import java.io.InputStream;
import java.util.Date;

/**
 *
 * <p><b>Title:</b> Datastream.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class Datastream
{

  public boolean isNew=false;
  
  public String DatastreamID;
  
  public String[] DatastreamAltIDs = new String[0];
 
  public String DSFormatURI;
  
  public String DSMIME;
  
  /** Datastream Control Group:
   *  This indicates the nature of the repository's control over the
   *  datastream content.  Values are:
   *  <p>
   *  R = Redirected.
   *      The datastream resides on an external server and is referenced
   *      by a URL.  When a dissemination request for the *datastream*
   *      comes through, Fedora sends an HTTP redirect to the client,
   *      thereby causing the client to directly access the datastream
   *      from its external location.  This is useful in cases where
   *      the datastream is really some sort of streaming media that
   *      cannot be piped through Fedora, or the datastream is an
   *      HTML document with relative hyperlinks to the server on
   *      which is is normally hosted.
   *  E = External Referenced.  The datastream content is external to the repository
   *      and referenced by URL.  The content is not under the direct
   *      custodianship of the repository.  The URL is considered public
   *      so the repository does not worry about whether it exposes the
   *      datastream location to collaborating services.
   *  M = Managed Content.  The datastream content is stored and managed
   *      by the repository.  The content is considered under the direct
   *      custodianship of the repository.  The repository does not expose
   *      the underlying storage location to collaborating services and
   *      it mediates all access to the content by collaborating services.
   *  X = Inline XML Metadata.  The datastream content is user-defined
   *      XML metadata that is stored within the digital object XML file itself.
   *      As such, it is intrinsically bound to the digital object, and by
   *      implication, it is stored and managed by the repository.  The content
   *      considered under the custodianship of the repository.
   */
  public String DSControlGrp;

  /** Info Type: DATA or one of the METS MDType values */
  /** Used to maintain backwards compatibility with METS-Fedora */
  public String DSInfoType;

  public String DSState;
  
  public boolean DSVersionable;
  
  // Version-level attributes:
  public String DSVersionID;
  public String DSLabel;
  public Date DSCreateDT;
  public long DSSize;
  public String DSLocation;
  public String DSLocationType;


  public Datastream() {
  } 

  public InputStream getContentStream()
          throws StreamIOException
  {
      return null;
  }

  // Get a complete copy of this datastream
  public Datastream copy() {
      Datastream ds = new Datastream();
      copy(ds);
      return ds;
  }

  // Copy this instance into target
  public void copy(Datastream target) {

      target.isNew = isNew;
      target.DatastreamID = DatastreamID;
      if (DatastreamAltIDs != null) {
          target.DatastreamAltIDs = new String[DatastreamAltIDs.length];
          for (int i = 0; i < DatastreamAltIDs.length; i++) {
              target.DatastreamAltIDs[i] = DatastreamAltIDs[i];
          }
      }
      target.DSFormatURI = DSFormatURI;
      target.DSMIME = DSMIME; 
      target.DSControlGrp = DSControlGrp;
      target.DSInfoType = DSInfoType;
      target.DSState = DSState;
      target.DSVersionable = DSVersionable;
      target.DSVersionID = DSVersionID;
      target.DSLabel = DSLabel;
      target.DSCreateDT = DSCreateDT;
      target.DSSize = DSSize;
      target.DSLocation = DSLocation;
      target.DSLocationType = DSLocationType;
  }

}
