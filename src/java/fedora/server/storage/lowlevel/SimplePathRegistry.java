package fedora.server.storage.lowlevel;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.ObjectNotInLowlevelStorageException;

/**
 *
 * <p><b>Title:</b> SimplePathRegistry.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
class SimplePathRegistry extends PathRegistry {
	private Hashtable hashtable = null;

	public SimplePathRegistry(Map configuration) throws LowlevelStorageException {
		super(configuration);
		rebuild();
	}

	public String get(String pid) throws LowlevelStorageException {
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

	public void put(String pid, String path) throws LowlevelStorageException {
		try {
			hashtable.put(pid,path);
		} catch (Exception e) {
			throw new LowlevelStorageException(true,"SimplePathRegistry.put(" + pid + ")", e);
		}
	}

	public void remove(String pid) throws LowlevelStorageException {
		try {
			hashtable.remove(pid);
		} catch (Exception e) {
			throw new LowlevelStorageException(true,"SimplePathRegistry.remove(" + pid + ")", e); // <<===
		}
	}

	public void auditFiles() throws LowlevelStorageException {
		System.err.println("\nbegin audit:  files-against-registry");
		traverseFiles(storeBases, AUDIT_FILES, false, FULL_REPORT);
		System.err.println("end audit:  files-against-registry (ending normally)");
	}

	public void rebuild() throws LowlevelStorageException {
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
	
	protected Enumeration keys() throws LowlevelStorageException {
		return hashtable.keys();
	}
}
