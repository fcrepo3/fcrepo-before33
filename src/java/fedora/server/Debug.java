package fedora.server;

/**
 *
 * <p><b>Title:</b> Debug.java</p>
 * <p><b>Description:</b> Provides static boolean that can be checked
 * for conditional debugging.  The default value is false.  Normally
 * the value is affected by the running server instance's fedora.fcfg file
 * "debug" parameter.<br/>
 * <b>Example Use:<b><br/>
 * <pre>
 * if (fedora.server.Debug.DEBUG) System.out.println("Something");
 * </pre></p>
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
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public abstract class Debug {

    public static boolean DEBUG = false;

}
