package fedora.server.storage.lowlevel;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.File;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.InitializationException;
abstract class PathAlgorithm implements IPathAlgorithm {
	
	//protected static final Configuration configuration = Configuration.getInstance();
	
	protected static final String sep = File.separator;
	
	private final String storeBase;

	protected final String getStoreBase() {
		return storeBase;
	}

	protected PathAlgorithm (String storeBase) {
		this.storeBase = storeBase;
	}
	
	private static final String encode(String unencoded) {
		return unencoded.replace(':','_');
	}

	public static final String decode(String encoded) {
		return encoded.replace('_',':');
	}
	
	abstract protected String format (String pid) throws LowlevelStorageException;
	
	public final String get (String pid) throws LowlevelStorageException {
		return format(encode(pid));
	}

}
