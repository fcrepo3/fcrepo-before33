package fedora.server.errors;

/**
 * <p>Title: ReplicationException.java</p>
 * <p>Description: Exception class for replication errors.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Paul Charlton
 * @version 1.0
 */

public class ReplicationException extends Exception
{

  /**
   * Creates a ReplicationException.
   *
   * @param msg Description of the exception.
   *
   */
  public ReplicationException(String msg) {
         super(msg);
  }
}
