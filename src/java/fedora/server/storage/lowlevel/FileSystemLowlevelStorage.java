//synch issues
//assume that there might be a registry rebuild process which might erroneously add 
//entries from orphaned files

//check existing low-level in file model, cp w/ properties
package fedora.server.storage.lowlevel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;
import fedora.server.Server;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.ObjectAlreadyInLowlevelStorageException;
import fedora.server.errors.ObjectNotInLowlevelStorageException;
import fedora.server.errors.InitializationException;
import java.io.ByteArrayInputStream; // for testing in main()
import java.io.InputStream; // for testing in main()

/** 
	Methods at this level are non-specific to implementation of path algorithm, path registry, and interface to
	operating system i/o; and provide independence from each other of these three implementations.
	File objects passed to IFileSystem methods are to the files actually holding Fedora objects; any other
	temporary File objects needed by an IFileSystem implementation will be instantiated in that implementation.
	The general Exceptions are in the hierarchy:
		Exception
			LowlevelStorageException
				ObjectAlreadyInStoreException
				ObjectNotInStoreException
*/
class FileSystemLowlevelStorage implements ILowlevelStorage {

	//These three included objects provide configurable implementations of subfunctionality
	private final IPathRegistry pathRegistry;
	private final IPathAlgorithm pathAlgorithm;
	private final IFileSystem fileSystem;
	
	private static final Configuration configuration = Configuration.getInstance();
	
	private FileSystemLowlevelStorage() throws ClassNotFoundException, InstantiationException, IllegalAccessException, LowlevelStorageException {
		String registryClass = configuration.getRegistryClass();
		String algorithmClass = "fedora.server.storage.lowlevel.TimestampPathAlgorithm";
		String fileSystemClass = "fedora.server.storage.lowlevel.GenericFileSystem";
		Class myClass = getClass();
		ClassLoader loader = myClass.getClassLoader();
		pathRegistry = (IPathRegistry) loader.loadClass(registryClass).newInstance();
		pathAlgorithm = (IPathAlgorithm) loader.loadClass(algorithmClass).newInstance();			
		fileSystem = (IFileSystem) loader.loadClass(fileSystemClass).newInstance();
	}
	
	public void rebuild () throws LowlevelStorageException {
		pathRegistry.rebuild();
	}
	
	public void audit () throws LowlevelStorageException {
		pathRegistry.auditFiles();
		pathRegistry.auditRegistry();
	}
	
	/** log file should be on separate volume
	*/
	static void log(Exception exception) {
		System.err.println(exception.getMessage());
	}
	
	private static void staticLog(String string) {
		System.err.println(string);
	}
	
	private static final boolean stringNull(String string) {
		return (null == string) || (string.equals(""));
	}

	private static final ILowlevelStorage singleInstance;
	static {
		ILowlevelStorage temp = null;
		try {
			temp = new FileSystemLowlevelStorage();
		} catch (Exception e) {
			staticLog("exception making FileSystemLowlevelStorage: " + e.getMessage());
		} finally {
			singleInstance = temp;				
		}
	}
	public static final ILowlevelStorage getInstance() {
		return singleInstance;
	}

	/** add to lowlevel store content of Fedora object not already in lowlevel store
	*/
	public final void add(String pid, InputStream content) throws LowlevelStorageException, ObjectAlreadyInLowlevelStorageException {
		String filePath;
		try { //check that object is not already in store
			filePath = pathRegistry.get(pid);
			ObjectAlreadyInLowlevelStorageException already = new ObjectAlreadyInLowlevelStorageException("" + pid);
			log(already);
			throw already;
		} catch (ObjectNotInLowlevelStorageException not) {
			// OK:  keep going
		}

		filePath = pathAlgorithm.get(pid, new GregorianCalendar());
		if (stringNull(filePath)) { //guard against algorithm implementation
			LowlevelStorageException nullPath = new LowlevelStorageException(true, "null path from algorithm for pid " + pid);
			log(nullPath);
			throw nullPath;
		}

		File file = null;
		try {
			file = new File(filePath);
		} catch (Exception eFile) { //purposefully general catch-all
			LowlevelStorageException newFile = new LowlevelStorageException(true,"couldn't make File for " + filePath, eFile);
			log(newFile);
			throw newFile;
		}
		fileSystem.write(file,content);
		pathRegistry.put(pid,filePath);
	}

	/** replace into low-level store content of Fedora object already in lowlevel store
	*/
	public final void replace(String pid, InputStream content) throws LowlevelStorageException, ObjectNotInLowlevelStorageException {
		String filePath;
		try {
			filePath = pathRegistry.get(pid);
		} catch (ObjectNotInLowlevelStorageException ffff) {
			LowlevelStorageException noPath = new LowlevelStorageException(false, "pid " + pid + " not in registry", ffff);
			log(noPath);
			throw noPath;
		}
		if (stringNull(filePath)) { //guard against registry implementation
			LowlevelStorageException nullPath = new LowlevelStorageException(true, "pid " + pid + " not in registry");
			log(nullPath);
			throw nullPath;
		}

		File file = null;
		try {
			file = new File(filePath);
		} catch (Exception eFile) { //purposefully general catch-all
			LowlevelStorageException newFile = new LowlevelStorageException(true, "couldn't make new File for " + filePath, eFile);
			log(newFile);
			throw newFile;
		}

		fileSystem.rewrite(file,content);
	}

	/** get content of Fedora object from low-level store
	*/
	public final InputStream retrieve(String pid) throws LowlevelStorageException, ObjectNotInLowlevelStorageException {
		String filePath;
		try {
			filePath = pathRegistry.get(pid);
		} catch (ObjectNotInLowlevelStorageException eReg) {
			log(eReg);
			throw eReg;
		}
		if (stringNull(filePath)) { //guard against registry implementation
			LowlevelStorageException nullPath = new LowlevelStorageException(true, "null path from registry for pid " + pid);
			log(nullPath);
			throw nullPath;
		}			

		File file;
		try {
			file = new File(filePath);
		} catch (Exception eFile) { //purposefully general catch-all
			LowlevelStorageException newFile = new LowlevelStorageException(true, "couldn't make File for " + filePath, eFile);
			log(newFile);
			throw newFile;
		}

		return fileSystem.read(file);
	}

	/** remove Fedora object from low-level store
	*/
	public final void remove(String pid) throws LowlevelStorageException, ObjectNotInLowlevelStorageException {
		String filePath;
		try {
			filePath = pathRegistry.get(pid);
		} catch (ObjectNotInLowlevelStorageException eReg) {
			log(eReg);
			throw eReg;
		}
		if (stringNull(filePath)) { //guard against registry implementation
			LowlevelStorageException nullPath = new LowlevelStorageException(true, "null path from registry for pid " + pid);
			log(nullPath);
			throw nullPath;
		}

		File file = null;
		try {
			file = new File(filePath);
		} catch (Exception eFile) { //purposefully general catch-all
			LowlevelStorageException newFile = new LowlevelStorageException(true, "couldn't make File for " + filePath, eFile);
			log(newFile);
			throw newFile;
		}
		pathRegistry.remove(pid);
		fileSystem.delete(file);
	}

}
