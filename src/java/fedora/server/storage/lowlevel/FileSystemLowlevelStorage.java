package fedora.server.storage.lowlevel;
import java.io.File;
import java.io.InputStream;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.ObjectAlreadyInLowlevelStorageException;
import fedora.server.errors.ObjectNotInLowlevelStorageException;
import java.lang.reflect.Constructor;

/**
 *
 * <p><b>Title:</b> FileSystemLowlevelStorage.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public class FileSystemLowlevelStorage implements ILowlevelStorage {
/*
	Methods at this level are non-specific to implementation of a. path algorithm, b. path registry, and
	c. interface to operating system i/o; and provide independence from each other of these three implementations.
	File objects passed to IFileSystem methods are to the files actually holding Fedora objects.
	If an implementation of an IFileSystem methods requires other, temporary File objects, instantiation
	and use will be within that implementation.
*/

	/** encapsulates all configuration data for this package */
	private static final Configuration conf = Configuration.getInstance();

	/* make and allow only 3 instantiations of this class.
	   class purposely unusable if these instantiations fail. */
	private static final ILowlevelStorage objectStore;
	private static final ILowlevelStorage datastreamStore;
	private static final ILowlevelStorage tempStore;
	static {
		ILowlevelStorage tempObjectStore;
		ILowlevelStorage tempDatastreamStore;
		ILowlevelStorage tempTempStore;
		try {
			tempObjectStore = new FileSystemLowlevelStorage(conf.getObjectRegistryTableName(),conf.getRegistryClass(),conf.getObjectStoreBase(), conf.getObjectStoreBases());
			tempDatastreamStore = new FileSystemLowlevelStorage(conf.getDatastreamRegistryTableName(),conf.getRegistryClass(),conf.getDatastreamStoreBase(), conf.getDatastreamStoreBases());
			tempTempStore = new FileSystemLowlevelStorage(conf.getTempRegistryTableName(),conf.getRegistryClass(),conf.getTempStoreBase(), conf.getTempStoreBases());
		} catch (Exception e) {
			tempObjectStore = null;
			tempDatastreamStore = null;
			tempTempStore = null;
		}
		objectStore = tempObjectStore;
		datastreamStore = tempDatastreamStore;
		tempStore = tempTempStore;
	}

	/** Path algorithm subfunctionality, loaded as per configuration data */
	private final IPathAlgorithm pathAlgorithm;

	/** Path registry subfunctionality, loaded as per configuration data */
	private final IPathRegistry pathRegistry;

	/** File system interface subfunctionality, loaded as per configuration data */
	private final IFileSystem fileSystem;

	/** load subfunctionality for path algorithm, path registry, and file system interface */
	private FileSystemLowlevelStorage(String registryName, String registryClass, String storeBase, String[] storeBases) throws LowlevelStorageException {

		ClassLoader loader = getClass().getClassLoader();

		IPathAlgorithm tempPathAlgorithm = null;
		IPathRegistry tempPathRegistry = null;
		IFileSystem tempFileSystem = null;

		String reason = "";
		try {
			reason = "path algorithm";
			Class cclass = loader.loadClass(conf.getAlgorithmClass());
			Object[] parameters = new Object[] {storeBase};
			Class[] parameterTypes = new Class[] {storeBase.getClass()};
			Constructor constructor = cclass.getConstructor(parameterTypes);
			tempPathAlgorithm = (IPathAlgorithm) constructor.newInstance(parameters);

			reason = "registry";
			cclass = loader.loadClass(registryClass);
			parameters = new Object[] {registryName, storeBases};
			parameterTypes = new Class[] {registryName.getClass(), storeBases.getClass()};
			constructor = cclass.getConstructor(parameterTypes);
			tempPathRegistry = (IPathRegistry) constructor.newInstance(parameters);

			reason = "file system access";
			cclass = loader.loadClass(conf.getFileSystemClass());
			tempFileSystem = (IFileSystem) cclass.newInstance();

			pathAlgorithm = tempPathAlgorithm;
			pathRegistry = tempPathRegistry;
			fileSystem = tempFileSystem;
		} catch (Exception e) {
			LowlevelStorageException wrapper = new LowlevelStorageException(true, "couldn't set up " + reason + " for " + registryName, e);
			log(wrapper);
			throw wrapper;
		}
	}


	public static final ILowlevelStorage getObjectStore() {
		return objectStore;
	}

	public static final ILowlevelStorage getDatastreamStore() {
		return datastreamStore;
	}

	public static final ILowlevelStorage getTempStore() {
		return tempStore;
	}

	/**
	* @deprecated  keep in place temporarily, so that code using it doesn't break awaiting change
	*/
	public static final ILowlevelStorage getPermanentStore() {
		return getObjectStore();
	}

	/**  */
	private static void log(Exception exception) {
		System.err.println(exception.getMessage());
	}

	/** compares a. path registry with OS files; and b. OS files with registry */
	public void audit () throws LowlevelStorageException {
		pathRegistry.auditFiles();
		pathRegistry.auditRegistry();
	}

	/** recreates path registry from OS files */
	public void rebuild () throws LowlevelStorageException {
		pathRegistry.rebuild();
	}

	/** add to lowlevel store content of Fedora object not already in lowlevel store */
	public final void add(String pid, InputStream content) throws LowlevelStorageException, ObjectAlreadyInLowlevelStorageException {
		String filePath;
		File file = null;
		try { //check that object is not already in store
			filePath = pathRegistry.get(pid);
			ObjectAlreadyInLowlevelStorageException already = new ObjectAlreadyInLowlevelStorageException("" + pid);
			log(already);
			throw already;
		} catch (ObjectNotInLowlevelStorageException not) {
			// OK:  keep going
		}
		filePath = pathAlgorithm.get(pid);
		if (filePath == null || filePath.equals("")) { //guard against algorithm implementation
			LowlevelStorageException nullPath = new LowlevelStorageException(true, "null path from algorithm for pid " + pid);
			log(nullPath);
			throw nullPath;
		}

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

	/** replace into low-level store content of Fedora object already in lowlevel store */
	public final void replace(String pid, InputStream content) throws LowlevelStorageException, ObjectNotInLowlevelStorageException {
		String filePath;
		File file = null;
		try {
			filePath = pathRegistry.get(pid);
		} catch (ObjectNotInLowlevelStorageException ffff) {
			LowlevelStorageException noPath = new LowlevelStorageException(false, "pid " + pid + " not in registry", ffff);
			log(noPath);
			throw noPath;
		}
		if (filePath == null || filePath.equals("")) { //guard against registry implementation
			LowlevelStorageException nullPath = new LowlevelStorageException(true, "pid " + pid + " not in registry");
			log(nullPath);
			throw nullPath;
		}

		try {
			file = new File(filePath);
		} catch (Exception eFile) { //purposefully general catch-all
			LowlevelStorageException newFile = new LowlevelStorageException(true, "couldn't make new File for " + filePath, eFile);
			log(newFile);
			throw newFile;
		}
		fileSystem.rewrite(file,content);
	}

	/** get content of Fedora object from low-level store */
	public final InputStream retrieve(String pid) throws LowlevelStorageException, ObjectNotInLowlevelStorageException {
		String filePath;
		File file;

		try {
			filePath = pathRegistry.get(pid);
		} catch (ObjectNotInLowlevelStorageException eReg) {
			log(eReg);
			throw eReg;
		}

		if (filePath == null || filePath.equals("")) { //guard against registry implementation
			LowlevelStorageException nullPath = new LowlevelStorageException(true, "null path from registry for pid " + pid);
			log(nullPath);
			throw nullPath;
		}

		try {
			file = new File(filePath);
		} catch (Exception eFile) { //purposefully general catch-all
			LowlevelStorageException newFile = new LowlevelStorageException(true, "couldn't make File for " + filePath, eFile);
			log(newFile);
			throw newFile;
		}

		return fileSystem.read(file);
	}

	/** remove Fedora object from low-level store */
	public final void remove(String pid) throws LowlevelStorageException, ObjectNotInLowlevelStorageException {
		String filePath;
		File file = null;

		try {
			filePath = pathRegistry.get(pid);
		} catch (ObjectNotInLowlevelStorageException eReg) {
			log(eReg);
			throw eReg;
		}
		if (filePath == null || filePath.equals("")) { //guard against registry implementation
			LowlevelStorageException nullPath = new LowlevelStorageException(true, "null path from registry for pid " + pid);
			log(nullPath);
			throw nullPath;
		}

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
