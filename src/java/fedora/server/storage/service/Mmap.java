package fedora.server.storage.service;

import java.util.Hashtable;

/**
 *
 * <p><b>Title:</b> Mmap.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class Mmap
{
    public String mmapName = null;
    //public MethodDef[] fedoraMethodDefs = new MethodDef[0];
    public MmapMethodDef[] mmapMethods = new MmapMethodDef[0];
    public Hashtable wsdlOperationToMethodDef;

    //public Hashtable wsdlOperationToDSInputKeys;
    //public MmapMethod[] mmapMethods = new MmapMethod[0];
    //public Hashtable wsdlMsgToMethodDef;
}