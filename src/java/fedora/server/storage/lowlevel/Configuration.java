package fedora.server.storage.lowlevel;
import java.io.File;
import fedora.server.Server;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.InitializationException;
class Configuration {
	private static Server s_server;
	private final boolean backslashIsEscape;
	private final String separator;
	private final String permanentStoreBase;
	private final String[] permanentStoreBases;
	private final String tempStoreBase;
	private final String[] tempStoreBases;
	//private final boolean useSingleRegistry;
	private final String algorithmClass;
	private final String permanentStoreRegistryClass;
	private final String tempStoreRegistryClass;
	private final String fileSystemClass;
	static {
		 try {
		     s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
		 } catch (InitializationException ie) {
		     System.err.println(ie.getMessage());
		 }
	}

	private static final Configuration singleInstance;
	static {
		Configuration temp = null;
		try {
			temp = new Configuration();
		} catch (LowlevelStorageException e) {
			System.err.println("didn't conf: " + e.getMessage());
		} finally {
			singleInstance = temp;
		}
	}
	
	public static final Configuration getInstance() {
		return singleInstance;
	}
	
	private Configuration () throws LowlevelStorageException {
		
		String algorithmClassTemp = s_server.getParameter("path_algorithm");
		if (algorithmClassTemp == null) {
			throw new LowlevelStorageException(true,"must configure path_algorithm");
		}
		algorithmClass = algorithmClassTemp;

		{
			String registryClassTemp = s_server.getParameter("pid_registry");
			if (registryClassTemp == null) {
				throw new LowlevelStorageException(true,"must configure pid_registry");
			}
			permanentStoreRegistryClass = registryClassTemp;
		}
		
		{
			String registryClassTemp = s_server.getParameter("temp_registry");
			if (registryClassTemp == null) {
				throw new LowlevelStorageException(true,"must configure temp_registry");
			}
			tempStoreRegistryClass = registryClassTemp;
		}
		
		String fileSystemClassTemp = s_server.getParameter("file_system");
		if (fileSystemClassTemp == null) {
			throw new LowlevelStorageException(true,"must configure file_system");
		}
		fileSystemClass = fileSystemClassTemp;
		
		String backslashIsEscapeString = s_server.getParameter("backslash_is_escape");
		if (backslashIsEscapeString == null) {
			throw new LowlevelStorageException(true,"must configure backslash_is_escape");
		}
		backslashIsEscapeString = backslashIsEscapeString.toUpperCase();
		if (! (backslashIsEscapeString.equals("YES") || backslashIsEscapeString.equals("NO")) ) {
			throw new LowlevelStorageException(true,"must configure backslash_is_escape as yes/no");
		}
		backslashIsEscape = backslashIsEscapeString.equals("YES");
		String permanentStoreBaseTemp = s_server.getParameter("store_base");
		String tempStoreBaseTemp = s_server.getParameter("temp_store_base");
		/*
		{
			boolean tempBoolean = false; // allows temp use of single db table for both permanent and temp uses
			String tempString = s_server.getParameter("single_registry");
			if ((tempString != null) && (tempString.equals("YES"))) {
				tempBoolean = true;
			}
			useSingleRegistry = tempBoolean;
		}
		*/
		if (permanentStoreBaseTemp == null) {
			throw new LowlevelStorageException(true,"must configure store_base");
		}
		if (tempStoreBaseTemp == null) {
			throw new LowlevelStorageException(true,"must configure temp_store_base");
		}
        
        // FIXME: thinks c:\temp and c:\temp2 overlap
		if (tempStoreBaseTemp.startsWith(permanentStoreBaseTemp)
		|| permanentStoreBaseTemp.startsWith(tempStoreBaseTemp)) {
			throw new LowlevelStorageException(true,"permanent_store_base and temp_store_base cannot overlap");
		}
		//if (! backslashIsEscape) {
			permanentStoreBase = permanentStoreBaseTemp;
			tempStoreBase = tempStoreBaseTemp;
			separator = File.separator;
		/*
		} else {
			StringBuffer buffer = new StringBuffer();
			String backslash = "\\";
			String escapedBackslash = "\\\\";
			for (int i = 0; i < storeBaseTemp.length(); i++) {
				String s = storeBaseTemp.substring(i,i+1);
				buffer.append(s.equals(backslash) ? escapedBackslash : s);
			}
			storeBase = buffer.toString();
			if (File.separator.equals(backslash)) {
				separator = escapedBackslash;
			} else {
				separator = File.separator;
			}
		}
		*/
		permanentStoreBases = new String[] {permanentStoreBase};
		tempStoreBases = new String[] {tempStoreBase};
	}
/*
	public final String getSeparator() {
		return separator;
	}
	*/
	public final String getPermanentStoreBase() {
		return permanentStoreBase;
	}
	public final String[] getPermanentStoreBases() {
		return permanentStoreBases;
	}
	public final String getTempStoreBase() {
		return tempStoreBase;
	}
	public final String[] getTempStoreBases() {
		return tempStoreBases;
	}
	public final String getAlgorithmClass() {
		return algorithmClass;
	}
	public final String getPermanentStoreRegistryClass() {
		return permanentStoreRegistryClass;
	}
	public final String getTempStoreRegistryClass() {
		return tempStoreRegistryClass;
	}
	public final String getFileSystemClass() {
		return fileSystemClass;
	}
	public final boolean getBackslashIsEscape() {
		return backslashIsEscape;
	}
}
