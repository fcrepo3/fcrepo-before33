package fedora.server.access;

import java.util.Calendar;
import java.util.Vector;
import fedora.server.storage.types.MIMETypedStream;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */

public interface FedoraAccess
{
  /** API-A Object Reflection Methods
   *
   */

   /**
    *
    * @param PID
    * @return
    */
   public String[] GetBehaviorDefinitions( String PID, Calendar asOfDate );

   /**
    *
    * @param bDefPID
    * @return
    */
   public MIMETypedStream GetBehaviorMethods( String PID, String bDefPID, Calendar asOfDate );

   /** API-A Dissemination Method
    *
    */

    /**
     *
     * @param PID
     * @param bDefPID
     * @param method
     * @param asOfDate
     * @return
     */
    public MIMETypedStream GetDissemination ( String PID, String bDefPID,
        String method,
        Vector userParms, Calendar asOfDate );

}