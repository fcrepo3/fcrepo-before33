package fedora.server.storage.lowlevel;
import java.io.File;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Enumeration;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.LowlevelStorageInconsistencyException;
import fedora.server.errors.ObjectNotInLowlevelStorageException;

/**
 *
 * <p><b>Title:</b> SimplePathRegistry.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
class SimplePathRegistry extends PathRegistry implements IPathRegistry {
	private Hashtable hashtable = null;


	/** encapsulates all configuration data for this package */
	private static final Configuration conf = Configuration.getInstance();

	public SimplePathRegistry(String registryName, String[] storeBases) throws LowlevelStorageException {
		super(registryName, storeBases);
		rebuild(); //<<<===!!!
	}

	/*
	public void init () throws LowlevelStorageException {
		//super.init();
		rebuild();
	}
	*/

	public String get (String pid)  throws LowlevelStorageException, ObjectNotInLowlevelStorageException {
		String result;
		try {
			result = (String) hashtable.get(pid);
		} catch (Exception e) {
			throw new LowlevelStorageException(true,"SimplePathRegistry.get(" + pid + ")", e); //<<========
		}
		if ((null == result) || (0 == result.length())) {
			throw new ObjectNotInLowlevelStorageException("SimplePathRegistry.get(" + pid + "): object not found");
		}
		return result;
	}

	public void put (String pid, String path)  throws LowlevelStorageException {
		try {
			hashtable.put(pid,path);
		} catch (Exception e) {
			throw new LowlevelStorageException(true,"SimplePathRegistry.put(" + pid + ")", e);
		}
	}

	public void remove (String pid) throws LowlevelStorageException, ObjectNotInLowlevelStorageException {
		String result = (String) hashtable.get(pid);
		try {
			hashtable.remove(pid);
		} catch (Exception e) {
			throw new LowlevelStorageException(true,"SimplePathRegistry.remove(" + pid + ")", e); // <<===
		}
	}

	public void auditFiles (/*String[] storeBases*/) throws LowlevelStorageException {
		System.err.println("\nbegin audit:  files-against-registry");
		traverseFiles(storeBases, AUDIT_FILES, false, FULL_REPORT);
		System.err.println("end audit:  files-against-registry (ending normally)");
	}

	protected Enumeration keys() throws LowlevelStorageException, LowlevelStorageInconsistencyException {
		return hashtable.keys();
	}

	public void rebuild (/*String[] storeBases*/) throws LowlevelStorageException {
		int report = FULL_REPORT;
		Hashtable temp = this.hashtable;
		this.hashtable = new Hashtable();
		try {
			System.err.println("\nbegin rebuilding registry from files");
			traverseFiles(storeBases, REBUILD, false, report); // allows bad files
			System.err.println("end rebuilding registry from files (ending normally)");
		} catch (Exception e) {
			this.hashtable = temp;
			if (report != NO_REPORT) {
				System.err.println("ending rebuild unsuccessfully: " + e.getMessage());
			}
			throw new LowlevelStorageException(true, "ending rebuild unsuccessfully", e); //<<====
		}
	}
}
