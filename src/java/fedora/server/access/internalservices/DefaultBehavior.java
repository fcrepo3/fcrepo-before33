package fedora.server.access.internalservices;

import java.util.Calendar;
import java.util.List;

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.storage.types.Property;

/**
 * <p>Title: DefaultBehavior.java</p>
 * <p>Description: Defines the methods of the default behavior definition
 * that is associated with every Fedora Object.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */
public interface DefaultBehavior
{

 /**
  * <p>Returns the object profile which contains key metadata from the object,
  * plus URLs for the object's Dissemination Index and Item Index.  The data
  * is returned as XML encoded to the objectprofile.xsd schema.</p>
  * @return
  * @throws ServerException
  */
  public MIMETypedStream getObjectProfile() throws ServerException;

  /**
   * <p>Returns an HTML rendering of the object profile which contains
   * key metadata from the object, plus URLs for the object's Dissemination
   * Index and Item Index.  The data is returned as HTML in a
   * presentation-oriented format.  This is accomplished by doing an XSLT
   * transform on the object profile XML that is obtained from getObjectProfile.</p>
   * @return
   * @throws ServerException
   */
  public MIMETypedStream viewObjectProfile() throws ServerException;

  /**
   * <p>Returns the Dissemination Index for the object.  The Dissemination
   * Index is a list of method definitions that represent all disseminations
   * possible on the object.  The Dissemination Index is returned as XML
   * encoded to the objectmethods.xsd schema.</p>
   * @return
   * @throws ServerException
   */
  public MIMETypedStream getMethodIndex() throws ServerException;

  /**
   * <p>Returns an HTML rendering of the Dissemination Index for the object.
   * The Dissemination Index is a list of method definitions that represent
   * all disseminations possible on the object.  The Dissemination Index
   * is returned as HTML in a presentation-oriented format.  This is
   * accomplished by doing an XSLT transform on the XML that is
   * obtained from getMethodIndex.</p>
   * @return
   * @throws ServerException
   */
  public MIMETypedStream viewMethodIndex() throws ServerException;

  /**
   * <p>Returns the Item Index for the object.  The Item
   * Index is a list of all datastreams in the object.  The datastream items
   * can be data or metadata.  The Item Index is returned as XML
   * encoded to the objectitems.xsd schema.</p>
   * @return
   * @throws ServerException
   */
  public MIMETypedStream getItemIndex() throws ServerException;

  /**
   * <p>Returns an HTML rendering of the Item Index for the object.  The Item
   * Index is a list of all datastreams in the object.  The datastream items
   * can be data or metadata.  The Item Index is returned as HTML in a
   * presentation-oriented format.  This is accomplished by doing an XSLT
   * transform on the XML that is obtained from getItemIndex.</p>
   * @return
   * @throws ServerException
   */
  public MIMETypedStream viewItemIndex() throws ServerException;

  /**
   * <p>Returns an particular item (datastream) in the object.
   * The item will by a mime-typed stream of bytes.</p>
   *
   * @param  itemID  the unique identifier for the item in
   * the object in the form of a DatastreamID.  The
   * item identifer can be discovered via the results of the
   * getItemIndex or viewItemIndex methods.
   * @return
   * @throws ServerException
   */
  public MIMETypedStream getItem(String itemID) throws ServerException;

  /**
   * <p>Returns the Dublin Core record for the object, if one exists.
   * The record is returned as XML encoded to the oaidc.xsd schema.</p>
   * @return
   * @throws ServerException
   */
  public MIMETypedStream getDublinCore() throws ServerException;

  /**
   * <p>Returns the Dublin Core record for the object, if one exists.
   * The record is returned as HTML in a presentation-oriented format.</p>
   * @return
   * @throws ServerException
   */
  public MIMETypedStream viewDublinCore() throws ServerException;

}