package fedora.server.storage.service;

/**
 *
 * <p><b>Title:</b> SOAPOperation.java</p>
 * <p><b>Description:</b> A data structure for holding WSDL SOAP binding for
 * an operation.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class SOAPOperation extends AbstractOperation
{
  /**
   * soapAction:  a URI for the soap request
   */
  public String soapAction;

  /**
   * soapActionStyle:  indicates whether the soap messages will be RPC-oriented
   * (message contains parameters and return values) or document-oriented
   * (message contains document or documents).
   *
   * Valid values for soapActionStyle:
   * 1) rpc
   * 2) document
   */
  public String soapActionStyle;

  // FIXIT!  finish up defintion here....

  //public String inputBindingScheme;

  //public String outputBindingScheme;
}