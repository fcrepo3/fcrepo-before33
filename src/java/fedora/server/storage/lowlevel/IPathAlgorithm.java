package fedora.server.storage.lowlevel;
import java.util.GregorianCalendar;
import fedora.server.errors.LowlevelStorageException;
interface IPathAlgorithm {
	public String get (String pid, GregorianCalendar calendar) throws LowlevelStorageException;
}
