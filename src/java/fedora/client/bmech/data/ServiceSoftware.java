package fedora.client.bmech.data;

/**
 *
 * <p><b>Title:</b> ServiceDependency</p>
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
public class ServiceSoftware {

	public static final String SW_PROGLANG = "Programming_Language";
	public static final String SW_OS = "Operating_System";
	public static final String SW_UTILITY = "Utility_Program";
	public static final String SW_APPLIC = "Application";
	public static final String SW_OTHER = "OTHER";
	
	public static final String L_COM = "Commercial";
	public static final String L_GPL = "GNU-GPL"; 
	public static final String L_LGPL = "GNU-LGPL";
	public static final String L_MPL = "MPL";
	public static final String L_BSD = "BSD"; 
	public static final String L_CPL = "Common Public License";
	public static final String L_OTHER = "OTHER";
	
	public static final boolean YES = true;
	public static final boolean NO = false;
	
    public String swName = null;
    public String swVersion = null;
    public String swType = null;
    public String swLicenceType = null;
    public boolean isOpenSource = false;

    public ServiceSoftware()
    {

    }
}