package fedora.server.storage.lowlevel;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.File;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.InitializationException;
abstract class PathAlgorithm implements IPathAlgorithm {
	
	protected static final Configuration configuration = Configuration.getInstance();

	public PathAlgorithm () {
	}

	abstract public String get (String pid, GregorianCalendar calendar) throws LowlevelStorageException;

}
