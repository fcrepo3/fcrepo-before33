package fedora.server.storage.lowlevel;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.LowlevelStorageInconsistencyException;
import fedora.server.errors.ObjectNotInLowlevelStorageException;

/**
 *
 * <p><b>Title:</b> PathRegistry.java</p>
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
abstract class PathRegistry implements IPathRegistry {

	protected static final int NO_REPORT = 0; //<=========????????
	protected static final int ERROR_REPORT = 1;
	protected static final int FULL_REPORT = 2;

	protected static final int REPORT_FILES = 0;
	protected static final int AUDIT_FILES = 1;
	protected static final int REBUILD = 2;

	//protected static final Configuration configuration = Configuration.getInstance();

	public void init () throws LowlevelStorageException {
	}

	protected final String registryName;
	protected final String[] storeBases;
	//private static final IPathAlgorithm pathAlgorithm = new CNullPathAlgorithm();
	public PathRegistry(String registryName, String storeBases[]) {
		this.registryName = registryName;
		this.storeBases = storeBases;
	}

	protected final String getRegistryName() {
		return registryName;
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
					/* drop .xml file suffix; code was:
					int j = filename.lastIndexOf(".xml");
					String pid = null;
					if (j >= 0) {
						pid = PathAlgorithm.decode(filename.substring(0,j));
					}
					*/
					String pid = PathAlgorithm.decode(filename);
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

	public abstract void rebuild (/*String[] storeBases*/) throws LowlevelStorageException;
	public abstract void auditFiles (/*String[] storeBases*/) throws LowlevelStorageException;
	//public abstract void auditRegistry () throws FOSExecutionException, FOSBadParmException;

	public void auditRegistry () throws LowlevelStorageException {
		System.err.println("\nbegin audit:  registry-against-files");
		//System.err.println("aR0");
		Enumeration keys = keys();
		//System.err.println("aR1");
		while (keys.hasMoreElements()) {
		//System.err.println("aR2");
			String pid = (String) keys.nextElement();
		//System.err.println("aR3");
			try {
				String path = get(pid);
		//System.err.println("aR4");
				File file = new File(path);
		//System.err.println("aR5");
				boolean fileExists = file.exists();
		//System.err.println("aR6");
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
