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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.NoSuchMethodException;

/**
 *
 * @author 	Bill Niebel

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


	/** guard against multiple instantiation of this class */
	private static final ILowlevelStorage permanentStore;
	static {
		ILowlevelStorage temp = null;
		try {
			temp = new FileSystemLowlevelStorage("objectPaths",conf.getPermanentStoreRegistryClass(),conf.getPermanentStoreBase(), conf.getPermanentStoreBases());
		} catch (Exception e) {
			System.err.println("exception making FileSystemLowlevelStorage: " + e.getMessage());
		} finally {
			permanentStore = temp;
		}
	}

	/** guard against multiple instantiation of this class */
	private static final ILowlevelStorage tempStore;
	static {
		ILowlevelStorage temp = null;
		try {
			temp = new FileSystemLowlevelStorage("tempPaths",conf.getTempStoreRegistryClass(),conf.getTempStoreBase(), conf.getTempStoreBases());
		} catch (Exception e) {
			System.err.println("exception making FileSystemLowlevelStorage: " + e.getMessage());
		} finally {
			tempStore = temp;
		}
	}

	/** Path algorithm subfunctionality, loaded as per configuration data */
	private final IPathAlgorithm pathAlgorithm;

	/** Path registry subfunctionality, loaded as per configuration data */
	private final IPathRegistry pathRegistry;

	/** File system interface subfunctionality, loaded as per configuration data */
	private final IFileSystem fileSystem;

	private final String storeBase;
	private final String[] storeBases;
	private final String registryName;
	private final String registryClass;
	//private final String separator;
	/** load subfunctionality for path algorithm, path registry, and file system interface */
	private FileSystemLowlevelStorage(String registryName, String registryClass, String storeBase, String[] storeBases) throws ClassNotFoundException, InstantiationException, IllegalAccessException, LowlevelStorageException {
		this.registryName = registryName;
		this.registryClass = registryClass;
		this.storeBase = storeBase;
		this.storeBases = storeBases;

		ClassLoader loader = getClass().getClassLoader();

		{
			IPathAlgorithm temp = null;
			try {
//System.out.println("algorithm class will be " + conf.getAlgorithmClass());
				Class cclass = loader.loadClass(conf.getAlgorithmClass());
//System.out.println("algorithm a, class is null?: " + (cclass == null));
				Object[] parameters = new Object[] {storeBase};
//System.out.println("algorithm b, storeBase type: " + storeBase.getClass());
				Class[] parameterTypes = new Class[] {storeBase.getClass()};
//System.out.println("algorithm c " + parameterTypes.length);
//Constructor constructors[] = cclass.getConstructors();
//System.out.println("constructors " + constructors.length);
//Constructor c = constructors[0];
//System.out.println("name = " + c.getName());
//Class[] cls = c.getParameterTypes();
//for (int i = 0; i < cls.length; i++) {
	//System.out.println(cls[i]);
//}

				Constructor constructor = cclass.getConstructor(parameterTypes); //<<==== trouble spot
//System.out.println("algorithm d");
				temp = (IPathAlgorithm) constructor.newInstance(parameters);
//System.out.println("assigned to temp algorithm (all the way, w/o throwing");
			} catch (InvocationTargetException e0) {
System.out.println("***target exception making algorithm");
			} catch (NoSuchMethodException e0a) {
System.out.println("***no method exception making algorithm");
			}
			pathAlgorithm = temp;
//System.out.println("assigned algorithm is null? " + (pathAlgorithm == null));
		}

		{
			IPathRegistry temp = null;
			try {
				//System.out.println("making registry 1");
				Class cclass = loader.loadClass(registryClass);
				//System.out.println("making registry 2");
				Object[] parameters = new Object[] {registryName, storeBases};
				//System.out.println("making registry 3");
				Class[] parameterTypes = new Class[] {registryName.getClass(), storeBases.getClass()};
				//System.out.println("making registry 4");
//Constructor constructors[] = cclass.getConstructors();
				Constructor constructor = cclass.getConstructor(parameterTypes); //<<== fails
				//System.out.println("making registry 5");
				temp = (IPathRegistry) constructor.newInstance(parameters);
			} catch (InvocationTargetException e0) {
				System.out.println("***target exception - path registry");
			} catch (NoSuchMethodException e0a) {
				System.out.println("***no such method - path registry");
			}
			pathRegistry = temp;
			//System.out.println("path registry, is null " + (pathRegistry == null));
		}

		{
			IFileSystem temp = null;
			//try {
				Class cclass = loader.loadClass(conf.getFileSystemClass());
				//Object[] parameters = new Object[] {storeBases};
				//Class[] parameterTypes = new Class[] {storeBases.getClass()};
//Constructor constructors[] = cclass.getConstructors();
				//Constructor constructor = cclass.getConstructor(parameterTypes);
				temp = (IFileSystem) /*constructor*/ cclass.newInstance(/*parameters*/);
				/*
			} catch (InvocationTargetException e0) {
				System.out.println("target exception - file system");
			} catch (NoSuchMethodException e0a) {
				System.out.println("no such method - file system");
			}
			*/
			fileSystem = temp;
			//System.out.println("file system, is null " + (fileSystem == null));
		}

	}
	/*

	Method getDeclaredMethod(String name, Class[] parameterTypes)
          Returns a Method object that reflects the specified declared method of the class or interface represented by this Class object.
	java.lang.reflect
Class Method
	invoke
public Object invoke(Object obj,
                     Object[] args)
              throws IllegalAccessException,
                     IllegalArgumentException,
                     InvocationTargetException

	*/

	/** instantiate with this method */
	public static final ILowlevelStorage getPermanentStore() {
		return permanentStore;
	}

	/** instantiate with this method */
	public static final ILowlevelStorage getTempStore() {
		return tempStore;
	}


	/**  */
	private static void log(Exception exception) {
		System.err.println(exception.getMessage());
	}

	/** compares a. path registry with OS files; and b. OS files with registry */
	public void audit () throws LowlevelStorageException {
		//System.out.println("audit 1:" + (pathRegistry == null));
		pathRegistry.auditFiles(/*storeBases*/);
		//System.out.println("audit 2");
		pathRegistry.auditRegistry();
		//System.out.println("audit 3");
	}

	/** recreates path registry from OS files */
	public void rebuild () throws LowlevelStorageException {
		pathRegistry.rebuild(/*storeBases*/);
	}

	/** add to lowlevel store content of Fedora object not already in lowlevel store */
	public final void add(String pid, InputStream content) throws LowlevelStorageException, ObjectAlreadyInLowlevelStorageException {
//System.out.println("lls.add " + pid);
		String filePath;
		File file = null;
		try { //check that object is not already in store
//System.out.println("about to check registry");
			filePath = pathRegistry.get(pid);
//System.out.println("back from checking registry");
			ObjectAlreadyInLowlevelStorageException already = new ObjectAlreadyInLowlevelStorageException("" + pid);
//System.out.println("already");
			log(already);
			throw already;
		} catch (ObjectNotInLowlevelStorageException not) {
			// OK:  keep going
		}
//System.out.println("about to run algorithm, algorithm is null? " + (pathAlgorithm == null));
		filePath = pathAlgorithm.get(pid);
//System.out.println("back from running algorithm");
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
//System.out.println("about to write");
		fileSystem.write(file,content);
//System.out.println("about to update registry");
		pathRegistry.put(pid,filePath);
//System.out.println("back from registry update");
	}

	/** replace into low-level store content of Fedora object already in lowlevel store */
	public final void replace(String pid, InputStream content) throws LowlevelStorageException, ObjectNotInLowlevelStorageException {
		String filePath;
		File file = null;
//System.out.println(">>>in lls.rereplace");
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
//System.out.println(">>>before fs.rewrite " + file + " " + content);
		fileSystem.rewrite(file,content);
//System.out.println(">>>after fs.rewrite ");
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
