package fedora.server.errors;

/**
 * Signals a problem during replication.
 *
 * @author Paul Charlton
 * @version 1.0
 */
public class ReplicationException extends ServerException
{

  /**
   * Creates a ReplicationException.
   *
   * @param msg Description of the exception.
   *
   */
  public ReplicationException(String msg) {
         super(null, msg, null, null, null);
  }
}
