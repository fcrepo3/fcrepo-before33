package fedora.server.storage.lowlevel;
import java.io.InputStream;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.ObjectAlreadyInLowlevelStorageException;
import fedora.server.errors.ObjectNotInLowlevelStorageException;

/**
 *
 * <p><b>Title:</b> ILowlevelStorage.java</p>
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
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author wdn5e@virginia.edu
 * @version 1.0
 */
public interface ILowlevelStorage {
	public void add(String pid, InputStream content) throws LowlevelStorageException, ObjectAlreadyInLowlevelStorageException;
	public void replace(String pid, InputStream content) throws LowlevelStorageException, ObjectNotInLowlevelStorageException;
	public InputStream retrieve(String pid) throws LowlevelStorageException, ObjectNotInLowlevelStorageException;
	public void remove(String pid) throws LowlevelStorageException, ObjectNotInLowlevelStorageException;
	public void rebuild () throws LowlevelStorageException;
	public void audit () throws LowlevelStorageException;
}
