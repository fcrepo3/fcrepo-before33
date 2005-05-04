package fedora.server.access.defaultdisseminator;

import fedora.server.errors.ServerException;
import fedora.server.storage.types.MIMETypedStream;

/**
 * <p><b>Title: </b>DefaultDisseminator.java</p>
 * <p><b>Description: </b>Defines the methods of the default behavior definition
 * that is associated with every Fedora Object.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public interface DefaultDisseminator

{

  /**
   * <p>Returns an HTML rendering of the object profile which contains
   * key metadata from the object, plus URLs for the object's Dissemination
   * Index and Item Index.  The data is returned as HTML in a
   * presentation-oriented format.  This is accomplished by doing an XSLT
   * transform on the XML that is obtained from getObjectProfile in API-A.</p>
   * @return
   * @throws ServerException
   */
  public MIMETypedStream viewObjectProfile() throws ServerException;

  /**
   * <p>Returns an HTML rendering of the Dissemination Index for the object.
   * The Dissemination Index is a list of method definitions that represent
   * all disseminations possible on the object.  The Dissemination Index
   * is returned as HTML in a presentation-oriented format.  This is
   * accomplished by doing an XSLT transform on the XML that is
   * obtained from listMethods in API-A.</p>
   * @return
   * @throws ServerException
   */
  public MIMETypedStream viewMethodIndex() throws ServerException;

  /**
   * <p>Returns an HTML rendering of the Item Index for the object.  The Item
   * Index is a list of all datastreams in the object.  The datastream items
   * can be data or metadata.  The Item Index is returned as HTML in a
   * presentation-oriented format.  This is accomplished by doing an XSLT
   * transform on the XML that is obtained from listDatastreams in API-A.</p>
   * @return
   * @throws ServerException
   */
  public MIMETypedStream viewItemIndex() throws ServerException;
  
  /**
   * <p>Returns the Dublin Core record for the object, if one exists.
   * The record is returned as HTML in a presentation-oriented format.</p>
   * @return
   * @throws ServerException
   */
  public MIMETypedStream viewDublinCore() throws ServerException;

}