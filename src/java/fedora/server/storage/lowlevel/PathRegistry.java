package fedora.server.storage.lowlevel;
import java.io.File;
import java.util.GregorianCalendar;
import java.io.IOException;
import java.util.Enumeration;
import java.io.File;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.LowlevelStorageInconsistencyException; 
import fedora.server.errors.ObjectNotInLowlevelStorageException;
import fedora.server.errors.InitializationException;
abstract class PathRegistry implements IPathRegistry {
	
	protected static final int NO_REPORT = 0; //<=========????????
	protected static final int ERROR_REPORT = 1;
	protected static final int FULL_REPORT = 2;
	
	protected static final int REPORT_FILES = 0;
	protected static final int AUDIT_FILES = 1;
	protected static final int REBUILD = 2;
	
	protected static final Configuration configuration = Configuration.getInstance();
	
	public void init () throws LowlevelStorageException {
	}

	//private static final IPathAlgorithm pathAlgorithm = new CNullPathAlgorithm();
	public PathRegistry() {
	}
	
	public static final boolean stringNull(String string) {
		return (null == string) || (string.equals(""));
	}

	public abstract String get (String pid)  throws LowlevelStorageException, ObjectNotInLowlevelStorageException;
	
	public abstract void put (String pid, String path)  throws LowlevelStorageException;
	
	public abstract void remove (String pid)  throws LowlevelStorageException, ObjectNotInLowlevelStorageException;
	
	private final void traverseFiles (File[] files, int operation, boolean stopOnError, int report) throws LowlevelStorageException {
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				traverseFiles(files[i].listFiles(), operation, stopOnError, report);
			} else {
				String filename = files[i].getName();
				String path = null;
				try {
					path = files[i].getCanonicalPath();
				} catch (IOException e) {
					if (report != NO_REPORT) {							
						System.err.println("couldn't get File path: " + e.getMessage());
					}
					if (stopOnError) {
						throw new LowlevelStorageException(true, "couldn't get File path", e);
					}
				}
				if (path != null) {
					int j = filename.lastIndexOf(".xml");
					String pid = null;
					if (j >= 0) {
						pid = filename.substring(0,j);
					}
					if (pid == null) {
						if (report != NO_REPORT) {							
							System.err.println("unexpected file at [" + path + "]");
						}
						if (stopOnError) {
							throw new LowlevelStorageException(true,"unexpected file traversing object store at [" + path + "]");
						}
					} else {
						switch (operation) {
							case REPORT_FILES: {
								if (report == FULL_REPORT) {
									System.err.println("file [" + path + "] would have pid [" + pid + "]");
								}
								break;
							}
							case REBUILD: {
								put(pid,path);
								if (report == FULL_REPORT) {
									System.err.println("added to registry: [" + pid + "] ==> [" + path + "]");
								}
								break;
							}
							case AUDIT_FILES: {
								String rpath = null; 
								try {
									rpath = get(pid);
								} catch (LowlevelStorageException e) {
								}
								boolean matches = (rpath.equals(path));
								if ((report == FULL_REPORT) || ! matches) {
									System.err.println((matches ? "" : "ERROR: ") +
									"[" + path + "] " + (matches ? "" : "NOT ") +
									"in registry" + 
									(matches ? "" : 
									("; pid [" + pid + "] instead registered as [" + 
									((rpath == null) ? "[OBJECT NOT IN STORE]" : rpath) +
									"]")));
								}
							}
						}
					}
				}
			}
		}
	}

	public void traverseFiles (String[] storeBases, int operation, boolean stopOnError, int report) throws LowlevelStorageException {
		File files[];
		try {
			files = new File[storeBases.length];
			for (int i = 0; i < storeBases.length; i++) {
				files[i] = new File(storeBases[i]);
			}
		} catch (Exception e) {
			throw new LowlevelStorageException(true,"couldn't rebuild VolatilePathRegistry", e);
		}
		traverseFiles(files, operation, stopOnError, report);
	}
	
	public abstract void rebuild () throws LowlevelStorageException;
	public abstract void auditFiles () throws LowlevelStorageException;
	//public abstract void auditRegistry () throws FOSExecutionException, FOSBadParmException;	
	
	public void auditRegistry () throws LowlevelStorageException {
		System.err.println("\nbegin audit:  registry-against-files");
		Enumeration keys = keys();
		while (keys.hasMoreElements()) {
			String pid = (String) keys.nextElement();
			try {
				String path = get(pid);
				File file = new File(path);
				boolean fileExists = file.exists();
				System.err.println((fileExists ? "" : "ERROR: ") +
					"registry has [" + pid + "] => [" + path + "] " +
					(fileExists ? "and" : "BUT") +
					" file does " + (fileExists ? "" : "NOT") + "exist");
			} catch (LowlevelStorageException e) {
				System.err.println("ERROR: registry has [" + pid + "] => []");
			}
	 	}
		System.err.println("end audit:  registry-against-files (ending normally)");
	}
	
	protected abstract Enumeration keys() throws LowlevelStorageException, LowlevelStorageInconsistencyException;
	
	private static final String getPath(File file) { //<===================
		String temp;
		try {
			temp = file.getCanonicalPath();
		} catch (Exception eCaughtFiles) {
			temp = "";
		}
		return temp;
	}
}
