package fedora.server.storage.lowlevel;
import java.io.File;
import fedora.server.Server;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.InitializationException;
class Configuration {
	private static Server s_server;
	private final String separator;
	private final String storeBase;
	private final String[] storeBases;
	private final String registryClass;
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
		String registryClassTemp = s_server.getParameter("pid_registry");
		if (registryClassTemp == null) {
			throw new LowlevelStorageException(true,"must configure pid_registry_class");
		}
		registryClass = registryClassTemp;
		boolean backslashIsEscape; {
			String backslashIsEscapeString = s_server.getParameter("backslash_is_escape");
			if (backslashIsEscapeString == null) {
				throw new LowlevelStorageException(true,"must configure backslash_is_escape");
			}
			backslashIsEscapeString = backslashIsEscapeString.toUpperCase();
			if (! (backslashIsEscapeString.equals("YES") || backslashIsEscapeString.equals("NO")) ) {
				throw new LowlevelStorageException(true,"must configure backslash_is_escape as yes/no");
			}
			backslashIsEscape = backslashIsEscapeString.equals("YES");
		}
		String storeBaseTemp = s_server.getParameter("store_base");
		if (storeBaseTemp == null) {
			throw new LowlevelStorageException(true,"must configure store_base");
		}
		if (! backslashIsEscape) {
			storeBase = storeBaseTemp;
			separator = File.separator;
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
		storeBases = new String[] {storeBase};
	}

	public final String getSeparator() {
		return separator;
	}
	public final String getStoreBase() {
		return storeBase;
	}
	public final String[] getStoreBases() {
		return storeBases;
	}
	public final String getRegistryClass() {
		return registryClass;
	}
}
