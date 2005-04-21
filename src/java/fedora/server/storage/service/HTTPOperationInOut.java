package fedora.server.storage.service;

/**
 *
 * <p><b>Title:</b> HTTPOperationInOut.java</p>
 * <p><b>Description:</b> A data structure for holding input and output
 * specification for WSDL HTTP operation binding.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class HTTPOperationInOut
{

  public static final String MIME_BINDING_TYPE = "MIME";
  public static final String URL_REPLACE_BINDING_TYPE = "URL_REPLACE";

  /**
   * ioBindingType:
   *
   * At this time, Fedora's WSDLParser can deal with:
   * 1) mime:content  (ioBindingType = MIME_BINDING_TYPE)
   * 2) mime:mimeXml  (ioBindingType = MIME_BINDING_TYPE)
   * 3) http:urlReplacement  (ioBindingType = URL_REPLACE_BINDING_TYPE)
   *
   * Not supported by Fedora's WSDLParser at this time are:
   * 1) mime:multipartRelated
   * 2) http:urlEncoded
   */
  public String ioBindingType = null;

  /**
   * ioMIMEContent:  Applies only when ioBindingType is MIME_BINDING_TYPE.
   * Defines the MIME type(s) of the content that is used
   * as input or output to an operation.  Multiple MIME types in the array
   * indicate alternative formats.  May may carry the name of the Message part
   * that it pertains to, although this can typically be inferred.
   *
   * NOTE:  When ioBindingType = URL_REPLACE_BINDING_TYPE
   *        this will be set to an array of zero length.
   *
   */
  public MIMEContent[] ioMIMEContent;

}