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
   * <p>Returns the key metadata from the object.  The data
   * is returned as XML encoded to the aaaa.xsd schema.</p>
   *
   */
  public MIMETypedStream getObjectProfile() throws ServerException;

  /**
   * <p>Returns the key metadata from the object.  the data
   * is returned as HTML in a presentation-oriented format.</p>
   *
   */
  public MIMETypedStream viewObjectInfo() throws ServerException;

  /**
   * <p>Returns the list of items (datastreams) in the object.
   * The item list information is returned as XML encoded to
   * the bbb.xsd schema.</p>
   */
  public MIMETypedStream getItemList() throws ServerException;

  /**
   * <p>Returns the list of items (datastreams) in the object.
   * The item list information is returned as HTML in
   * a presentation-oriented format.</p>
   */
  public MIMETypedStream viewItemList() throws ServerException;

  /**
   * <p>Returns an particular item (datastream) in the object.
   * The item will by a mime-typed stream of bytes.</p>
   *
   * @param  itemID  : the unique identifier for the item in
   * the object in the form of DatastreamID+VersionID.  The
   * item identifer can be discovered via the results of the
   * getItemList or viewItemList methods.
   */
  public MIMETypedStream getItem(String itemID) throws ServerException;

  /**
   * <p>Returns the Dublin Core record for the object, if one exists.
   * The record is returned as XML encoded to the oaidc.xsd schema.</p>
   *
   */
  public MIMETypedStream getDublinCore() throws ServerException;

  /**
   * <p>Returns the Dublin Core record for the object, if one exists.
   * The record is returned as HTML in a presentation-oriented format.</p>
   *
   */
  public MIMETypedStream viewDublinCore() throws ServerException;

}