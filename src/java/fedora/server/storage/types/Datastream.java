package fedora.server.storage.types;

import fedora.server.errors.StreamIOException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * <p><b>Title:</b> Datastream.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class Datastream
{

  private ArrayList m_auditRecordIdList;

  public boolean isNew=false;
  
  public String DatastreamID;
  
  public String DatastreamURI;
 
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
  
  public String DSVersionable;
  
  public String DSCurrent;
  
  // Version-level attributes:
  public String DSVersionID;
  public String DSLabel;
  public Date DSCreateDT;
  public long DSSize;
  public String DSLocation;


  public Datastream() {
	  m_auditRecordIdList=new ArrayList();
  }
  
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
