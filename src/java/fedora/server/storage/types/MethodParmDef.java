package fedora.server.storage.types;

/**
 *
 * <p><b>Title:</b> MethodParmDef.java</p>
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
 * @version 1.0
 */
public class MethodParmDef
{

    public static final String PASS_BY_REF = "URL_REF";
    public static final String PASS_BY_VALUE = "VALUE";

    public static final String DATASTREAM_INPUT = "fedora:datastreamInputType";
    public static final String USER_INPUT = "fedora:userInputType";
    public static final String DEFAULT_INPUT = "fedora:defaultInputType";

    public String parmName = null;
    public String parmType = null;
    public String parmDefaultValue = null;
    public String[] parmDomainValues = new String[0];
    public boolean parmRequired = true;
    public String parmLabel = null;
    public String parmPassBy = null;

    public MethodParmDef()
    {

    }
}