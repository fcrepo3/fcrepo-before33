package fedora.server.storage.lowlevel;
import fedora.server.errors.LowlevelStorageException;
interface IPathAlgorithm {
	public String get (String pid) throws LowlevelStorageException;
}
