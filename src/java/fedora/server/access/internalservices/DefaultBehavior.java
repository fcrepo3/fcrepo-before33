package fedora.server.access.internalservices;

import fedora.server.errors.ServerException;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.storage.types.Property;

/**
 * <p><b>Title: </b>DefaultBehavior.java</p>
 * <p><b>Description: </b>Defines the methods of the default behavior definition
 * that is associated with every Fedora Object.</p>
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
   * encoded to the objectMethods.xsd schema.</p>
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
   * encoded to the objectItemIndex.xsd schema.</p>
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