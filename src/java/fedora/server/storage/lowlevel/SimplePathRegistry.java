package fedora.server.storage.lowlevel;
import java.io.File;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Enumeration;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.LowlevelStorageInconsistencyException;
import fedora.server.errors.ObjectNotInLowlevelStorageException;
class SimplePathRegistry extends PathRegistry implements IPathRegistry {
	private Hashtable  hashtable = null;

	public SimplePathRegistry() throws LowlevelStorageException {
		super();
		rebuild();
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

	public void auditFiles () throws LowlevelStorageException {
		System.err.println("\nbegin audit:  files-against-registry");
		traverseFiles(configuration.getStoreBases(), AUDIT_FILES, false, FULL_REPORT);
		System.err.println("end audit:  files-against-registry (ending normally)");
	}
	
	protected Enumeration keys() throws LowlevelStorageException, LowlevelStorageInconsistencyException {
		return hashtable.keys();
	}

	public void rebuild () throws LowlevelStorageException {
		int report = FULL_REPORT;
		Hashtable temp = this.hashtable;
		this.hashtable = new Hashtable();
		try {
			System.err.println("\nbegin rebuilding registry from files");
			traverseFiles(configuration.getStoreBases(), REBUILD, false, report); // allows bad files
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
