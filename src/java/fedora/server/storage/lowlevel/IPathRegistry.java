package fedora.server.storage.lowlevel;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.ObjectNotInLowlevelStorageException;
interface IPathRegistry {
	public String get (String pid) throws LowlevelStorageException, ObjectNotInLowlevelStorageException;
	public void put (String pid, String path) throws LowlevelStorageException;
	public void remove (String pid) throws LowlevelStorageException, ObjectNotInLowlevelStorageException;
	public void rebuild () throws LowlevelStorageException;
	public void auditFiles () throws LowlevelStorageException;
	public void auditRegistry () throws LowlevelStorageException;
}
