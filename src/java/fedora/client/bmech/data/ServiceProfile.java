package fedora.client.bmech.data;

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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class ServiceProfile
{
	public String serviceName = null;
	public String serviceLabel = null;
	public String serviceTestURL = null;

	public String transProtocol = null;
	public String msgProtocol = null;
	public String[] inputMIMETypes = new String[0];
	public String[] outputMIMETypes = new String[0];
	public ServiceSoftware[] software = new ServiceSoftware[0];

  public ServiceProfile()
  {
  }
}