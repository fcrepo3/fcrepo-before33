package fedora.server.storage.lowlevel;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.ObjectNotInLowlevelStorageException;

/**
 *
 * <p><b>Title:</b> IPathRegistry.java</p>
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
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
interface IPathRegistry {
	public String get (String pid) throws LowlevelStorageException, ObjectNotInLowlevelStorageException;
	public void put (String pid, String path) throws LowlevelStorageException;
	public void remove (String pid) throws LowlevelStorageException, ObjectNotInLowlevelStorageException;
	public void rebuild (/*String[] storeBases*/) throws LowlevelStorageException;
	public void auditFiles (/*String[] storeBases*/) throws LowlevelStorageException;
	public void auditRegistry () throws LowlevelStorageException;
}
